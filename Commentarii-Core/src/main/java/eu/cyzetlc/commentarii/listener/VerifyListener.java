package eu.cyzetlc.commentarii.listener;

import eu.cyzetlc.commentarii.Commentarii;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VerifyListener extends ListenerAdapter {
    @Override
    // A method that is called when a message is received.
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            if (Commentarii.getInstance().getVerifyHandler().getVerifyChannel(event.getGuild().getIdLong()) != null) {
                if (event.getChannel().getId().equals(Commentarii.getInstance().getVerifyHandler().getVerifyChannel(event.getGuild().getIdLong()).getId())) {
                    if (event.getMessage().isWebhookMessage()) {
                        event.getChannel().asTextChannel().retrieveWebhooks().queue(webhooks -> webhooks.forEach(webhook -> {
                            if (webhook.getOwnerAsUser() != null)
                                if (webhook.getOwnerAsUser().getId().equals(Commentarii.getInstance().getJda().getSelfUser().getId())) {
                                    if (webhook.getIdLong() == Commentarii.getInstance().getVerifyHandler().getWebhookIdByGuild(event.getGuild().getIdLong())) {
                                        if (event.getMessage().getEmbeds().size() > 0) {
                                            if (event.getMessage().getEmbeds().get(0) != null) {
                                                String desc = event.getMessage().getEmbeds().get(0).getDescription();
                                                if (desc != null) {
                                                    Member user = event.getGuild().getMemberById(Long.parseLong(desc));
                                                    if (user != null) {
                                                        Role role = Commentarii.getInstance().getVerifyHandler().getVerifyRole(event.getGuild().getIdLong());
                                                        if (!user.getRoles().contains(role)) {
                                                            event.getGuild().addRoleToMember(user, role).queue(val -> event.getMessage().delete().queue());
                                                        } else {
                                                            event.getMessage().delete().queue();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                        }));
                    }
                }
            }
        }
    }
}
