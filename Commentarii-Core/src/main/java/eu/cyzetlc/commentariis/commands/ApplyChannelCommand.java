package eu.cyzetlc.commentariis.commands;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.command.Command;
import eu.cyzetlc.commentariis.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentariis.service.entities.Embed;
import eu.cyzetlc.commentariis.service.entities.User;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

@CommandSpecification(
        command = "applychannel",
        description = "Sets the channel for the applications"
)
public class ApplyChannelCommand extends Command {
    @Override
    public void initialize(CommandSpecification spec) {
        super.initialize(spec);
        this.getCommandData().addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel for the applications").setRequired(true));
    }

    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        if (event.getOption("channel") != null) {
            GuildChannel applyChannel = event.getOption("channel").getAsChannel();

            Commentarii.getInstance().getApplyHandler().apply(
                    applyChannel.getGuild(), applyChannel.getGuild().getTextChannelById(applyChannel.getId())
            );
            this.sendEmbed(Embed.getEmbed(
                    "**Hura**",
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(
                            channel.getGuild().getIdLong(),
                            "commentarii.command.applychannel.changed",
                            applyChannel.getAsMention()
                            ),
                    Color.GREEN
            ));
        }
    }
}
