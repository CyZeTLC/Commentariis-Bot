package eu.cyzetlc.commentarii.listener;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.audio.PlayerHandler;
import eu.cyzetlc.commentarii.service.log.LogHandler;
import lombok.Getter;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.*;
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

import java.util.LinkedHashMap;
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

        if (Commentarii.getInstance().getVerifyHandler().getVerifyChannel(event.getGuild().getIdLong()) != null) {
            Commentarii.getInstance().getVerifyHandler().sendPrivateMessage(event.getUser().getId(), event.getGuild().getId());
        }

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
    // A method that is called when a user joins a voice channel.
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        if (!event.getMember().getId().equals(Commentarii.getInstance().getJda().getSelfUser().getId())) {
            AudioChannel connectedChannel = event.getMember().getVoiceState().getChannel();
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(connectedChannel);
            audioManager.setSpeakingMode(SpeakingMode.VOICE);

            PlayerHandler.getInstance()
                    .loadAndPlay(Commentarii.getInstance().getLogHandler().getLogChannelOfGuild(event.getGuild().getIdLong()),
                            "https://www.youtube.com/watch?v=ZevEW7zwslA");
        }
    }


    /*
     * Channel Events
     */

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_create.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_create.content", event.getChannel().getAsMention()),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_delete.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_delete.content", "**" + event.getChannel().getName() + "**"),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_name.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_name.content", event.getChannel().getAsMention(), event.getNewValue(), event.getOldValue()),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    public void onChannelUpdateNSFW(@NotNull ChannelUpdateNSFWEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_nsfw.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_nsfw.content", event.getChannel().getAsMention()),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    public void onChannelUpdatePosition(@NotNull ChannelUpdatePositionEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_position.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_position.content", event.getChannel().getAsMention(), event.getNewValue()+"", event.getOldValue()+""),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    public void onChannelUpdateSlowmode(@NotNull ChannelUpdateSlowmodeEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_slowmode.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_slowmode.content", event.getChannel().getAsMention(), event.getNewValue()+"", event.getOldValue()+""),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    public void onChannelUpdateType(@NotNull ChannelUpdateTypeEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_type.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_type.content", event.getChannel().getAsMention(), event.getNewValue().name(), event.getOldValue().name()),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }

    @Override
    public void onChannelUpdateUserLimit(@NotNull ChannelUpdateUserLimitEvent event) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_limit.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.log.channel_limit.content", event.getChannel().getAsMention(), event.getNewValue()+"", event.getOldValue()+""),
                LogHandler.LogLevel.INFO,
                event.getGuild().getIdLong()
        );
    }
}
