package eu.cyzetlc.commentarii.commands;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.command.Command;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentarii.service.entities.Embed;
import eu.cyzetlc.commentarii.service.entities.User;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

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
                Commentarii.getInstance().getVerifyHandler().apply(event.getGuild(), verifyChannel);
                Commentarii.getInstance().getVerifyHandler().apply(event.getGuild(), role);
                verifyChannel.createWebhook("verify").queue((createdWebhook) -> {
                    Commentarii.getInstance().getVerifyHandler().apply(event.getGuild(), createdWebhook.getIdLong());
                    Commentarii.getInstance().getVerifyHandler().apply(event.getGuild(), createdWebhook.getUrl());
                });

                verifyChannel.sendMessageEmbeds(Embed.getEmbed(
                        "**Verifiziere dich**",
                        "Um dich zu verifizieren hast du eine private Nachricht von mir erhalten.\nFalls dies nicht der Fall sein sollte, dann aktiviere die Private-Nachrichten. Dafür musst du einfach auf den Servernamen klicken und danach auf Privatsphäre Einstellungen.\nDanach musst du nur noch einmal `/resend` ausführen.",
                        Color.GREEN
                ).build()).queue();
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
