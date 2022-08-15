package eu.cyzetlc.commentarii.listener;

import eu.cyzetlc.commentarii.Commentarii;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListener extends ListenerAdapter {

    @Override
    // A method that is called when a button is clicked.
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        event.deferEdit().queue();
        if (Commentarii.getInstance().getButtonHandler().getButtons().get(event.getId()) != null) {
            Commentarii.getInstance().getButtonHandler().getButtons().get(event.getId()).handleClick(event);
        }
    }
}
