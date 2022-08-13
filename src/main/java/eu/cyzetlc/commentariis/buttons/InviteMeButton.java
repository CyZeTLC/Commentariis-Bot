package eu.cyzetlc.commentariis.buttons;

import eu.cyzetlc.commentariis.service.button.Button;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class InviteMeButton extends Button {
    @Override
    public void handleClick(ButtonClickEvent event) {
    }

    @Override
    public String getUrl() {
        return "https://github.com/CyZeTLC/Commentariis-Bot";
    }

    @Override
    public String getEmoji() {
        return "\uD83D\uDD17";
    }
}
