package eu.cyzetlc.commentariis.service.button;

import eu.cyzetlc.commentariis.service.button.annotation.ButtonSpecification;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface IButton {
    void handleClick(ButtonInteractionEvent event);

    net.dv8tion.jda.api.interactions.components.buttons.Button register(ButtonSpecification spec);

    String getUrl();

    String getEmoji();
}
