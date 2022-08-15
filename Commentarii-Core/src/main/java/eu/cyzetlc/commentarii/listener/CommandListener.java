package eu.cyzetlc.commentarii.listener;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.entities.Embed;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Commentarii.getInstance().getCommandHandler().updateCommands(event.getGuild());
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Commentarii.getInstance().getCommandHandler().updateCommands(event.getGuild());
    }

    @Override
    // This is a method that is called when a slash command is used.
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        String cmd = e.getName();
        String msg = e.getCommandString();

        if (e.isFromGuild()) {
            Command command = Commentarii.getInstance().getCommandHandler().getCommand(cmd);
            OptionMapping option = e.getOption("args");

            if (command != null) {
                command.execute(new eu.cyzetlc.commentarii.service.entities.User(e.getUser()), e, e.getChannel().asTextChannel(), option != null ?
                        option.getAsString().split(" ") : msg.split(" "));
            } else {
                e.getChannel().sendMessageEmbeds(Embed.getEmbed("**Ups!**", "Dieser Command wurde nicht gefunden!", Color.RED).build())
                        .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
            }
        } else {
            e.reply("Bitte verwende diesen Command über einen Discord-Server").queue();
        }
    }
}
