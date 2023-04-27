package eu.cyzetlc.commentarii.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.buttons.InviteMeButton;
import eu.cyzetlc.commentarii.buttons.VisiteWebsiteButton;
import eu.cyzetlc.commentarii.service.audio.PlayerHandler;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentarii.service.entities.Embed;
import eu.cyzetlc.commentarii.service.entities.User;
import eu.cyzetlc.commentarii.service.log.LogHandler;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@CommandSpecification(
        command = "play",
        description = "Plays the music you like"
)
public class PlayCommand extends Command {
    @Override
    public void initialize(CommandSpecification spec) {
        super.initialize(spec);
        this.getCommandData().addOptions(new OptionData(OptionType.STRING, "songname", "Name of the song").setRequired(true));
    }

    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        AudioChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        if (connectedChannel != null) {
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(connectedChannel);
            audioManager.setSpeakingMode(SpeakingMode.VOICE);

            String link = event.getOption("songname").getAsString();

            if (!isUrl(link)) {
                link = "ytsearch:" + link;
            }

            PlayerHandler.getInstance()
                    .loadAndPlay(Commentarii.getInstance().getLogHandler().getLogChannelOfGuild(event.getGuild().getIdLong()),
                            link,
                            (AudioTrack track) -> this.sendEmbed(Embed.getEmbed(Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_play.title"), Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_play.content", track.getInfo().title, track.getInfo().author), Color.GREEN)),
                            (playlist) -> this.sendEmbed(Embed.getEmbed(Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_playlist_play.title"), Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_playlist_play.content", playlist.getName(), playlist.getTracks().size()+""), Color.GREEN)),
                            (message) -> this.sendEmbed(Embed.getEmbed(Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_failed.title"), message, Color.RED)));
        } else {
            this.sendMessage(Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.command.play.not_in_channel"));
        }
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
