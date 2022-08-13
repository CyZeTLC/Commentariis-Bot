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
        command = "language",
        description = "Changes the Language of the Bot"
)
public class LanguageCommand extends Command {
    @Override
    public void initialize(CommandSpecification spec) {
        super.initialize(spec);

        OptionData optionData = new OptionData(OptionType.STRING, "languagekey", "The key of the langauge (de/en)").setRequired(true);
        optionData.addChoice("German", "de").addChoice("English", "en");
        this.getCommandData().addOptions(optionData);
    }

    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandEvent event, TextChannel channel, String[] args) {
        if (event.getOption("languagekey") != null) {
            String languageKey = event.getOption("languagekey").getAsString();

            Commentarii.getInstance().getMessageHandler().applyLanguage(channel.getGuild().getIdLong(), languageKey);
            this.sendEmbed(Embed.getEmbed(
                    "**Hura**",
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(
                            channel.getGuild().getIdLong(),
                            "commentarii.command.language.changed",
                            languageKey
                            ),
                    Color.GREEN
            ));
        }
    }
}
