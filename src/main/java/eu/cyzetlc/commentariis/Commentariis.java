package eu.cyzetlc.commentariis;

import eu.cyzetlc.commentariis.service.json.JsonConfig;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

@Getter
public class Commentariis {
    // A static variable. It is used to access the instance of the class from anywhere.
    private static Commentariis instance;
    // A logger. It is used to log things.
    public static Logger log = LoggerFactory.getLogger(Commentariis.class.getName());

    // A variable that is used to store the config.
    private final JsonConfig config;

    // A variable that is used build the JDA object.
    private JDABuilder jdaBuilder;
    // A variable that is used to store the JDA object.
    private JDA jda;

    /**
     * The main function is the entry point of the program.
     */
    public static void main(String[] args) throws LoginException, InterruptedException {
        log.info("Initializing Commentariis-Instance");
        instance = new Commentariis();
    }

    private Commentariis() throws LoginException, InterruptedException {
        instance = this;

        this.config = new JsonConfig("./config.json");

        this.buildJDA();
    }

    /**
     * It builds the JDA object and sets the activity
     */
    private void buildJDA() throws LoginException, InterruptedException {
        this.jdaBuilder = JDABuilder.createDefault(this.config.getObject().getString("token"));
        this.jdaBuilder.setActivity(Activity.of(Activity.ActivityType.valueOf(this.config.getObject().getString("activityType")), this.config.getObject().getString("activityContent")));
        this.jda = this.jdaBuilder.build().awaitReady();

        log.info("Invite me: " + this.jda.getInviteUrl(Permission.ADMINISTRATOR));
        log.info("Bot loggedin as " + this.jda.getSelfUser().getName() + "#" + this.getJda().getSelfUser().getDiscriminator() + " <@" + this.jda.getSelfUser().getId() + ">");
    }
}
