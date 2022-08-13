package eu.cyzetlc.commentariis;

import eu.cyzetlc.commentariis.commands.*;
import eu.cyzetlc.commentariis.listener.*;
import eu.cyzetlc.commentariis.service.apply.ApplyHandler;
import eu.cyzetlc.commentariis.service.button.ButtonHandler;
import eu.cyzetlc.commentariis.service.command.CommandHandler;
import eu.cyzetlc.commentariis.service.database.mysql.MySQLCredentials;
import eu.cyzetlc.commentariis.service.database.mysql.QueryHandler;
import eu.cyzetlc.commentariis.service.json.JsonConfig;
import eu.cyzetlc.commentariis.service.log.LogHandler;
import eu.cyzetlc.commentariis.service.message.MessageHandler;
import eu.cyzetlc.commentariis.service.modal.ModalHandler;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

@Getter
public class Commentarii {
    // A static variable. It is used to access the instance of the class from anywhere.
    @Getter
    private static Commentarii instance;
    // Used to store the time when the bot was started.
    @Getter
    private static Long stated;
    // A logger. It is used to log things.
    public static Logger log = LoggerFactory.getLogger(Commentarii.class.getName());
    // A variable that is used to store the config.
    private final JsonConfig config;
    // A variable that is used build the JDA object.
    private JDABuilder jdaBuilder;
    // A variable that is used to store the JDA object.
    private JDA jda;
    // A variable that is used to store the CommandHandler object.
    private CommandHandler commandHandler;
    // Used to store the ButtonHandler object.
    private ButtonHandler buttonHandler;
    // Used to store the LogHandler object.
    private LogHandler logHandler;
    // Used to store the MessageHandler object.
    private MessageHandler messageHandler;
    // A variable that is used to store the QueryHandler object.
    private QueryHandler queryHandler;
    // A variable that is used to store the ModalHandler object.
    private ModalHandler modalHandler;
    // A variable that is used to store the ApplyHandler object.
    private ApplyHandler applyHandler;

    /**
     * The main function is the entry point of the program.
     */
    public static void main(String[] args) throws LoginException, InterruptedException {
        log.info("Initializing Commentarii-Instance");
        instance = new Commentarii();
    }

    private Commentarii() throws LoginException, InterruptedException {
        instance = this;
        stated = System.currentTimeMillis();

        this.config = new JsonConfig("./config.json");

        this.buildJDA();
        this.buildMySQLConnection();
        this.buildHandlers();
        this.buildListeners();
        this.buildCommands();

        new LogListener().onReady(new ReadyEvent(this.jda, 0));
    }

    /**
     * It creates the handlers for the plugin
     */
    private void buildHandlers() {
        this.applyHandler = new ApplyHandler();
        this.buttonHandler = new ButtonHandler();
        this.modalHandler = new ModalHandler();
        this.logHandler = new LogHandler();
        this.messageHandler = new MessageHandler();
        this.messageHandler.applyPrefix("commentarii", "commentarii.prefix");
    }

    /**
     * It creates a new QueryHandler object with the credentials from the config file, and then creates a table if it
     * doesn't exist
     */
    private void buildMySQLConnection() {
        this.queryHandler = new QueryHandler(new JsonConfig(this.config.getObject().getJSONObject("mysql")).load(MySQLCredentials.class));
        this.queryHandler.createBuilder("CREATE TABLE IF NOT EXISTS logs(numeric_id INT UNIQUE AUTO_INCREMENT, timestamp BIGINT, thread VARCHAR(64), guild_id BIGINT, text TEXT);").executeUpdateSync();
        this.queryHandler.createBuilder("CREATE TABLE IF NOT EXISTS settings(numeric_id INT UNIQUE AUTO_INCREMENT, guild_id BIGINT, language VARCHAR(3), log_channel BIGINT, apply_channel BIGINT);").executeUpdateSync();
    }

    /**
     * It adds a listener to the JDA object
     */
    private void buildListeners() {
        this.jda.addEventListener(new CommandListener());
        this.jda.addEventListener(new ButtonListener());
        this.jda.addEventListener(new JoinGuildListener());
        this.jda.addEventListener(new LogListener());
        this.jda.addEventListener(new ModalListener());
    }

    /**
     * It loads the command into the command handler
     */
    private void buildCommands() {
        this.commandHandler = new CommandHandler();
        this.commandHandler.loadCommand(new LanguageCommand());
        this.commandHandler.loadCommand(new InfoCommand());
        this.commandHandler.loadCommand(new LogChannelCommand());
        this.commandHandler.loadCommand(new GuildCommand());
        this.commandHandler.loadCommand(new BroadcastCommand());
        this.commandHandler.loadCommand(new ApplyCommand());
        this.commandHandler.loadCommand(new ApplyChannelCommand());
    }

    /**
     * It builds the JDA object and sets the activity
     */
    private void buildJDA() throws LoginException, InterruptedException {
        this.jdaBuilder = JDABuilder.create(this.config.getObject().getString("token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
        this.jdaBuilder.setActivity(Activity.of(Activity.ActivityType.valueOf(this.config.getObject().getString("activityType")), this.config.getObject().getString("activityContent")));
        this.jda = this.jdaBuilder.build().awaitReady();

        log.info("Invite me: " + this.jda.getInviteUrl(Permission.ADMINISTRATOR));
        log.info("Bot loggedIn as " + this.jda.getSelfUser().getName() + "#" + this.getJda().getSelfUser().getDiscriminator() + " <@" + this.jda.getSelfUser().getId() + ">");
    }
}
