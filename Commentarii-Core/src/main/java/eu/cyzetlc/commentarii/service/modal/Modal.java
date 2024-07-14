package eu.cyzetlc.commentarii.service.modal;

import eu.cyzetlc.commentarii.service.modal.annotation.ModalSpecification;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Arrays;

public abstract class Modal implements IModal {
    @Getter
    private net.dv8tion.jda.api.interactions.modals.Modal modal;

    @Override
    @Deprecated
    public void create(Class<?> clazz, ActionRow... rows) {
        /*if (clazz.isAnnotationPresent(ModalSpecification.class)) {
            ModalSpecification spec = clazz.getAnnotation(ModalSpecification.class);
            this.modal = net.dv8tion.jda.api.interactions.modals.Modal.create(
                    spec.id(), spec.title()
            ).addActionRow(Arrays.asList(rows)).build();
        }*/
    }

    @Override
    public Button register(ModalSpecification spec) {
        return null;
    }
}
