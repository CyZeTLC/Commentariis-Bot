package eu.cyzetlc.commentariis.commands;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.buttons.InviteMeButton;
import eu.cyzetlc.commentariis.buttons.VisiteWebsiteButton;
import eu.cyzetlc.commentariis.modals.ApplyModal;
import eu.cyzetlc.commentariis.service.command.Command;
import eu.cyzetlc.commentariis.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentariis.service.entities.Embed;
import eu.cyzetlc.commentariis.service.entities.User;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.List;

@CommandSpecification(
        command = "info",
        description = "Shows all debug-infos of the bot"
)
public class InfoCommand extends Command {
    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append("**Author:**").append("\n").append("CyZeTLC").append(" ").append("×").append(" ").append("Tom").append(" ").append("C.");
        builder.append("\n\n");
        builder.append("**Version:**").append("\n").append("v1.0");
        builder.append("\n\n");
        builder.append("**Gestartet:**").append("\n").append("<t:").append(Commentarii.getStated()/1000).append(":R>");
        builder.append("\n\n");
        builder.append("**Nutzer:**").append("\n").append(Commentarii.getInstance().getJda().getUsers().size());
        builder.append("\n\n");
        builder.append("**Server:**").append("\n").append(Commentarii.getInstance().getJda().getGuilds().size());
        builder.append("\n\n");
        builder.append("**Gateway Ping:**").append("\n").append(Commentarii.getInstance().getJda().getGatewayPing()).append("ms");
        builder.append("\n\n");
        builder.append("**API Anfragen:**").append("\n").append(Commentarii.getInstance().getJda().getResponseTotal());

        this.sendEmbedWithButtons(
                Embed.getEmbed(
                "**Debug-Info**",
                builder.toString(),
                Color.GREEN),
                List.of(new InviteMeButton(), new VisiteWebsiteButton())
        );
    }
}
