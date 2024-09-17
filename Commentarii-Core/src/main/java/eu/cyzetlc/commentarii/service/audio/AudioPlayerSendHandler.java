package eu.cyzetlc.commentarii.service.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import lombok.Getter;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

@Getter
public class AudioPlayerSendHandler implements AudioSendHandler {
    // Creating a new audio player.
    private final AudioPlayer audioPlayer;
    // Used to store the audio data.
    private final ByteBuffer buffer;
    // A class that is used to store the audio data.
    private final MutableAudioFrame frame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
    }

    @Override
    // Checking if the audio player can provide audio.
    public boolean canProvide() {
        try {
            return this.audioPlayer.provide(this.frame);
        } catch (Exception e) {

        }
        return false;
    }

    @Nullable
    @Override
    // Providing the audio to the discord server.
    public ByteBuffer provide20MsAudio() {
        return this.buffer.flip();
    }

    @Override
    // Checking if the audio is in the Opus format.
    public boolean isOpus() {
        return true;
    }
}
