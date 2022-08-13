package eu.cyzetlc.commentariis.buttons;

import eu.cyzetlc.commentariis.service.button.Button;
import eu.cyzetlc.commentariis.service.button.annotation.ButtonSpecification;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

@ButtonSpecification(
        id = "inviteMe",
        label = "Lad mich mal ein",
        style = ButtonStyle.PRIMARY
)
public class InviteMeButton extends Button {
    @Override
    // The method that is called when the button is clicked.
    public void handleClick(ButtonClickEvent event) {
    }

    @Override
    // The URL that is displayed when the button is clicked.
    public String getUrl() {
        return "https://discord.com/oauth2/authorize?client_id=1007778519717269516&scope=bot&permissions=8";
    }

    @Override
    // The emoji that is displayed next to the button.
    public String getEmoji() {
        return "\uD83D\uDD17";
    }
}
