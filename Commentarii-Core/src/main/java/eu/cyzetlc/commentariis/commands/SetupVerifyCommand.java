package eu.cyzetlc.commentariis.commands;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.buttons.VerifyButton;
import eu.cyzetlc.commentariis.service.command.Command;
import eu.cyzetlc.commentariis.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentariis.service.entities.Embed;
import eu.cyzetlc.commentariis.service.entities.User;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.List;

@CommandSpecification(
        command = "setup-verify",
        description = "Setup for the verify system"
)
public class SetupVerifyCommand extends Command {
    @Override
    public void initialize(CommandSpecification spec) {
        super.initialize(spec);
        this.getCommandData().addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel where users should execute the verify command").setRequired(true));
        this.getCommandData().addOptions(new OptionData(OptionType.ROLE, "role", "The verification-role").setRequired(true));
    }

    @Override
    // This is the method that is called when the command is executed.
    public void onCommand(User user, SlashCommandInteractionEvent event, TextChannel channel, String[] args) {
        if (event.getOption("channel") != null && event.getOption("role") != null) {
            TextChannel verifyChannel = event.getOption("channel").getAsChannel().asTextChannel();
            Role role = event.getOption("role").getAsRole();

            if (event.getGuild() != null) {
                verifyChannel.createWebhook("verify").queue((createdWebhook) -> {
                    Commentarii.getInstance().getVerifyHandler().apply(event.getGuild(), createdWebhook.getIdLong());
                    Commentarii.getInstance().getVerifyHandler().apply(event.getGuild(), createdWebhook.getUrl());
                });
                Commentarii.getInstance().getVerifyHandler().apply(event.getGuild(), verifyChannel);

                verifyChannel.sendMessageEmbeds(Embed.getEmbed(
                        "**Verifiziere dich**",
                        "Klicke auf verifizieren um alle grundlegenden Channel zu sehen.\nBitte bedenke, dass dieses Verfahren einige Sekunden in Anspruch nehemn kann.",
                        Color.GREEN
                ).build()).setActionRow(List.of(Commentarii.getInstance().getButtonHandler().register(new VerifyButton()))).queue();
            }

            this.sendEmbed(Embed.getEmbed(
                    "**Hura**",
                    Commentarii.getInstance().getMessageHandler().getMessageForGuild(
                            channel.getGuild().getIdLong(),
                            "commentarii.command.verify.setup",
                            verifyChannel.getAsMention(), role.getAsMention()
                            ),
                    Color.GREEN
            ));
        }
    }
}
