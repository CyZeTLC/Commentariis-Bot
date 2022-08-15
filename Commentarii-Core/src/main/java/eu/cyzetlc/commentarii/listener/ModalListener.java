package eu.cyzetlc.commentarii.listener;

import eu.cyzetlc.commentarii.Commentarii;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ModalListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        event.deferEdit().queue();
        if (Commentarii.getInstance().getModalHandler().getModals().get(event.getModalId()) != null) {
            Commentarii.getInstance().getModalHandler().getModals().get(event.getModalId()).handleSubmit(event);
        }
    }
}
