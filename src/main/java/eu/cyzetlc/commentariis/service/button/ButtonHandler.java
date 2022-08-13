package eu.cyzetlc.commentariis.service.button;

import eu.cyzetlc.commentariis.service.button.annotation.ButtonSpecification;
import lombok.Getter;

import java.util.LinkedHashMap;

public class ButtonHandler {
    @Getter
    // Creating a new LinkedHashMap with a String as the key and a Button as the value.
    private final LinkedHashMap<String, Button> buttons = new LinkedHashMap<>();

    /**
     * If the button has a ButtonSpecification annotation, add it to the buttons map and return the button
     *
     * @param button The button to register.
     * @return A Button object.
     */
    public net.dv8tion.jda.api.interactions.components.Button register(Button button) {
        if (button.getClass().isAnnotationPresent(ButtonSpecification.class)) {
            this.buttons.put(button.getClass().getAnnotation(ButtonSpecification.class).id(), button);
            return button.register(button.getClass().getAnnotation(ButtonSpecification.class));
        }
        return null;
    }
}
