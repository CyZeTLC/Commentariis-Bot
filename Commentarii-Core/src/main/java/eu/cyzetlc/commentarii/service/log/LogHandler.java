package eu.cyzetlc.commentarii.service.log;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.entities.Embed;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;

@Getter
public class LogHandler {
    // It creates a logger for the class.
    private final Logger log = LoggerFactory.getLogger(LogHandler.class.getName());

    // Creating a new LinkedHashMap with the key being a Long and the value being a TextChannel.
    private final LinkedHashMap<Long, TextChannel> logChannels = new LinkedHashMap<>();

    public LogHandler() throws IOException {
        try {
            CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                    "SELECT guild_id,log_channel FROM settings"
            ).executeQuerySync();

            while (rs.next()) {
                Guild guild = Commentarii.getInstance().getJda().awaitReady().getGuildById(rs.getLong("guild_id"));
                if (guild != null) {
                    this.logChannels.put(guild.getIdLong(), guild.getTextChannelById(rs.getLong("log_channel")));
                }
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * It reads the contents of the latest.log file, writes it to a new file with the current date and time as the file
     * name, and then clears the latest.log file
     */
    public static void storeLogs() throws IOException {
        Path path = Path.of("./logs/latest.log");
        FileWriter latestFile = new FileWriter("./logs/latest.log");
        FileWriter logFile = new FileWriter("./logs/" + new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(new Date()) + ".log");
        try {
            String everything = Files.readString(path);
            logFile.write(everything);
            latestFile.write("");
        } finally {
            logFile.close();
            latestFile.close();
        }
    }

    /**
     * It checks if the guild already has a log channel set, if it does, it updates the database, if it doesn't, it inserts
     * a new row into the database
     *
     * @param guild The guild the settings are for
     * @param channel The channel to set as the log channel
     */
    public void apply(Guild guild, TextChannel channel) {
        if (this.logChannels.containsKey(guild.getIdLong())) {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "UPDATE settings SET log_channel = ? WHERE guild_id = ?"
            ).addParameters(Arrays.asList(channel.getIdLong(), guild.getIdLong())).executeUpdateAsync();
        } else {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "INSERT INTO settings (guild_id, language, log_channel, apply_channel, verify_channel, verify_webhook, verify_webhook_url, verify_role) VALUES (?,?,?,?,0,0,'',0)"
            ).addParameters(Arrays.asList(guild.getIdLong(), "de", channel.getIdLong(), channel.getIdLong())).executeUpdateAsync();
        }
        this.logChannels.put(guild.getIdLong(), channel);
    }

    /**
     * This function logs a message to the console and to a file
     *
     * @param title The title of the log.
     * @param log The log to write to.
     * @param level The level of the log.
     * @param guildId The ID of the guild you want to log to.
     */
    public void log(String title, String log, LogLevel level, long guildId) {
        this.log(title,log,level,guildId,this.log);
    }

    /**
     * It logs a message to the console, and if the guild ID is not -1, it will log it to the log channel for that guild
     *
     * @param title The title of the log.
     * @param log The message to log
     * @param level The level of the log. This is used to determine the color of the log.
     * @param guildId The ID of the guild that the log is for. If the log is not for a guild, this should be -1.
     * @param logger The logger you want to use.
     */
    public void log(String title, String log, LogLevel level, long guildId, Logger logger) {
        Color logColor = Color.GRAY;
        String guildName = (guildId != -1 ? (Commentarii.getInstance().getJda().getGuildById(guildId) != null ? "[" + Commentarii.getInstance().getJda().getGuildById(guildId).getName() + "] " : "") : "");

        switch (level.level) {
            case 0 -> {
                logger.info(guildName + log.replaceAll("\n",""));
                logColor = Color.GREEN;
            }
            case 1 -> {
                logger.warn(guildName + log.replaceAll("[^a-zA-Z0-9\\\\s.-]",""));
                logColor = Color.ORANGE;
            }
            case 2 -> {
                logger.debug(guildName + log.replaceAll("[^a-zA-Z0-9\\\\s.-]",""));
                logColor = Color.YELLOW;
            }
            case 3 -> {
                logger.error(guildName + log.replaceAll("[^a-zA-Z0-9\\\\s.-]",""));
                logColor = Color.RED;
            }
            default -> {
            }
        }

        if (guildId != -1) {
            if (this.logChannels.containsKey(guildId)) {
                TextChannel logChannel = this.logChannels.get(guildId);
                logChannel.sendMessageEmbeds(Embed.getEmbed(
                        "**" + title + "**",
                        log,
                        logColor
                ).build()).queue();
            } else if (Commentarii.getInstance().getJda().getGuildById(guildId) != null) {
                Guild guild = Commentarii.getInstance().getJda().getGuildById(guildId);
                if (guild != null && guild.getDefaultChannel() != null) {
                    guild.getDefaultChannel().asTextChannel().sendMessageEmbeds(Embed.getEmbed(
                            "**" + title + "**",
                            log,
                            logColor
                    ).build()).queue();
                }
            }
        }
        Commentarii.getInstance().getQueryHandler().createBuilder(
                "INSERT INTO logs (timestamp, thread, guild_id, text) VALUES (?,?,?,?)"
        ).addParameters(Arrays.asList(System.currentTimeMillis(), logger.getName(), guildId, log)).executeUpdateSync();
    }

    /**
     * Get the number of logs for a guild.
     *
     * @param guildId The ID of the guild you want to get the log count of.
     * @return The number of logs in the database for a specific guild.
     */
    public int getLogCountOfGuild(long guildId) {
        CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                "SELECT numeric_id FROM logs WHERE guild_id = ?"
        ).addParameter(guildId).executeQuerySync();
        return rs.size();
    }

    /**
     * This function returns the log channel of the guild with the given ID.
     *
     * @param guildId The id of the guild you want to get the log channel of.
     * @return A TextChannel object
     */
    public TextChannel getLogChannelOfGuild(long guildId) {
        return this.logChannels.get(guildId);
    }

    public enum LogLevel {
        INFO(0),
        WARN(1),
        DEBUG(2),
        ERROR(3);


        // A variable that is used to determine the color and level of the log.
        public final int level;

        LogLevel(int level) {
            this.level = level;
        }
    }
}
