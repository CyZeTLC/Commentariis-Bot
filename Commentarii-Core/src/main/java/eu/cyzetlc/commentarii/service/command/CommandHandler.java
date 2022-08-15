package eu.cyzetlc.commentarii.service.command;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.command.annotation.CommandSpecification;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandHandler {
    // Creating a new LinkedList of type Command.
    private final LinkedList<Command> commands = new LinkedList<>();

    public void updateCommands(Guild guild) {
        for (net.dv8tion.jda.api.interactions.commands.Command cmd : guild.retrieveCommands().complete()) {
            guild.deleteCommandById(cmd.getId()).queue();
        }

        List<CommandData> commandData = new LinkedList<>();

        for (Command cmd : this.commands) {
            commandData.add(cmd.getCommandData());
            Commentarii.log.info("Registered command " + cmd.getCommand());
        }
        Commentarii.getInstance().getJda().updateCommands().addCommands(commandData).queue();
    }

    /**
     * Loop through all the commands, and if the command name or alias matches the command name passed in, return the
     * command.
     *
     * @param command The command that the user typed in.
     * @return A Command object
     */
    public Command getCommand(String command) {
        for (Command cmd : this.commands) {
            CommandSpecification clazz = cmd.getClass().getAnnotation(CommandSpecification.class);
            if (command.equalsIgnoreCase(clazz.command())) {
                return cmd;
            }

            for (String alias : clazz.aliases()) {
                if (alias.equalsIgnoreCase(command)) {
                    return cmd;
                }
            }
        }
        return null;
    }

    /**
     * It loads all classes in the package "eu.cyzetlc.discord.commands" and if the class extends the Command class, it
     * loads it
     */
    public void loadCommands() {
        for (Class<?> clazz : this.getAllClasses("eu.cyzetlc.discord.commands")) {
            if (clazz.getSuperclass().equals(Command.class)) {
                try {
                    Command command = (Command) clazz.newInstance();
                    this.loadCommand(command);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * It loads a command into the command manager
     *
     * @param command The command to load.
     */
    public void loadCommand(Command command) {
        if (command.getClass().isAnnotationPresent(CommandSpecification.class)) {
            command.initialize(command.getClass().getAnnotation(CommandSpecification.class));
            command.register();
            this.commands.add(command);
        }
    }

    /**
     * It reads all the lines from the packageName.txt file, filters out the lines that don't end with .class, and then
     * maps the remaining lines to a Class object
     *
     * @param packageName The name of the package to search for classes in.
     * @return A set of all the classes in the package.
     */
    public Set<Class<?>> getAllClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    /**
     * It takes a string of the form "com.example.MyClass" and returns the Class object for the class "MyClass" in the
     * package "com.example"
     *
     * @param className The name of the class to be loaded.
     * @param packageName The package name of the class you want to load.
     * @return The class object of the class that is being passed in.
     */
    private Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function returns the commands.
     *
     * @return The LinkedList of commands.
     */
    public LinkedList<Command> getCommands() {
        return commands;
    }
}
