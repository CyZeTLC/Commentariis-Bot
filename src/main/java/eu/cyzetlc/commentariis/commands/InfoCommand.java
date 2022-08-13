package eu.cyzetlc.commentariis.commands;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.buttons.InviteMeButton;
import eu.cyzetlc.commentariis.buttons.VisiteWebsiteButton;
import eu.cyzetlc.commentariis.service.command.Command;
import eu.cyzetlc.commentariis.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentariis.service.entities.Embed;
import eu.cyzetlc.commentariis.service.entities.User;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.List;

@CommandSpecification(
        command = "info"
)
public class InfoCommand extends Command {
    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandEvent event, TextChannel channel, String[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append("__Author:__").append(" ").append("CyZeTLC").append(" ").append("×").append(" ").append("Tom").append(" ").append("C.");
        builder.append("\n");
        builder.append("__Version:__").append(" ").append("v1.0");
        builder.append("\n");
        builder.append("__Gestartet:__").append(" ").append("<t:").append(Commentarii.getStated()/1000).append(":R>");
        builder.append("\n");
        builder.append("__Nutzer:__").append(" ").append(Commentarii.getInstance().getJda().getUsers().size());
        builder.append("\n");
        builder.append("__Server:__").append(" ").append(Commentarii.getInstance().getJda().getGuilds().size());
        builder.append("\n");
        builder.append("__Gateway Ping:__").append(" ").append(Commentarii.getInstance().getJda().getGatewayPing()).append("ms");
        builder.append("\n");
        builder.append("__API Anfragen:__").append(" ").append(Commentarii.getInstance().getJda().getResponseTotal());

        this.sendEmbedWithButtons(
                Embed.getEmbed(
                "**Debug-Info**",
                builder.toString(),
                Color.GREEN),
                List.of(new InviteMeButton(), new VisiteWebsiteButton())
        );
    }
}
