package eu.cyzetlc.commentariis.listener;

import eu.cyzetlc.commentariis.Commentariis;
import eu.cyzetlc.commentariis.buttons.InviteMeButton;
import eu.cyzetlc.commentariis.service.entities.Embed;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class JoinGuildListener extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Commentariis.log.info("Joined new guild: " + event.getGuild().getName());
        Commentariis.getInstance().getButtonHandler().sendEmbedWithButtons(
                Embed.getEmbed(
                        "**Moin!**",
                        "Ich freue mich, dass ihr euch für mich entschieden habt \uD83D\uDC4C.\nBenutzt einfach `/logchannel` um den Bot einzustellen",
                        Color.GREEN
                ),
                List.of(new InviteMeButton()),
                event.getGuild().getDefaultChannel(),
                false
        );
    }
}
