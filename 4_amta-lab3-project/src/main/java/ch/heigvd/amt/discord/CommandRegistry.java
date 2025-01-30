package ch.heigvd.amt.discord;

import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to register the custom commands so they appear in the Discord interface
 */
@ApplicationScoped
public class CommandRegistry {

    private final Map<String, CommandInterface> commands = new HashMap<>();


    @Inject
    public CommandRegistry(@All List<CommandInterface> commands) {
        commands.forEach(command -> registerCommand(command.commandName(), command));
    }

    /**
     * Registers a new command with the specified name.
     *
     * @param name    The name of the command.
     * @param command The command implementation.
     */
    public void registerCommand(String name, CommandInterface command) {
        commands.put(name, command);
    }

    /**
     * Retrieves a command by its name.
     *
     * @param name The name of the command.
     * @return The command implementation or null if not found.
     */
    public CommandInterface getCommandHandler(String name) {
        return commands.get(name);
    }
}
