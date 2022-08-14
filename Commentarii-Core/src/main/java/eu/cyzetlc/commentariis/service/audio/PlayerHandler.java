package eu.cyzetlc.commentariis.service.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.log.LogHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerHandler {
    // A variable that is used to store the instance of the PlayerHandler class.
    private static PlayerHandler instance;

    // Creating a map that uses a Long as the key and a GuildMusicHandler as the value.
    private final Map<Long, GuildMusicHandler> musicManagers;
    // Creating a new instance of the AudioPlayerManager class.
    private final AudioPlayerManager audioPlayerManager;

    public PlayerHandler() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    /**
     * If the guild doesn't have a music manager, create one and return it
     *
     * @param guild The guild that the music manager is being requested for.
     * @return A GuildMusicHandler object.
     */
    public GuildMusicHandler getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicHandler guildMusicHandler = new GuildMusicHandler(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicHandler.getSendHandler());

            return guildMusicHandler;
        });
    }

    /**
     * It loads the track from the URL, and then queues it to be played
     *
     * @param channel The channel to send the message to.
     * @param trackUrl The URL of the track to load.
     */
    public void loadAndPlay(TextChannel channel, String trackUrl) {
        final GuildMusicHandler musicHandler = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicHandler, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicHandler.scheduler.queue(track);

                Commentarii.getInstance().getLogHandler().log(
                        Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_play.title"),
                        Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_play.content", track.getInfo().title, track.getInfo().author),
                        LogHandler.LogLevel.INFO,
                        channel.getGuild().getIdLong()
                );
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                //
            }

            @Override
            public void noMatches() {
                //
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                //
            }
        });
    }

    /**
     * If the instance variable is null, create a new instance of the PlayerHandler class and assign it to the instance
     * variable. If the instance variable is not null, return the instance variable
     *
     * @return The instance of the PlayerHandler class.
     */
    public static PlayerHandler getInstance() {
        if (instance == null) {
            instance = new PlayerHandler();
        }
        return instance;
    }

}
