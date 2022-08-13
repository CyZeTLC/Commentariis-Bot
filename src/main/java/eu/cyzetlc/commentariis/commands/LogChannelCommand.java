package eu.cyzetlc.commentariis.commands;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.command.Command;
import eu.cyzetlc.commentariis.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentariis.service.entities.Embed;
import eu.cyzetlc.commentariis.service.entities.User;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

@CommandSpecification(
        command = "logchannel"
)
public class LogChannelCommand extends Command {
    @Override
    public void initialize(CommandSpecification spec) {
        super.initialize(spec);
        this.getCommandData().addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel for the logs").setRequired(true));
    }

    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandEvent event, TextChannel channel, String[] args) {
        if (event.getOption("channel") != null) {
            GuildChannel logChannel = event.getOption("channel").getAsGuildChannel();

            Commentarii.getInstance().getLogHandler().apply(
                    logChannel.getGuild(), logChannel.getGuild().getTextChannelById(logChannel.getId())
            );
            this.sendEmbed(Embed.getEmbed(
                    "**Hura**",
                    "Der Channel wurde erfolgreich zu " + logChannel.getAsMention() + " geändert!",
                    Color.GREEN
            ));
        }
    }
}
