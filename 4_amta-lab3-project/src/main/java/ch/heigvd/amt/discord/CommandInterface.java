package ch.heigvd.amt.discord;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

/**
 * Represents a basic user input command (e.g. /command-name)
 */
public interface CommandInterface {
    @Transactional
    Mono<Void> handle(ChatInputInteractionEvent event);

    String commandName();
}
