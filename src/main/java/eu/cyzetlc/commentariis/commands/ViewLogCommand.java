package eu.cyzetlc.commentariis.commands;

import eu.cyzetlc.commentariis.service.command.Command;
import eu.cyzetlc.commentariis.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentariis.service.entities.Embed;
import eu.cyzetlc.commentariis.service.entities.User;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

@CommandSpecification(
        command = "viewlog"
)
public class ViewLogCommand extends Command {
    @Override
    // This method is called when the command is registered.
    public void initialize(CommandSpecification spec) {
        super.initialize(spec);
        this.getCommandData().addOptions(new OptionData(OptionType.CHANNEL, "channel", "The logs of a specific channel"));
        this.getCommandData().addOptions(new OptionData(OptionType.USER, "user", "The logs of a specific user"));
        this.getCommandData().addOptions(new OptionData(OptionType.ROLE, "role", "The logs of a specific role"));
    }

    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandEvent event, TextChannel channel, String[] args) {
        this.sendEmbed(Embed.getEmbed(
                "**Hura**",
                "Der Channel wurde erfolgreich zu " + event.getOption("channel") + " geändert!",
                Color.GREEN
        ));
    }
}
