package ch.heigvd.amt.commands;

import ch.heigvd.amt.discord.CommandInterface;
import ch.heigvd.amt.repository.UserRepository;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

/**
 * Used by users to view the leaderboard of the users in order of their current balance
 */
@ApplicationScoped
public class LeaderboardCommand implements CommandInterface {

    @Inject
    UserRepository userRepository;

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        // Since we've already deferred the reply in the Bot class, we can just return a Mono<Void>
        return Mono.fromCallable(() -> userRepository.listLeaderboard())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(users -> {
                    var spec = EmbedCreateSpec.builder()
                            .title("Leaderboard")
                            .description(users.isEmpty()
                                    ? "No users found in the leaderboard."
                                    : users.stream()
                                    .map(user -> String.format("%s - Balance: %d", user.getUsername(), user.getBalance()))
                                    .collect(Collectors.joining("\n")))
                            .build();

                    return event.editReply()
                            .withEmbeds(spec)
                            .then();
                });
    }



    @Override
    public String commandName() {
        return "leaderboard";
    }
}
