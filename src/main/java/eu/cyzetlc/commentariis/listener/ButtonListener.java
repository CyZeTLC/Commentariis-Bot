package eu.cyzetlc.commentariis.listener;

import eu.cyzetlc.commentariis.Commentariis;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        event.deferEdit().queue();
        if (Commentariis.getInstance().getButtonHandler().getButtons().get(event.getId()) != null) {
            Commentariis.getInstance().getButtonHandler().getButtons().get(event.getId()).handleClick(event);
        }
    }
}
