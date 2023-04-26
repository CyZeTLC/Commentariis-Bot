package eu.cyzetlc.commentarii.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.audio.GuildMusicHandler;
import eu.cyzetlc.commentarii.service.audio.PlayerHandler;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentarii.service.entities.Embed;
import eu.cyzetlc.commentarii.service.entities.User;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

@CommandSpecification(
        command = "stop",
        description = "Stops playing music"
)
public class StopCommand extends Command {
    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        AudioChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        if (connectedChannel != null) {
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(connectedChannel);
            audioManager.setSpeakingMode(SpeakingMode.VOICE);

            final GuildMusicHandler musicHandler = PlayerHandler.getInstance().getMusicManager(audioManager.getGuild());
            musicHandler.scheduler.player.stopTrack();
            musicHandler.scheduler.queue.clear();

            this.sendEmbed(Embed.getEmbed(Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_stop.title"), Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_stop.content"), Color.GREEN));
        } else {
            this.sendMessage(Commentarii.getInstance().getMessageHandler().getMessageForGuild(event.getGuild().getIdLong(), "commentarii.command.play.not_in_channel"));
        }
    }
}
