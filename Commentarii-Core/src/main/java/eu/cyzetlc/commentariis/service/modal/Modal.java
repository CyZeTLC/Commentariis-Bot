package eu.cyzetlc.commentariis.service.modal;

import eu.cyzetlc.commentariis.service.modal.annotation.ModalSpecification;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public abstract class Modal implements IModal {
    @Getter
    private net.dv8tion.jda.api.interactions.components.Modal modal;

    @Override
    public void create(Class<?> clazz, ActionRow... rows) {
        if (clazz.isAnnotationPresent(ModalSpecification.class)) {
            ModalSpecification spec = clazz.getAnnotation(ModalSpecification.class);
            this.modal = net.dv8tion.jda.api.interactions.components.Modal.create(
                    spec.id(), spec.title()
            ).addActionRows(rows).build();
        }
    }

    @Override
    public Button register(ModalSpecification spec) {
        return null;
    }
}
