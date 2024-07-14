package eu.cyzetlc.commentarii.modals;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.entities.Embed;
import eu.cyzetlc.commentarii.service.modal.Modal;
import eu.cyzetlc.commentarii.service.modal.annotation.ModalSpecification;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.awt.*;

@ModalSpecification(
        id = "apply",
        title = "Bewirb dich"
)
public class ApplyModal extends Modal {
    @Override
    public void initialize() {
        TextInput name = TextInput.create("name", "Name", TextInputStyle.SHORT)
                .setMinLength(3).setMaxLength(32).setRequired(true).build();

        TextInput age = TextInput.create("age", "Alter", TextInputStyle.SHORT)
                .setMinLength(2).setMaxLength(2).setRequired(true).build();

        TextInput content = TextInput.create("content", "Bewerbung", TextInputStyle.PARAGRAPH)
                .setMinLength(250).setMaxLength(4000).setRequired(true).build();

        create(this.getClass(), ActionRow.of(name), ActionRow.of(age), ActionRow.of(content));
    }

    @Override
    public void handleSubmit(ModalInteractionEvent event) {
        String name = event.getValue("name").getAsString();
        String age = event.getValue("age").getAsString();
        String content = event.getValue("content").getAsString();
        TextChannel channel = Commentarii.getInstance().getApplyHandler().getApplyChannelOfGuild(event.getGuild().getIdLong());

        if (channel != null) {
            channel.sendMessageEmbeds(Embed.getEmbed(
                    "**Bewerbung**",
                    "**Name**\n" + name + "\n\n" + "**Alter**\n" + age + "\n\n" + "**Bewerbung**\n" + content,
                    Color.GREEN
            ).build()).queue();
        }
    }
}
