package eu.cyzetlc.commentarii.commands;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.buttons.InviteMeButton;
import eu.cyzetlc.commentarii.buttons.VisiteWebsiteButton;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentarii.service.entities.Embed;
import eu.cyzetlc.commentarii.service.entities.User;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

@CommandSpecification(
        command = "info",
        description = "Shows all debug-infos of the bot"
)
public class InfoCommand extends Command {
    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        Embed embed = Embed.getEmbed("**Debug-Info**", "Stand: " + LocalDateTime.now(), Color.GREEN);
        embed.addField("**Author**", "CyZeTLC × Tom C.", true);
        embed.addField("**Version**", "v1.0", true);
        embed.addField("**Gestartet**", "<t:" + (Commentarii.getStated()/1000) + ":R>", true);
        embed.addField("**Nutzer**", String.valueOf(Commentarii.getInstance().getJda().getUsers().size()), true);
        embed.addField("**Server**", String.valueOf(Commentarii.getInstance().getJda().getGuilds().size()), true);
        embed.addField("**GatewayPing**", Commentarii.getInstance().getJda().getGatewayPing() + "ms", true);
        embed.addField("**API Anfragen**", String.valueOf(Commentarii.getInstance().getJda().getResponseTotal()), true);
        embed.addThumbnail("https://img.cyzetlc.eu/PTB9GOgooh");
        this.sendEmbedWithButtons(embed, List.of(new InviteMeButton(), new VisiteWebsiteButton()));
    }
}
