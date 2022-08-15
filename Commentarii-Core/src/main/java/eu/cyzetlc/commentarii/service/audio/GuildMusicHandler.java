package eu.cyzetlc.commentarii.service.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;

public class GuildMusicHandler {
    // Creating a new AudioPlayer object.
    public final AudioPlayer audioPlayer;

    // A reference to the TrackScheduler class.
    public final TrackScheduler scheduler;

    @Getter
    // A reference to the AudioPlayerSendHandler class.
    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicHandler(AudioPlayerManager manager) {
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }
}
