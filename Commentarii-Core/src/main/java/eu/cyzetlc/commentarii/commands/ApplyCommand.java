package eu.cyzetlc.commentarii.commands;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.modals.ApplyModal;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentarii.service.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@CommandSpecification(
        command = "apply",
        description = "Apply for the Commentarii-Team"
)
public class ApplyCommand extends Command {
    @Override
    public void initialize(CommandSpecification spec) {
        super.initialize(spec);
    }

    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        Commentarii.getInstance().getModalHandler().load(new ApplyModal(), event);
    }
}
