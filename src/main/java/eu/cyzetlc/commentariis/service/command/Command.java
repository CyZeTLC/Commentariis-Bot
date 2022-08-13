package eu.cyzetlc.commentariis.service.command;

import com.google.common.base.Joiner;
import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.button.Button;
import eu.cyzetlc.commentariis.service.command.annotation.CommandSpecification;
import eu.cyzetlc.commentariis.service.entities.Embed;
import eu.cyzetlc.commentariis.service.entities.User;
import eu.cyzetlc.commentariis.service.log.LogHandler;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class Command {
    // Creating a logger object.
    public static Logger log = LoggerFactory.getLogger(Command.class.getName());

    // Declaring a variable called command of type String.
    private String command;
    // Declaring a private variable called permission.
    private String permission;
    // Declaring a variable called aliases of type String array.
    private String[] aliases;
    // Declaring a variable called description of type String.
    private String description;
    // Creating a variable called cooldownUnit of type CommandSpecification.TimeUnit.
    private CommandSpecification.TimeUnit cooldownUnit;
    // Declaring a variable called cooldownValue and setting it to a value of 0.
    private long cooldownValue;
    // Creating a temporary channel for the user to use.
    protected TextChannel tempChannel;
    // Creating a temporary event that will be used to store the event that is passed to the onCommand method.
    protected SlashCommandEvent tempEvent;
    // Creating a new instance of the CommandData class.
    private CommandData commandData;
    // Creating a list of subcommands.
    private final List<Command> subCommands = new LinkedList<>();

    // Creating a new HashMap called cooldowns.
    protected static Map<String, HashMap<String, Long>> cooldowns = new LinkedHashMap<>();

    public Command() {

    }

    /**
     * It takes the command specification and sets the command, permission, aliases, cooldown unit, cooldown value, and
     * description to the command specification's values
     *
     * @param spec The CommandSpecification annotation that is used to specify the command.
     */
    public void initialize(CommandSpecification spec) {
        this.command = spec.command();
        this.permission = spec.permission();
        this.aliases = spec.aliases();
        this.cooldownUnit = spec.cooldownType();
        this.cooldownValue = spec.cooldownValue();
        this.description = spec.description();
        this.commandData = new CommandData(this.command, this.description);

        cooldowns.put(this.command, new HashMap<>());
    }

    /**
     * It adds the command to the command handler
     */
    public void register() {
        try {
            //Commentarii.getInstance().getCommandHandler().jdaCommands.addCommands(this.commandData).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * It checks if the user has the required permissions, if the command is on cooldown and if the command has
     * subcommands. If the command has subcommands, it will check if the user has entered a subcommand and execute it. If
     * the command has no subcommands, it will execute the command
     *
     * @param user The user who executed the command
     * @param event The event that triggered the command
     * @param channel The channel where the command was executed
     * @param args The arguments of the command.
     */
    public void execute(User user, SlashCommandEvent event, TextChannel channel, String[] args) {
        Commentarii.getInstance().getLogHandler().log(
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.command.executed.title"),
                Commentarii.getInstance().getMessageHandler().getMessageForGuild(channel.getGuild().getIdLong(), "commentarii.command.executed.content", user.getJdaUser().getAsMention(), event.getCommandString()),
                LogHandler.LogLevel.INFO,
                channel.getGuild().getIdLong(),
                log
        );
        this.tempChannel = channel;
        this.tempEvent = event;

        if (this.getPlayerCooldown(user.getJdaUser().getId()) > System.currentTimeMillis()) {
            this.sendEmbed(Embed.getEmbed("**Warte kurz!**", "Du musst noch " + this.formatTimeToString(
                    System.currentTimeMillis() - this.getPlayerCooldown(user.getJdaUser().getId())) + " warten!", Color.RED));
            return;
        } else {
            if (this.getCooldownValue() > 0) {
                HashMap<String, Long> time = new HashMap<>();

                if (!Command.cooldowns.get(this.getCommand()).isEmpty()) {
                    time = Command.cooldowns.get(this.getCommand());
                }
                time.put(user.getJdaUser().getId(), System.currentTimeMillis() + (this.getCooldownValue() * this.getCooldownUnit().getValue()));

                Command.cooldowns.put(this.getCommand(), time);
            }
        }

        if (this.permission != null && !this.permission.equals("")) {
            if (!user.hasPermission(this.permission)) {
                channel.sendMessageEmbeds(Embed.getEmbed("**Keine Rechte!**", "Dir fehlen folgende Berechtigung: **" + this.permission + "**", Color.RED).build())
                        .queue(message -> this.deleteAfter(message, 5));
                return;
            }
        }

        if (this.getSubCommands().size() > 0 && args.length > 1) {
            if (this.getSubCommand(args[1]) != null) {
                Command baseCommand = this.getSubCommand(args[1]);
                String[] newArgs = new String[args.length-1];
                baseCommand.tempChannel = channel;

                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                baseCommand.execute(user,event,channel,newArgs);
                return;
            }
        }

        String[] newArgs = new String[args.length-1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);

        try {
            this.onCommand(user, event, channel, newArgs);
        } catch (Exception e) {
            this.sendEmbed(Embed.getEmbed("**Hoppla!**", "Bei ausführen des Commands `" +
                    this.getClass().getAnnotation(CommandSpecification.class).command() + "` ist ein Fehler aufgetreten.", Color.RED));
            e.printStackTrace();
        }
    }

    /**
     * It takes a long value in milliseconds and returns a string with the time in a human readable format
     *
     * @param millis The time in milliseconds
     * @return The time in a string format.
     */
    public String formatTimeToString(long millis) {
        List<String> items = new ArrayList<>();
        long milliseconds = millis % 1000L;
        float seconds = (float)(millis / 1000L % 60L);
        long minutes = millis / 60000L % 60L;
        long hours = millis / 3600000L;
        if (hours >= 24L) {
            return " bis zum " + (new SimpleDateFormat("dd.MM.yyyy um HH.mm")).format(new Date(System.currentTimeMillis() + millis)) + " ";
        } else {
            if (hours > 0L) {
                items.add(hours + " Stunde" + (hours == 1L ? "" : "n"));
            }

            if (minutes > 0L) {
                items.add(minutes + " Minute" + (minutes == 1L ? "" : "n"));
            }

            if (seconds > 0.0F || milliseconds > 0L) {
                items.add((new DecimalFormat("#.##")).format((seconds + this.mapValue((float)milliseconds, 1000.0F, 0.6F))) + " Sekunden");
            }

            return Joiner.on(", ").join(items);
        }
    }

    /**
     * It takes a command, initializes it, and adds it to the list of subcommands
     *
     * @param baseCommand The command to register
     */
    public void registerSubCommand(Command baseCommand) {
        baseCommand.initialize(baseCommand.getClass().getAnnotation(CommandSpecification.class));
        this.subCommands.add(baseCommand);
        this.commandData.addOptions(Collections.singleton(new OptionData(OptionType.STRING, baseCommand.command, baseCommand.description)));
    }

    /**
     * It adds an option to the command
     */
    public void enableArgs() {
        this.commandData.addOptions(Collections.singleton(new OptionData(OptionType.STRING, "args", "The following arguments for command")));
    }

    /**
     * It loops through all the subcommands of the command, and if the command or any of its aliases match the command, it
     * returns the command
     *
     * @param command The command name.
     * @return A Command object
     */
    public Command getSubCommand(String command) {
        for (Command baseCommand : this.subCommands) {
            if (baseCommand.command != null) {
                if (baseCommand.command.equals(command.toLowerCase()) || Arrays.asList(baseCommand.aliases).contains(command.toLowerCase())) {
                    return baseCommand;
                }
            }
        }
        return null;
    }

    /**
     * > It takes a value, a maximum input value, and a maximum output value, and returns the value mapped to the output
     * range
     *
     * @param value The value to be mapped.
     * @param maxIn The maximum value of the input range.
     * @param maxOut The maximum value you want to map to.
     * @return The value of the maxOut multiplied by the value divided by the maxIn.
     */
    private float mapValue(float value, float maxIn, float maxOut) {
        return maxOut * (value / maxIn);
    }

    /**
     * If the command exists in the cooldowns map, and the player exists in the command's map, return the player's
     * cooldown. Otherwise, return -1
     *
     * @param id The player's UUID
     * @return The cooldown of the player.
     */
    public long getPlayerCooldown(String id) {
        return cooldowns.containsKey(this.command) ? (cooldowns.get(this.command).containsKey(id) ? cooldowns.get(this.command).get(id) : -1) : -1;
    }

    /**
     * It sends an embed to the channel
     *
     * @param embed The embed to send
     */
    public void sendEmbed(Embed embed) {
        this.sendEmbed(embed,false, true);
    }

    /**
     * It sends an embed to the channel that the command was sent in
     *
     * @param embed The embed to send
     * @param autoDelete If true, the message will be deleted after 5 seconds.
     * @param isReply If you want to reply to the command message, set this to true.
     */
    public void sendEmbed(Embed embed, boolean autoDelete, boolean isReply) {
        if (isReply) {
            Collection<MessageEmbed> embeds = new ArrayList<>();
            embeds.add(embed.build());
            this.tempEvent.replyEmbeds(embeds).queue();
        } else {
            if (this.tempChannel != null) {
                if (autoDelete) {
                    this.tempChannel.sendMessageEmbeds(embed.build()).queue(msg -> this.deleteAfter(msg, 5));
                } else {
                    this.tempChannel.sendMessageEmbeds(embed.build()).queue();
                }
            }
        }
    }

    /**
     * This function sends a message to the server.
     *
     * @param message The message to send
     */
    public void sendMessage(String message) {
        this.sendMessage(message,false,true);
    }

    /**
     * If the message is a reply, reply to the event. If it's not a reply, send the message to the temporary channel. If
     * the temporary channel is null, do nothing
     *
     * @param message The message to send
     * @param autoDelete If the message should be deleted after a certain amount of time.
     * @param isReply If true, the message will be sent as a reply to the command message.
     */
    public void sendMessage(String message, boolean autoDelete, boolean isReply) {
        if (isReply) {
            this.tempEvent.reply(message).queue();
        } else {
            if (this.tempChannel != null) {
                if (autoDelete) {
                    this.tempChannel.sendMessage(message).queue(msg -> this.deleteAfter(msg, 5));
                } else {
                    this.tempChannel.sendMessage(message).queue();
                }
            }
        }
    }

    /**
     * Sends an embed with buttons to the user.
     *
     * @param embed The embed you want to send.
     * @param buttons A list of buttons to be added to the embed.
     */
    public void sendEmbedWithButtons(Embed embed, List<Button> buttons) {
        this.sendEmbedWithButtons(embed, buttons, true, false);
    }

    /**
     * It sends an embed with buttons to the temporary channel
     *
     * @param embed The embed you want to send.
     * @param buttons A list of buttons to be added to the embed.
     * @param autoDelete If the message should be deleted after a certain amount of time.
     */
    public void sendEmbedWithButtons(Embed embed, List<Button> buttons, boolean isReply, boolean autoDelete) {
        LinkedList<net.dv8tion.jda.api.interactions.components.Button> list = new LinkedList<>();
        for (Button btn : buttons) {
            list.add(Commentarii.getInstance().getButtonHandler().register(btn));
        }

        if (isReply) {
            Collection<MessageEmbed> embeds = new ArrayList<>();
            embeds.add(embed.build());
            this.tempEvent.replyEmbeds(embeds).addActionRow(list).queue();
        } else if (this.tempChannel != null) {
            if (autoDelete) {
                this.tempChannel.sendMessageEmbeds(embed.build()).setActionRow(list).queue(msg -> this.deleteAfter(msg, 5));
            } else {
                this.tempChannel.sendMessageEmbeds(embed.build()).setActionRow(list).queue();
            }
        }
    }

    /**
     * Delete the message after the specified delay.
     *
     * @param message The message to delete.
     * @param delay The amount of time to wait before deleting the message.
     */
    public void deleteAfter(Message message, int delay) {
        message.delete().queueAfter(delay, TimeUnit.SECONDS);
    }

    /**
     * This function is called when a user uses a command
     *
     * @param user The user who sent the command
     * @param event The event that triggered the command.
     * @param channel The channel the command was sent in
     * @param args The arguments passed to the command.
     */
    public abstract void onCommand(User user, SlashCommandEvent event, TextChannel channel, String[] args);
}
