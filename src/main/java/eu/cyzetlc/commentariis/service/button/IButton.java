package eu.cyzetlc.commentariis.service.button;

import eu.cyzetlc.commentariis.service.button.annotation.ButtonSpecification;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public interface IButton {
    void handleClick(ButtonClickEvent event);

    Button register(ButtonSpecification spec);

    String getUrl();

    String getEmoji();
}
