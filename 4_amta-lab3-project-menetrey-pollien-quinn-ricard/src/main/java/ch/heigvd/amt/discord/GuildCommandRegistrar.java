package ch.heigvd.amt.discord;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Discord4J provided file to register custom commands to the bot
 */
public class GuildCommandRegistrar {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final RestClient restClient;

    // The name of the folder the commands json is in, inside our resources folder
    private static final String commandsFolderName = "META-INF/commands/test/";

    public GuildCommandRegistrar(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Registers guild-specific commands dynamically by listing all JSON files in the commands folder.
     *
     * @param guildId The ID of the guild where commands should be registered.
     * @throws IOException if there's an issue reading the command files.
     */
    public void registerCommandsDynamically(long guildId) throws IOException {
        // Get a list of all JSON files in the commands folder
        List<String> fileNames = getCommandFileNames();

        // Register commands using the retrieved file names
        registerCommands(fileNames, guildId);
    }

    /**
     * Registers guild-specific commands for the specified guild.
     *
     * @param fileNames List of JSON file names defining commands.
     * @param guildId   The ID of the guild where commands should be registered.
     * @throws IOException if there's an issue reading the command files.
     */
    public void registerCommands(List<String> fileNames, long guildId) throws IOException {
        // Create an ObjectMapper that supports Discord4J classes
        final JacksonResources d4jMapper = JacksonResources.create();

        // Convenience variables for the sake of easier-to-read code
        final ApplicationService applicationService = restClient.getApplicationService();
        final long applicationId = restClient.getApplicationId().block();

        // Get our commands JSON from resources as command data
        List<ApplicationCommandRequest> commands = new ArrayList<>();
        for (String json : getCommandsJson(fileNames)) {
            ApplicationCommandRequest request = d4jMapper.getObjectMapper()
                    .readValue(json, ApplicationCommandRequest.class);

            commands.add(request); // Add to our list
        }

        /* Bulk overwrite commands for the specified guild. This will replace all existing guild-specific commands
         for the given guild with the commands provided here.
         */
        applicationService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, commands)
                .doOnNext(cmd -> LOGGER.debug("Successfully registered Guild Command " + cmd.name()))
                .doOnError(e -> LOGGER.error("Failed to register guild commands for guild ID: " + guildId, e))
                .subscribe();
    }

    /**
     * Gets a list of all JSON file names in the commands folder.
     *
     * @return List of file names in the commands folder.
     * @throws IOException if there's an issue accessing the folder.
     */
    private static List<String> getCommandFileNames() throws IOException {
        URL url = GuildCommandRegistrar.class.getClassLoader().getResource(commandsFolderName);
        if (url == null) {
            throw new FileNotFoundException(commandsFolderName + " could not be found");
        }

        File directory = new File(url.getFile());
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException(commandsFolderName + " is not a valid directory");
        }

        // List files ending with ".json"
        return List.of(Objects.requireNonNull(directory.list((dir, name) -> name.endsWith(".json"))));
    }

    public void clearCommands(long guildId) throws IOException {
        final ApplicationService applicationService = restClient.getApplicationService();
        final long applicationId = restClient.getApplicationId().block();

        applicationService.getGuildApplicationCommands(applicationId, guildId)
                .flatMap(command -> applicationService.deleteGuildApplicationCommand(applicationId, guildId, command.id().asLong()))
                .doOnError(e -> LOGGER.error("Failed to delete guild command", e))
                .subscribe();
    }

    /* The two below methods are boilerplate that can be completely removed when using Spring Boot */

    private static List<String> getCommandsJson(List<String> fileNames) throws IOException {
        // Confirm that the commands folder exists
        URL url = GuildCommandRegistrar.class.getClassLoader().getResource(commandsFolderName);
        Objects.requireNonNull(url, commandsFolderName + " could not be found");

        // Get all the files inside this folder and return the contents of the files as a list of strings
        List<String> list = new ArrayList<>();
        for (String file : fileNames) {
            String resourceFileAsString = getResourceFileAsString(commandsFolderName + file);
            list.add(Objects.requireNonNull(resourceFileAsString, "Command file not found: " + file));
        }
        return list;
    }

    /**
     * Gets a specific resource file as String.
     *
     * @param fileName The file path omitting "resources/"
     * @return The contents of the file as a String, otherwise throws an exception
     */
    private static String getResourceFileAsString(String fileName) throws IOException {
        try (InputStream resourceAsStream = GuildCommandRegistrar.class.getClassLoader().getResourceAsStream(fileName)) {
            if (resourceAsStream == null) {
                throw new FileNotFoundException("Resource file not found: " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}