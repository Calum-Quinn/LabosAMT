package ch.heigvd.amt.commands;

import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.discord.CommandInterface;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.BetRepository;
import ch.heigvd.amt.repository.UserRepository;
import ch.heigvd.amt.service.BetService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.component.SelectMenu.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Used by the creator of a bet to define the winning outcome and start the payout process
 */
@ApplicationScoped
public class ClosureCommand implements CommandInterface {
    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final BetService betService;

    @Inject
    public ClosureCommand(UserRepository userRepository, BetRepository betRepository, BetService betService) {
        this.userRepository = userRepository;
        this.betRepository = betRepository;
        this.betService = betService;
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        long userId = event.getInteraction().getUser().getId().asLong();
        String username = event.getInteraction().getUser().getUsername();

        User creator = userRepository.findOrCreateById(userId, username);

        // Fetch bets created by the user that are closed but have no outcome yet
        List<Bet> betsToClose = betRepository.listClosableBetsForCreator(creator);

        if (betsToClose.isEmpty()) {
            return event.reply("You have no bets to close.").withEphemeral(true);
        }

        List<Option> options = betsToClose.stream()
                .map(bet -> Option.of(bet.getName(), bet.getId().toString()))
                .toList();

        SelectMenu selectMenu = SelectMenu.of("close_bet_menu", options);
        ActionRow actionRow = ActionRow.of(selectMenu);

        return event
                .reply("Select a bet to close:")
                .withComponents(actionRow)
                .withEphemeral(true);
    }

    @Override
    public String commandName() {
        return "close-bet";
    }

    // Handle the selected bet from the dropdown
    @Transactional
    public Publisher<Void> handleBetSelection(SelectMenuInteractionEvent event) {
        if (!event.getCustomId().equals("close_bet_menu")) {
            return Mono.empty();
        }

        String selectedBetId = event.getValues().getFirst(); // Get the selected bet ID from the dropdown
        Bet selectedBet = betRepository.findById(UUID.fromString(selectedBetId));

        if (selectedBet == null) {
            return event.reply("Bet not found.").withEphemeral(true);
        }

        // Create buttons for closing the bet
        ActionRow buttonRow = ActionRow.of(
                Button.primary("close_bet_for_" + selectedBetId, "Mark as For"),
                Button.danger("close_bet_against_" + selectedBetId, "Mark as Against")
        );
        return Mono.fromCompletionStage(() ->
                event.reply("Choose the closing decision for \"" + selectedBet.getName() + "\":")
                        .withComponents(buttonRow)
                        .withEphemeral(true)
                        .toFuture()
        );
    }

    // Handle button click interactions for closing the bet
    @Transactional
    public Publisher<Void> handleButtonInteraction(ComponentInteractionEvent event) {
        String customId = event.getCustomId();
        if (!customId.startsWith("close_bet_for_") && !customId.startsWith("close_bet_against_")) {
            return Mono.empty();
        }

        String[] parts = customId.split("_");

        String decision = parts[2]; // "win" or "lose"
        String selectedBetId = parts[3]; // Extract bet ID

        Bet bet = betRepository.findById(UUID.fromString(selectedBetId));
        if (bet == null) {
            return event.reply("Bet not found.").withEphemeral(true);
        }
        else if (bet.getOutcome() != null) {
            return event.reply("Bet is already closed.").withEphemeral(true);
        }

        BetOutcome outcome = decision.equals("for") ? BetOutcome.FOR : BetOutcome.AGAINST;
        betService.closeBet(bet, outcome);

        return event.reply("Bet marked as " + (outcome == BetOutcome.FOR ? "For" : "Against") + ".").withEphemeral(true);
    }
}
