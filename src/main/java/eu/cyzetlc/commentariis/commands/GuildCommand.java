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
        command = "guild",
        description = "Shows details of the server"
)
public class GuildCommand extends Command {
    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandEvent event, TextChannel channel, String[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append("**Besitzer**").append("\n").append(channel.getGuild().getOwner().getAsMention());
        builder.append("\n\n");
        builder.append("**Nutzer**").append("\n").append(channel.getGuild().getMembers().size());
        builder.append("\n\n");
        builder.append("**Erstellt**").append("\n").append("<t:").append(channel.getGuild().getTimeCreated().toEpochSecond()).append(":R>");
        builder.append("\n\n");
        builder.append("**Gespeicherte Logs**").append("\n").append(Commentarii.getInstance().getLogHandler().getLogCountOfGuild(channel.getGuild().getIdLong()));

        this.sendEmbed(
                Embed.getEmbed(
                "**Server-Info**",
                builder.toString(),
                Color.GREEN).addThumbnail(channel.getGuild().getIconUrl())
        );
    }
}
