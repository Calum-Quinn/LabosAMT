package ch.heigvd.amt.discord;

import ch.heigvd.amt.Configuration;
import ch.heigvd.amt.commands.ClosureCommand;
import ch.heigvd.amt.service.BettingPlacementService;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Main file for the Discord Bot implementation
 */
@Singleton
@Startup
public class Bot {

    @Inject
    GatewayProvider gatewayProvider;

    @Inject
    CommandRegistry commandRegistry;

    @Inject
    BettingPlacementService bettingPlacementService;

    @Inject
    ClosureCommand closureCommand;

    @Inject
    Configuration configuration;

    private GatewayDiscordClient gateway;

    // Registers the necessary so the bot can be interacted with in the server
    @PostConstruct
    public void start() {
        gateway = gatewayProvider.getGateway();

        Long guildId = configuration.guildId();
        if (guildId == null) {
            throw new IllegalStateException("Guild ID is missing!");
        }

        GuildCommandRegistrar guildCommandRegistrar = new GuildCommandRegistrar(gateway.getRestClient());

        // Get all json command file names
        List<String> commands = getFileNamesFromDirectory("META-INF/commands/test");
        if (commands.isEmpty()) {
            System.out.println("No commands found.");
        }

        // Register the commands so the Discord users will see them
        try {
            guildCommandRegistrar.registerCommands(commands, guildId);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to register commands: " + e.getMessage(), e);
        }

        // Handle incoming commands
        gateway.on(ChatInputInteractionEvent.class, event -> {
            String commandName = event.getCommandName();
            CommandInterface handler = commandRegistry.getCommandHandler(commandName);

            // Check if the command is one that takes a long time and needs to be deferred
            // so as not to exceed the Discord 3s timeout
            if ("create-bet".equals(commandName) || "leaderboard".equals(commandName) || "credits".equals(commandName)) {
                // Acknowledge the interaction and defer reply
                return event.deferReply().withEphemeral(true).then(
                        Mono.defer(() -> {
                            if (handler != null) {
                                return handler.handle(event);
                            } else {
                                return event.reply("Unknown command: " + commandName).withEphemeral(true);
                            }
                        })
                );
            } else {
                // Handle quick commands without deferring
                if (handler != null) {
                    return handler.handle(event);
                } else {
                    return event.reply("Unknown command: " + commandName).withEphemeral(true);
                }
            }
        }).subscribe();

        // Register event handlers for all commands
        gateway.on(SelectMenuInteractionEvent.class, selectMenuEvent -> closureCommand.handleBetSelection(selectMenuEvent)).subscribe();

        // Handle button interactions
        gateway.on(ComponentInteractionEvent.class, componentEvent -> {
            Mono<Void> placedResponse = bettingPlacementService.handleButtonInteraction(componentEvent);
            Mono<Void> closureResponse = Mono.from(closureCommand.handleButtonInteraction(componentEvent));
            return placedResponse.switchIfEmpty(closureResponse);
        }).subscribe();

        // Handle popup interactions
        gateway.on(ModalSubmitInteractionEvent.class, modalEvent ->
            // Handle modal submit interaction if needed
            bettingPlacementService.handleModalSubmit(modalEvent)
        ).subscribe();
    }

    // Stop the bot smoothly
    @PreDestroy
    public void stop() {
        if (gateway != null) {
            gateway.logout().block();
        }
    }

    private List<String> getFileNamesFromDirectory(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(directoryPath);

        if (resource != null) {
            File directory = new File(resource.getFile());
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            fileNames.add(file.getName());
                        }
                    }
                }
            } else {
                System.err.println("Directory " + directoryPath + " does not exist.");
            }
        } else {
            System.err.println("Directory not found or not a directory: " + directoryPath);
        }
        return fileNames;
    }
}
