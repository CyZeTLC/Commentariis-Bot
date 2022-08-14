package eu.cyzetlc.commentariis.service.modal;

import eu.cyzetlc.commentariis.service.modal.annotation.ModalSpecification;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public interface IModal {
    void initialize();

    void create(Class<?> clazz, ActionRow... rows);

    void handleSubmit(ModalInteractionEvent event);

    net.dv8tion.jda.api.interactions.components.buttons.Button register(ModalSpecification spec);
}
