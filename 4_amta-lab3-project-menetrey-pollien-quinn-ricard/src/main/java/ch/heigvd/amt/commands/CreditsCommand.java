package ch.heigvd.amt.commands;

import ch.heigvd.amt.discord.CommandInterface;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.UserRepository;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * Used by users to view their current credit balance
 */
@ApplicationScoped
public class CreditsCommand implements CommandInterface {
    @Inject
    UserRepository userRepository;

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        User user = userRepository.findOrCreateById(event.getInteraction().getUser().getId().asLong(), event.getInteraction().getUser().getUsername());
        return event.editReply("You currently have " + user.getBalance() + " credits.").then();
    }

    @Override
    public String commandName() {
        return "credits";
    }
}
