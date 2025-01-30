package ch.heigvd.amt.service;

import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.discord.GatewayProvider;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.PlacedBet;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.BetRepository;
import ch.heigvd.amt.repository.PlacedBetRepository;
import ch.heigvd.amt.repository.UserRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import ch.heigvd.amt.Configuration;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Handles most of the interactions with the bets
 */
@ApplicationScoped
public class BetService {
    @Inject
    Configuration configuration;

    @Inject
    BetRepository repository;

    @Inject
    GatewayProvider gatewayProvider;

    @Channel("board-update-request")
    Emitter<String> boardUpdateRequest;
    @Inject
    BetRepository betRepository;
    @Inject
    PlacedBetRepository placedBetRepository;
    @Inject
    UserRepository userRepository;

    // Add a new bet to the database
    @Transactional
    public Bet addBet(String name, double oddsFor, double oddsAgainst, Instant closing, User creator) {
        try {
            if (betRepository.findByName(name) != null) {
                throw new IllegalArgumentException("Bet with name " + name + " already exists");
            }

            if (oddsFor < 1.1 || oddsFor > 10 || oddsAgainst < 1.1 || oddsAgainst > 10) {
                throw new IllegalArgumentException("Invalid odds, please try again with values between [1.1 ; 10]");
            }

            Bet bet = new Bet();
            bet.setName(name);
            bet.setOddsFor(oddsFor);
            bet.setOddsAgainst(oddsAgainst);
            bet.setClosing(closing);
            bet.setCreator(creator);

            betRepository.persist(bet);

            // Request an update of the image
            boardUpdateRequest.send(bet.getId().toString());

            return bet;
        } catch (Exception e) {
            if (e.getCause() instanceof jakarta.persistence.PersistenceException) {
                throw new IllegalArgumentException("A bet with this name already exists");
            }
            System.err.println("Error in addBet: " + e.getMessage());
            throw e;
        }
    }

    // Place a bet amount for or against an existing bet
    @Transactional
    public PlacedBet placeBet(User user, UUID betId, Long amount, BetOutcome outcome) {
        if (user.getBalance() == null || user.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        if(amount <= 0) {
            throw new IllegalArgumentException("The amount needs to be greater than zero");
        }

        // Lock the row to make sure no message consumer will read the row before the transaction finishes
        Bet bet = repository.findById(betId, LockModeType.PESSIMISTIC_WRITE);

        PlacedBet placedBet = new PlacedBet();
        placedBet.setUser(user);
        placedBet.setBet(bet);
        placedBet.setAmount(amount);
        placedBet.setDecision(outcome);

        // Add the new placement to the DB
        placedBetRepository.persist(placedBet);

        // Make sure the bet lists the new placement
        if(bet.getPlacedBets() == null) {
            bet.setPlacedBets(List.of(placedBet));
        }
        else {
            bet.getPlacedBets().add(placedBet);
        }

        repository.persist(bet);

        // Update the user's amount after deduction
        user.setBalance(user.getBalance() - amount);
        userRepository.persist(user);

        // Request an update of the bet in the channel
        boardUpdateRequest.send(bet.getId().toString());

        return placedBet;
    }


    // Set the ID of the Discord message that represents the bet
    @Transactional
    public void setMessageId(Bet bet, String messageId) {
        bet.setMessageId(messageId);
        repository.persist(bet);
    }

    // Define the winning outcome of the bet and payout the winnings
    @Transactional
    public void closeBet(Bet bet, BetOutcome outcome) {
        bet.setOutcome(outcome);
        repository.persist(bet);
        handlePayouts(bet, Mono.empty());
    }

    // Update the winning user's balances
    @Transactional
    public void handlePayouts(Bet bet, Mono<Void> discordNotification) {
        BetOutcome outcome = bet.getOutcome();
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome has not yet been set");
        }

        // Cycle through all bets placed on this particular bet
        for (PlacedBet placedBet : bet.getPlacedBets()) {
            // Check for winning bets
            boolean isWinningOutcome = outcome == placedBet.getDecision();
            if (isWinningOutcome) {
                double odds = outcome == BetOutcome.FOR ? bet.getOddsFor() : bet.getOddsAgainst();
                long payout = Math.round(placedBet.getAmount() * odds);

                User user = placedBet.getUser();
                user.setBalance(user.getBalance() + payout);
                userRepository.persist(user);

                // Notify user of payout
                discordNotification = discordNotification.then(
                        sendPayoutNotification(user.getId(), payout, user.getBalance(), bet.getName())
                );
            }
        }

        // Make sure all updates are sent before exiting
        discordNotification.block();
    }

    // Send a private message to the winning user with the amount they won
    private Mono<Void> sendPayoutNotification(long userId, long payout, long balance, String betName) {
        GatewayDiscordClient gateway = gatewayProvider.getGateway();
        return gateway.getUserById(Snowflake.of(userId))
                .flatMap(user -> user.getPrivateChannel()
                        .flatMap(channel -> channel.createMessage("Congratulations! You won " + payout +
                                " credits for *" + betName + "*. Your balance is now " + balance)).then());
    }

    // Retrieve the url to see the bet details
    public String getUrl(Bet bet) {
        return UriBuilder.fromUri(configuration.externalUrl().toString())
                .path("/bets/{id}")
                .build(bet.getId().toString())
                .toString();
    }
}
