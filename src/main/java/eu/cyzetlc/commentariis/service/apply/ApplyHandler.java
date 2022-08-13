package eu.cyzetlc.commentariis.service.apply;

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
public class ApplyHandler {
    // Creating a new LinkedHashMap with the key being a Long and the value being a TextChannel.
    private final LinkedHashMap<Long, TextChannel> applyChannels = new LinkedHashMap<>();

    public ApplyHandler() {
        try {
            CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                    "SELECT guild_id,apply_channel FROM settings"
            ).executeQuerySync();

            while (rs.next()) {
                Guild guild = Commentarii.getInstance().getJda().awaitReady().getGuildById(rs.getLong("guild_id"));
                if (guild != null) {
                    this.applyChannels.put(guild.getIdLong(), guild.getTextChannelById(rs.getLong("apply_channel")));
                }
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
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
        if (this.applyChannels.containsKey(guild.getIdLong())) {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "UPDATE settings SET apply_channel = ? WHERE guild_id = ?"
            ).addParameters(Arrays.asList(channel.getIdLong(), guild.getIdLong())).executeUpdateAsync();
        }
        this.applyChannels.put(guild.getIdLong(), channel);
    }

    /**
     * It gets the apply channel of a guild
     *
     * @param guildId The ID of the guild you want to get the apply channel of.
     * @return A TextChannel object.
     */
    public TextChannel getApplyChannelOfGuild(long guildId) {
        try {
            CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                    "SELECT apply_channel FROM settings WHERE guild_id = ?"
            ).addParameter(guildId).executeQuerySync();

            while (rs.next()) {
                return Commentarii.getInstance().getJda().getGuildById(guildId).getTextChannelById(rs.getLong("apply_channel"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
