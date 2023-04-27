package eu.cyzetlc.commentarii.service.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.log.LogHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
     * This function loads and plays audio tracks or playlists in a Discord channel and provides callbacks for success or
     * failure.
     *
     * @param channel The Discord text channel where the music will be played.
     * @param trackUrl The URL of the audio track or playlist to be loaded and played.
     * @param callback The callback parameter is a Consumer functional interface that accepts an AudioTrack object as input
     * and performs some operation on it. It is used to handle the loaded AudioTrack object after it has been successfully
     * loaded by the audio player manager.
     * @param clb The parameter "clb" is a Consumer that accepts an AudioPlaylist object as input and performs some action
     * with it. It is used as a callback function when an audio playlist is successfully loaded.
     * @param failed The "failed" parameter is a Consumer that accepts a String as input. It is used to handle the case
     * where loading the audio track or playlist fails, and the String input is the error message explaining why the
     * loading failed.
     */
    public void loadAndPlay(TextChannel channel, String trackUrl, Consumer<AudioTrack> callback, Consumer<AudioPlaylist> clb, Consumer<String> failed) {
        final GuildMusicHandler musicHandler = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicHandler, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicHandler.scheduler.queue(track);

                callback.accept(track);
                Commentarii.getInstance().getLogHandler().log(
                        Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_play.title"),
                        Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_play.content", track.getInfo().title, track.getInfo().author),
                        LogHandler.LogLevel.INFO,
                        channel.getGuild().getIdLong()
                );
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                clb.accept(playlist);
                Commentarii.getInstance().getLogHandler().log(
                        Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_playlist_play.title"),
                        Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_playlist_play.content", playlist.getName(), playlist.getTracks().size()+""),
                        LogHandler.LogLevel.INFO,
                        channel.getGuild().getIdLong()
                );

                for (final AudioTrack track : tracks) {
                    musicHandler.scheduler.queue(track);
                }
            }

            @Override
            public void noMatches() {
                failed.accept(Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.log.music_no_matches"));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                failed.accept(exception.getMessage());
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
