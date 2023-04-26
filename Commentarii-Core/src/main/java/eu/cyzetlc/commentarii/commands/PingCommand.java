package eu.cyzetlc.commentarii.commands;

import eu.cyzetlc.commentarii.buttons.InviteMeButton;
import eu.cyzetlc.commentarii.buttons.VisiteWebsiteButton;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentarii.service.entities.Embed;
import eu.cyzetlc.commentarii.service.entities.User;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@CommandSpecification(
        command = "ping",
        description = "Displays the bot's ping to different servers across the world"
)
public class PingCommand extends Command {
    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        Embed embed = Embed.getEmbed("**Ping-Info**", "Stand: " + LocalDateTime.now(), Color.GREEN);
        try {
            embed.addField("**NA-East**", this.requestServer("ping-nae.ds.on.epicgames.com").toMillis() + "ms", true);
            embed.addField("**NA-Central**", this.requestServer("ping-nac.ds.on.epicgames.com").toMillis() + "ms", true);
            embed.addField("**NA-West**", this.requestServer("ping-naw.ds.on.epicgames.com").toMillis() + "ms", true);
            embed.addField("**Europe**", this.requestServer("ping-eu.ds.on.epicgames.com").toMillis() + "ms", true);
            embed.addField("**Oceania**", this.requestServer("ping-oce.ds.on.epicgames.com").toMillis() + "ms", true);
            embed.addField("**Brazil**", this.requestServer("ping-br.ds.on.epicgames.com").toMillis() + "ms", true);
            embed.addField("**Asia**", this.requestServer("ping-asia.ds.on.epicgames.com").toMillis() + "ms", true);
            embed.addField("**Middle East**", this.requestServer("ping-me.ds.on.epicgames.com").toMillis() + "ms", true);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        embed.addThumbnail("https://img.cyzetlc.eu/PTB9GOgooh");
    }

    private Duration requestServer(String host) throws IOException, InterruptedException {
        Instant startTime = Instant.now();
        try {
            InetAddress address = InetAddress.getByName(host);
            if (address.isReachable(1000)) {
                return Duration.between(startTime, Instant.now());
            }
        } catch (IOException e) {
            // Host not available
        }
        return Duration.ofDays(1);
    }
}
