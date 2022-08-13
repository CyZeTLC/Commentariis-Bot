package eu.cyzetlc.commentariis.buttons;

import eu.cyzetlc.commentariis.service.button.Button;
import eu.cyzetlc.commentariis.service.button.annotation.ButtonSpecification;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

@ButtonSpecification(
        id = "visitWebsite",
        label = "Besuche mal meine Webseite",
        style = ButtonStyle.PRIMARY
)
public class VisiteWebsiteButton extends Button {
    @Override
    // The method that is called when the button is clicked.
    public void handleClick(ButtonClickEvent event) {
    }

    @Override
    // The URL that is displayed when the button is clicked.
    public String getUrl() {
        return "https://github.com/CyZeTLC/Commentariis-Bot";
    }

    @Override
    // The emoji that is displayed next to the button.
    public String getEmoji() {
        return "\uD83D\uDD17";
    }
}
