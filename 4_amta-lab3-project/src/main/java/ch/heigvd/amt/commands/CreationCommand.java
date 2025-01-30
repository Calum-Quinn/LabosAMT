package ch.heigvd.amt.commands;

import ch.heigvd.amt.discord.CommandInterface;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.UserRepository;
import ch.heigvd.amt.service.BetService;
// Import the GatewayProvider
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Used to create a new bet that all users will see
 */
@ApplicationScoped
public class CreationCommand implements CommandInterface {
    @Inject
    BetService betService;

    @Inject
    UserRepository userRepository;

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        long userId = event.getInteraction().getUser().getId().asLong();
        String userName = event.getInteraction().getUser().getUsername();

        User creator = userRepository.findOrCreateById(userId, userName);

        String name = event.getOption("name").flatMap(ApplicationCommandInteractionOption::getValue).orElseThrow().asString();
        double oddsFor = event.getOption("odds_for").flatMap(ApplicationCommandInteractionOption::getValue).orElseThrow().asDouble();
        double oddsAgainst = event.getOption("odds_against").flatMap(ApplicationCommandInteractionOption::getValue).orElseThrow().asDouble();
        int closingDateOption = (int) event.getOption("closing_date").flatMap(ApplicationCommandInteractionOption::getValue).orElseThrow().asLong();

        // Make sure the user is not entering invalid odds
        if (oddsFor < 1.1 || oddsFor > 10 || oddsAgainst < 1.1 || oddsAgainst > 10) {
            return event.editReply("Invalid odds, please try again with values between [1.1 ; 10]").then();
        }

        try {
            Instant closingTime = switch (closingDateOption) {
                case 1 -> Instant.now().plus(1L, ChronoUnit.HOURS);
                case 2 -> Instant.now().plus(1L, ChronoUnit.DAYS);
                case 3 -> Instant.now().plus(7L, ChronoUnit.DAYS);
                default -> throw new RuntimeException("Invalid value.");
            };

            // Since we've already deferred the reply in the Bot class, we can just return a Mono<Void>
            return Mono.fromRunnable(() -> betService.addBet(name, oddsFor, oddsAgainst, closingTime, creator))
                    .subscribeOn(Schedulers.boundedElastic())
                    .then(event.editReply("Bet created successfully!")) // Confirmation message
                    .onErrorResume(e ->{
                        if (e instanceof IllegalArgumentException && e.getMessage().contains("already exists")) {
                            return event.editReply("A bet with this name already exists!");
                        }
                        return event.editReply("Error creating bet: " + e.getMessage());
                    }).then();

        } catch (Exception e) {
            return event.editReply("An error occurred while creating bet: " + e.getMessage())
                    .then();
        }

    }

    @Override
    public String commandName() {
        return "create-bet";
    }
}
