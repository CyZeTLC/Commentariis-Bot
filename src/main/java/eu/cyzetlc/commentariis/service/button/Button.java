package eu.cyzetlc.commentariis.service.button;

import eu.cyzetlc.commentariis.service.button.annotation.ButtonSpecification;
import net.dv8tion.jda.api.entities.Emoji;

public abstract class Button implements IButton {
    @Override
    // A method that returns a `net.dv8tion.jda.api.interactions.components.Button` object.
    public net.dv8tion.jda.api.interactions.components.Button register(ButtonSpecification spec) {
        net.dv8tion.jda.api.interactions.components.Button btn = net.dv8tion.jda.api.interactions.components.Button.of(
                spec.style(),
                spec.id(),
                spec.label()
        );

        if (this.getEmoji() != null) {
            btn = btn.withEmoji(Emoji.fromMarkdown(this.getEmoji()));
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
