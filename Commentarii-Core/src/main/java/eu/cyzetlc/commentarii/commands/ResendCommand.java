package eu.cyzetlc.commentarii.commands;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentarii.service.entities.Embed;
import eu.cyzetlc.commentarii.service.entities.User;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

@CommandSpecification(
        command = "resend",
        description = "Resend verification message"
)
public class ResendCommand extends Command {
    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        Role role = Commentarii.getInstance().getVerifyHandler().getVerifyRole(event.getGuild().getIdLong());
        if (!event.getGuild().getMemberById(user.getJdaUser().getIdLong()).getRoles().contains(role)) {
            Commentarii.getInstance().getVerifyHandler().sendPrivateMessage(user.getJdaUser().getId(), channel.getGuild().getId());
            event.replyEmbeds(Embed.getEmbed(
                    "**Mal schauen ob es geklappt hat ;)**",
                    "Du solltest nun eine private Nachricht erhalten haben.\nWenn du keine erhalten hast, so wende dich bitte an den Support!",
                    Color.GREEN
            ).build()).setEphemeral(true).queue();
        } else {
            event.replyEmbeds(Embed.getEmbed(
                    "**Huch**",
                    "Du bist doch schon verifiziert?",
                    Color.RED
            ).build()).setEphemeral(true).queue();
        }
    }
}
