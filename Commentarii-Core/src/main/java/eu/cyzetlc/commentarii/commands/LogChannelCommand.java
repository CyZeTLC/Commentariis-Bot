package eu.cyzetlc.commentarii.commands;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentarii.service.entities.Embed;
import eu.cyzetlc.commentarii.service.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

@CommandSpecification(
        command = "logchannel",
        description = "Sets the log channel"
)
public class LogChannelCommand extends Command {
    @Override
    public void initialize(CommandSpecification spec) {
        super.initialize(spec);
        this.getCommandData().addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel for the logs").setRequired(true));
    }

    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        if (event.getOption("channel") != null) {
            GuildChannel logChannel = event.getOption("channel").getAsChannel();

            Commentarii.getInstance().getLogHandler().apply(
                    logChannel.getGuild(), logChannel.getGuild().getTextChannelById(logChannel.getId())
            );
            this.sendEmbed(Embed.getEmbed(
                    "**Hura**",
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(
                            channel.getGuild().getIdLong(),
                            "commentarii.command.logchannel.changed",
                            logChannel.getAsMention()
                            ),
                    Color.GREEN
            ));
        }
    }
}
