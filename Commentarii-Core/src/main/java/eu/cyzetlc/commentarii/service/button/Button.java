package eu.cyzetlc.commentarii.service.button;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.button.annotation.ButtonSpecification;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public abstract class Button implements IButton {

    @Override
    // A method that returns a `net.dv8tion.jda.api.interactions.components.Button` object.
    public net.dv8tion.jda.api.interactions.components.buttons.Button register(ButtonSpecification spec) {
        String label = spec.label();

        if (spec.type() == ButtonSpecification.ButtonLabelType.MESSAGE_KEY) {
            label = Commentarii.getInstance().getMessageHandler().getStaticMessage(label);
        }

        net.dv8tion.jda.api.interactions.components.buttons.Button btn = net.dv8tion.jda.api.interactions.components.buttons.Button.of(
                spec.style(),
                spec.id(),
                label
        );

        if (this.getEmoji() != null) {
            btn = btn.withEmoji(Emoji.fromFormatted(this.getEmoji()));
        }

        if (this.getUrl() != null) {
            btn = btn.withUrl(this.getUrl());
        }

        return btn;
    }

    @Override
    // A method that returns a string.
    public String getUrl() {
        return null;
    }

    @Override
    // A method that returns a string.
    public String getEmoji() {
        return null;
    }
}
