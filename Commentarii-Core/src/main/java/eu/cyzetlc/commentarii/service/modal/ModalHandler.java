package eu.cyzetlc.commentarii.service.modal;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class ModalHandler {
    @Getter
    private final LinkedHashMap<String, Modal> modals = new LinkedHashMap<>();

    public void load(IModal modal, @NotNull GenericCommandInteractionEvent event) {
        if (modal instanceof Modal m) {
            modal.initialize();
            if (m.getModal() != null) {
                event.replyModal(m.getModal()).queue();
                modals.put(m.getModal().getId(), m);
            }
        }
    }
}
