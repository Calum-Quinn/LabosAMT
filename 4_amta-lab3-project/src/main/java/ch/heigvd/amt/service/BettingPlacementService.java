package ch.heigvd.amt.service;

import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.discord.GatewayProvider;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.BetRepository;
import ch.heigvd.amt.repository.PlacedBetRepository;
import ch.heigvd.amt.repository.UserRepository;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles when a user bets by pressing one of the buttons under the bet image
 */
@ApplicationScoped
public class BettingPlacementService {
    @Inject
    GatewayProvider gatewayProvider;
    @Inject
    UserRepository userRepository;
    @Inject
    BetRepository betRepository;
    @Inject
    PlacedBetRepository placedBetRepository;
    @Inject
    BetService betService;

    // Listen for interaction with the buttons under the bet
    public Mono<Void> handleButtonInteraction(ComponentInteractionEvent event) {
        String customId = event.getCustomId();
        if (customId.equals("bet-for") || customId.equals("bet-against")) {
            String messageId = event.getMessage()
                    .map(message -> message.getId().asString())
                    .orElseThrow(() -> new IllegalStateException("Message ID not found in the event."));

            return gatewayProvider.getGateway().getUserById(event.getInteraction().getUser().getId())
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(discordUser -> {
                        String username = discordUser.getUsername();
                        User user = userRepository.findOrCreateById(discordUser.getId().asLong(), username);

                        Bet bet = betRepository.findByMessageId(messageId); // Fetch Bet using messageId
                        if (bet == null) {
                            return event.reply("Bet not found. Please try again.").withEphemeral(true);
                        }

                        Instant now = Instant.now();
                        if (bet.getClosing() != null && now.isAfter(bet.getClosing())) {
                            return event.reply("This bet is already closed.").withEphemeral(true);
                        }

                        if (placedBetRepository.hasMadeABet(user, bet)) {
                            return event.reply("You have already made a bet!").withEphemeral(true);
                        }

                        double userBalance = user.getBalance() != null ? user.getBalance() : 0.0;
                        String modalTitle = "Bet your credits (Available: " + userBalance + " credits)";

                        TextInput amountInput = TextInput
                                .small("amountInput", "Enter your amount")
                                .required(true);

                        // Wrap the TextInput in an ActionRow
                        ActionRow actionRow = ActionRow.of(amountInput);

                        return event.presentModal(modalTitle, "bet_modal_" + customId + "_" + bet.getId(), List.of(actionRow));
                    });
        }
        return Mono.empty();
    }


    // Handle bet amount submission
    @Transactional
    public Publisher<Void> handleModalSubmit(ModalSubmitInteractionEvent event) {
        if (event.getCustomId().startsWith("bet_modal_")) {
            return handleBetModal(event);
        }
        return Mono.empty();
    }

    public InteractionApplicationCommandCallbackReplyMono handleBetModal(ModalSubmitInteractionEvent event) {
        String[] parts = event.getCustomId().split("_");
        String decisionType = parts[2]; // "for" or "against"
        String betId = parts[3];

        long userId = event.getInteraction().getUser().getId().asLong();
        User user = userRepository.findById(userId);
        if (user == null) {
            return event.reply("User not found. Please try again.").withEphemeral(true);
        }

        String betAmountStr = event.getComponents(TextInput.class).stream()
                .filter(input -> "amountInput".equals(input.getCustomId()))
                .findFirst()
                .flatMap(TextInput::getValue)
                .orElse("");

        Optional<Long> amount = tryParse(betAmountStr);
        if (amount.isEmpty() || amount.get() <= 0) {
            return event.reply("Invalid amount entered. Please enter a valid number.").withEphemeral(true);
        }

        long betAmount = amount.get();
        long userBalance = user.getBalance() != null ? user.getBalance() : 0L;

        if (betAmount > userBalance) {
            return event.reply("Insufficient balance. You have " + userBalance + " credits available.").withEphemeral(true);
        }

        Bet bet = betRepository.findById(UUID.fromString(betId));
        if (bet == null) {
            return event.reply("Bet not found. Please try again.").withEphemeral(true);
        }

        BetOutcome decision = "bet-for".equals(decisionType) ? BetOutcome.FOR : BetOutcome.AGAINST;
        betService.placeBet(user, bet.getId(), betAmount, decision);

        return event.reply("You successfully bet " + betAmount + " credits " + decision + " *" + bet.getName() + "*.").withEphemeral(true);
    }

    private Optional<Long> tryParse(String str) {
        try {
            return Optional.of(Long.parseLong(str));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
