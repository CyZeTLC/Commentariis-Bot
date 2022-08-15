package eu.cyzetlc.commentarii.service.verify;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.buttons.VerifyButton;
import eu.cyzetlc.commentarii.service.entities.Embed;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.sql.rowset.CachedRowSet;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
public class VerifyHandler {
    // Creating a new LinkedHashMap with the key being a Long and the value being a TextChannel.
    private final LinkedHashMap<Long, TextChannel> verifyChannels = new LinkedHashMap<>();
    // It's creating a new LinkedHashMap with the key being a Long and the value being a TextChannel.
    private final LinkedHashMap<Long, Role> verifyRoles = new LinkedHashMap<>();

    public VerifyHandler() {
        try {
            CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                    "SELECT guild_id,verify_channel FROM settings"
            ).executeQuerySync();

            while (rs.next()) {
                if (rs.getLong("verify_channel") != 0) {
                    Guild guild = Commentarii.getInstance().getJda().awaitReady().getGuildById(rs.getLong("guild_id"));
                    if (guild != null) {
                        this.verifyChannels.put(guild.getIdLong(), guild.getTextChannelById(rs.getLong("verify_channel")));
                    }
                }
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * It sends a private message to the user with the id `userId` and the guild id `guildId`
     *
     * @param userId The user id of the user you want to send the message to
     * @param guildId The ID of the guild the user is in
     */
    public void sendPrivateMessage(String userId, String guildId) {
        try {
            Commentarii.getInstance().getJda().getUserById(userId).openPrivateChannel().submit().thenCompose(privateChannel -> privateChannel.sendMessageEmbeds(Embed.getEmbed(
                    "**Verifiziere dich**",
                    "Klicke auf verifizieren um alle grundlegenden Channel zu sehen.\nBitte bedenke, dass dieses Verfahren einige Sekunden in Anspruch nehemn kann.",
                    Color.GREEN
            ).build()).setActionRow(List.of(Commentarii.getInstance().getButtonHandler().register(new VerifyButton()).withUrl("https://commentarii.cyzetlc.eu/verify/?uid=" + userId + "&guild=" + guildId))).submit()).whenComplete(((message, throwable) -> {
            }));
        } catch (Exception ignored) {
        }
    }

    /**
     * It checks if the guild already has a log channel set, if it does, it updates the database, if it doesn't, it inserts
     * a new row into the database
     *
     * @param guild The guild the settings are for
     * @param channel The channel to set as the verification channel
     */
    public void apply(Guild guild, TextChannel channel) {
        Commentarii.getInstance().getQueryHandler().createBuilder(
                "UPDATE settings SET verify_channel = ? WHERE guild_id = ?"
        ).addParameters(Arrays.asList(channel.getIdLong(), guild.getIdLong())).executeUpdateAsync();
        this.verifyChannels.put(guild.getIdLong(), channel);
    }

    public void apply(Guild guild, Role role) {
        if (this.verifyChannels.containsKey(guild.getIdLong())) {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "UPDATE settings SET verify_role = ? WHERE guild_id = ?"
            ).addParameters(Arrays.asList(role.getIdLong(), guild.getIdLong())).executeUpdateAsync();
        }
        this.verifyRoles.put(guild.getIdLong(), role);
    }

    /**
     * If the guild has a verification channel, update the database with the new webhook ID
     *
     * @param guild The guild the webhook is being set for.
     * @param webhookId The webhook that was created
     */
    public void apply(Guild guild, long webhookId) {
        if (this.verifyChannels.containsKey(guild.getIdLong())) {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "UPDATE settings SET verify_webhook = ? WHERE guild_id = ?"
            ).addParameters(Arrays.asList(webhookId, guild.getIdLong())).executeUpdateAsync();
        }
    }

    /**
     * It updates the database with the new webhook URL
     *
     * @param guild The guild that the webhook is being applied to.
     * @param webhookUrl The webhook URL to use for the verification channel.
     */
    public void apply(Guild guild, String webhookUrl) {
        if (this.verifyChannels.containsKey(guild.getIdLong())) {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "UPDATE settings SET verify_webhook_url = ? WHERE guild_id = ?"
            ).addParameters(Arrays.asList(webhookUrl, guild.getIdLong())).executeUpdateAsync();
        }
    }

    /**
     * It gets the verification channel of a guild
     *
     * @param guildId The ID of the guild you want to get the verification channel of.
     * @return A TextChannel object.
     */
    public TextChannel getVerifyChannel(long guildId) {
        try {
            CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                    "SELECT verify_channel FROM settings WHERE guild_id = ?"
            ).addParameter(guildId).executeQuerySync();

            while (rs.next()) {
                return Commentarii.getInstance().getJda().getGuildById(guildId).getTextChannelById(rs.getLong("verify_channel"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the verify role for a guild.
     *
     * @param guildId The ID of the guild you want to get the verify role from.
     * @return A Role object
     */
    public Role getVerifyRole(long guildId) {
        try {
            CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                    "SELECT verify_role FROM settings WHERE guild_id = ?"
            ).addParameter(guildId).executeQuerySync();

            while (rs.next()) {
                return Commentarii.getInstance().getJda().getGuildById(guildId).getRoleById(rs.getLong("verify_role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the webhook ID for the specified guild ID.
     *
     * @param guildId The ID of the guild you want to get the webhook ID from.
     * @return A long
     */
    public long getWebhookIdByGuild(long guildId) {
        try {
            CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                    "SELECT verify_webhook FROM settings WHERE guild_id = ?"
            ).addParameter(guildId).executeQuerySync();

            while (rs.next()) {
                return rs.getLong("verify_webhook");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
