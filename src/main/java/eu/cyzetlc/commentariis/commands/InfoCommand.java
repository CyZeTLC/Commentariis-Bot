package eu.cyzetlc.commentariis.commands;

import eu.cyzetlc.commentariis.service.command.Command;
import eu.cyzetlc.commentariis.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentariis.service.entities.Embed;
import eu.cyzetlc.commentariis.service.entities.User;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

@CommandSpecification(
        command = "info"
)
public class InfoCommand extends Command {
    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandEvent event, TextChannel channel, String[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append("__Author:__").append(" ").append("Tom").append(" ").append("Coombs");
        builder.append("\n");
        builder.append("__Version:__").append(" ").append("v1.0");

        this.sendEmbed(Embed.getEmbed(
                "**Debug-Info**",
                builder.toString(),
                Color.GREEN
        ));
    }
}
