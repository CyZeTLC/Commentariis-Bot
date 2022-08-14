package eu.cyzetlc.commentariis.listener;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.log.LogHandler;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GenericGuildInviteEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class LogListener extends ListenerAdapter {
    @Getter
    private static final LinkedHashMap<Long, List<Invite>> invites = new LinkedHashMap<>();

    @Override
    // A method that is called when the bot is ready.
    public void onReady(@NotNull ReadyEvent event) {
        Commentarii.getInstance().getCommandHandler().updateCommands(Commentarii.getInstance().getJda().getGuilds().get(0));

        for (Guild guild : Commentarii.getInstance().getJda().getGuilds()) {
            Commentarii.getInstance().getLogHandler().log(
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(guild.getIdLong(), "commentarii.log.started.title"),
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(guild.getIdLong(), "commentarii.log.started.content", String.valueOf(Commentarii.getStated()/1000)),
                    LogHandler.LogLevel.INFO,
                    guild.getIdLong()
            );
        }
    }

    @Override
    // A method that is called when the name of the server is changed.
    public void onGuildUpdateName(@NotNull GuildUpdateNameEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.servername.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.servername.content"),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    // A method that is called when a new invite is created.
    public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event) {
        String log = "Es wurde ein [Einladungslink](" + event.getUrl() + ") erstellt!";
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.invite_create.title"),
                log + " \n" + Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.invite_create.content", event.getInvite().getInviter().getAsMention()),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
        event.getGuild().retrieveInvites().queue(invites -> LogListener.getInvites().put(event.getGuild().getIdLong(), invites));
    }

    @Override
    // A method that is called when a role is added to a member.
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        for (Role role : event.getRoles()) {
            Commentarii.getInstance().getLogHandler().log(
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.role_add.title"),
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.role_add.content", event.getMember().getAsMention(), role.getAsMention()),
                    LogHandler.LogLevel.INFO,
                    event.getGuild().getIdLong()
            );
        }
    }

    @Override
    // A method that is called when a role is removed from a member.
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        for (Role role : event.getRoles()) {
            Commentarii.getInstance().getLogHandler().log(
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.role_remove.title"),
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.role_remove.content", event.getMember().getAsMention(), role.getAsMention()),
                    LogHandler.LogLevel.INFO,
                    event.getGuild().getIdLong()
            );
        }
    }

    @Override
    // A method that is called when a member joins the server.
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.join.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.join.content", event.getMember().getAsMention()),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );

        event.getGuild().retrieveInvites().queue(inviteList -> inviteList.forEach(invite -> {
            for (Invite in : invites.get(event.getGuild().getIdLong())) {
                if (in.getCode().equals(invite.getCode())) {
                    if (invite.getUses() > in.getUses()) {
                        Commentarii.getInstance().getLogHandler().log(
                                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.join_invite.title"),
                                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.join_invite.content", invite.getCode(), invite.getInviter().getAsMention()),
                                LogHandler.LogLevel.INFO,
                                event.getGuild().getIdLong()
                        );
                    }
                    break;
                }
            }
        }));
        event.getGuild().retrieveInvites().queue(invites -> LogListener.getInvites().put(event.getGuild().getIdLong(), invites));
    }

    @Override
    // A method that is called when a member leaves the server.
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.leave.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.leave.content", event.getMember().getAsMention()),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    // Logging when a user changes his nickname.
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.username.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.username.content", event.getMember().getAsMention(), event.getNewNickname(), event.getOldNickname()),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    // It logs when a user reacts to a message.
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        String log = event.getMember().getAsMention() + " hat auf eine Nachricht mit " + event.getReaction().getEmoji().getFormatted() + " reagiert.";
        Commentarii.getInstance().getLogHandler().log("Reaktion hinzugefügt", log, LogHandler.LogLevel.INFO, event.getGuild().getIdLong());
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        AudioChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(connectedChannel);
    }
}
