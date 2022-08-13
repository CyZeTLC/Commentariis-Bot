package eu.cyzetlc.commentariis.service.log;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.entities.Embed;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.LinkedHashMap;

@Getter
public class LogHandler {
    private final Logger log = LoggerFactory.getLogger(LogHandler.class.getName());

    private final LinkedHashMap<Long, TextChannel> logChannels = new LinkedHashMap<>();

    public void apply(Guild guild, TextChannel channel) {
        this.logChannels.put(guild.getIdLong(), channel);
    }

    public void log(String title, String log, LogLevel level, long guildId) {
        Color logColor = Color.GRAY;
        String guildName = (guildId != -1 ? (Commentarii.getInstance().getJda().getGuildById(guildId) != null ? "[" + Commentarii.getInstance().getJda().getGuildById(guildId).getName() + "] " : "") : "");

        switch (level.level) {
            case 0 -> {
                this.log.info(guildName + log);
                logColor = Color.GREEN;
            }
            case 1 -> {
                this.log.warn(guildName + log);
                logColor = Color.ORANGE;
            }
            case 2 -> {
                this.log.debug(guildName + log);
                logColor = Color.YELLOW;
            }
            case 3 -> {
                this.log.error(guildName + log);
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
    }

    public static enum LogLevel {
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
