package eu.cyzetlc.commentariis.listener;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.log.LogHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GenericGuildInviteEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class LogListener extends ListenerAdapter {
    @Override
    // A method that is called when the bot is ready.
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild guild : Commentarii.getInstance().getJda().getGuilds()) {
            Commentarii.getInstance().getLogHandler().log("Bot gestartet", "Der Bot ist <t:" + (Commentarii.getStated()/1000) + ":R> gestartet.", LogHandler.LogLevel.INFO, guild.getIdLong());
        }
    }

    @Override
    // A method that is called when the name of the server is changed.
    public void onGuildUpdateName(@NotNull GuildUpdateNameEvent event) {
        Commentarii.getInstance().getLogHandler().log("Servername aktualisiert", "Der Servername wurde geändert!", LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
    }

    @Override
    // A method that is called when a new invite is created.
    public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event) {
        String log = event.getInvite().getInviter().getName() + "#" + event.getInvite().getInviter().getDiscriminator() + " hat einen Einladungslink erstellt!";
        Commentarii.getInstance().getLogHandler().log("Einladungslink erstellt", log, LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
    }

    @Override
    // A method that is called when a role is added to a member.
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        for (Role role : event.getRoles()) {
            Commentarii.getInstance().getLogHandler().log("Rolle hinzugefügt", event.getMember().getAsMention() + " hat nun " + role.getAsMention() + ".", LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
        }
    }

    @Override
    // A method that is called when a role is removed from a member.
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        for (Role role : event.getRoles()) {
            Commentarii.getInstance().getLogHandler().log("Rolle entfernt", event.getMember().getAsMention() + " hat nun nicht mehr " + role.getAsMention() + ".", LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
        }
    }

    @Override
    // A method that is called when a member joins the server.
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Commentarii.getInstance().getLogHandler().log("Nutzer gejoint", event.getMember().getAsMention() + " ist gejoint.", LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
    }

    @Override
    // A method that is called when a member leaves the server.
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Commentarii.getInstance().getLogHandler().log("Nutzer geleaved", event.getMember().getAsMention() + " ist geleaved.", LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
    }

    @Override
    // Logging when a user changes his nickname.
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        Commentarii.getInstance().getLogHandler().log("Nutzername geändert", event.getMember().getAsMention() + " ist hat seinen Namen geändert.", LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
    }

    @Override
    // It logs when a user reacts to a message.
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        String log = event.getMember().getAsMention() + " hat auf eine Nachricht mit " + event.getReaction().getReactionEmote().getAsReactionCode() + " reagiert.";
        Commentarii.getInstance().getLogHandler().log("Reaktion hinzugefügt", log, LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
    }

    @Override
    // It logs when a user creates an invite.
    public void onGenericGuildInvite(@NotNull GenericGuildInviteEvent event) {
        String log = "Es wurde ein [Einladungslink](" + event.getUrl() + ") erstellt!";
        Commentarii.getInstance().getLogHandler().log("Einladungslink erstellt", log, LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
    }
}
