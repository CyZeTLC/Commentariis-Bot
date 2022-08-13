package eu.cyzetlc.commentariis.service.log;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.entities.Embed;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;

@Getter
public class LogHandler {
    private final Logger log = LoggerFactory.getLogger(LogHandler.class.getName());

    private final LinkedHashMap<Long, TextChannel> logChannels = new LinkedHashMap<>();

    public LogHandler() {
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

    public void apply(Guild guild, TextChannel channel) {
        if (this.logChannels.containsKey(guild.getIdLong())) {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "UPDATE settings SET log_channel = ? WHERE guild_id = ?"
            ).addParameters(Arrays.asList(channel.getIdLong(), guild.getIdLong())).executeUpdateAsync();
        } else {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "INSERT INTO settings (guild_id, language, log_channel) VALUES (?,?,?)"
            ).addParameters(Arrays.asList(guild.getIdLong(), "de", channel.getIdLong())).executeUpdateAsync();
        }
        this.logChannels.put(guild.getIdLong(), channel);
    }

    public void log(String title, String log, LogLevel level, long guildId) {
        this.log(title,log,level,guildId,this.log);
    }
    public void log(String title, String log, LogLevel level, long guildId, Logger logger) {
        Color logColor = Color.GRAY;
        String guildName = (guildId != -1 ? (Commentarii.getInstance().getJda().getGuildById(guildId) != null ? "[" + Commentarii.getInstance().getJda().getGuildById(guildId).getName() + "] " : "") : "");

        switch (level.level) {
            case 0 -> {
                logger.info(guildName + log);
                logColor = Color.GREEN;
            }
            case 1 -> {
                logger.warn(guildName + log);
                logColor = Color.ORANGE;
            }
            case 2 -> {
                logger.debug(guildName + log);
                logColor = Color.YELLOW;
            }
            case 3 -> {
                logger.error(guildName + log);
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
                    guild.getDefaultChannel().sendMessageEmbeds(Embed.getEmbed(
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

    public int getLogCountOfGuild(long guildId) {
        CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                "SELECT numeric_id FROM logs WHERE guild_id = ?"
        ).addParameter(guildId).executeQuerySync();
        return rs.size();
    }

    public enum LogLevel {
        INFO(0),
        WARN(1),
        DEBUG(2),
        ERROR(3);


        public final int level;

        LogLevel(int level) {
            this.level = level;
        }
    }
}
