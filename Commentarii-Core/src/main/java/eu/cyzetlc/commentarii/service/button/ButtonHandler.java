package eu.cyzetlc.commentarii.service.button;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.button.annotation.ButtonSpecification;
import eu.cyzetlc.commentarii.service.entities.Embed;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public net.dv8tion.jda.api.interactions.components.buttons.Button register(Button button) {
        if (button.getClass().isAnnotationPresent(ButtonSpecification.class)) {
            this.buttons.put(button.getClass().getAnnotation(ButtonSpecification.class).id(), button);
            return button.register(button.getClass().getAnnotation(ButtonSpecification.class));
        }
        return null;
    }

    /**
     * It sends an embed with buttons to the temporary channel
     *
     * @param embed The embed you want to send.
     * @param buttons A list of buttons to be added to the embed.
     * @param autoDelete If the message should be deleted after a certain amount of time.
     */
    public void sendEmbedWithButtons(Embed embed, List<Button> buttons, TextChannel tempChannel, boolean autoDelete) {
        LinkedList<net.dv8tion.jda.api.interactions.components.buttons.Button> list = new LinkedList<>();
        for (Button btn : buttons) {
            list.add(Commentarii.getInstance().getButtonHandler().register(btn));
        }

        if (tempChannel != null) {
            if (autoDelete) {
                tempChannel.sendMessageEmbeds(embed.build()).setActionRow(list).queue(msg -> this.deleteAfter(msg, 5));
            } else {
                tempChannel.sendMessageEmbeds(embed.build()).setActionRow(list).queue();
            }
        }
    }

    /**
     * Delete the message after the specified delay.
     *
     * @param message The message to delete.
     * @param delay The amount of time to wait before deleting the message.
     */
    public void deleteAfter(Message message, int delay) {
        message.delete().queueAfter(delay, TimeUnit.SECONDS);
    }
}
