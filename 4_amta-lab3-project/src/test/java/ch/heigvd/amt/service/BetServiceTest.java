package ch.heigvd.amt.service;

import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.PlacedBet;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.BetRepository;
import ch.heigvd.amt.repository.PlacedBetRepository;
import ch.heigvd.amt.repository.UserRepository;
import ch.heigvd.amt.test.TestInMemoryConnector;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(TestInMemoryConnector.class)
public class BetServiceTest {

    @Inject
    BetService betService;

    @Inject
    UserRepository userRepository;

    @Inject
    BetRepository betRepository;

    @Inject
    PlacedBetRepository placedBetRepository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        placedBetRepository.removeAll();
        betRepository.removeAll();
        userRepository.removeAll();
    }

    /**
     * Creating a new bet with valid values
     */
    @ParameterizedTest
    @CsvSource({
            "2.5, 1.5, 1",
            "1.8, 2.2, 2",
            "10, 10, 8760000",
    })
    public void testAddValidBet(double oddsFor, double oddsAgainst, int expiresInHours) {

        String name = "testBet";
        Instant closing = Instant.now().plus(expiresInHours, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        Bet addedBet = betRepository.findByName(name);
        Assertions.assertNotNull(addedBet, "The bet should be found in the database");
        Assertions.assertEquals(name, addedBet.getName(), "The bet name should be the same");
        Assertions.assertEquals(oddsFor, addedBet.getOddsFor(), "The odds for should be the same");
        Assertions.assertEquals(oddsAgainst, addedBet.getOddsAgainst(), "The odds against should be the same");
        Assertions.assertNull(addedBet.getOutcome(), "The outcome should not be set as the bet expiration was set to 1h");
        Assertions.assertTrue(Math.abs(closing.toEpochMilli() - addedBet.getClosing().toEpochMilli()) <= 1,"The closing times should be within 1 millisecond of each other");
        Assertions.assertNotNull(addedBet.getCreator(), "The creator should not be null");
        Assertions.assertEquals(creator.getId(), addedBet.getCreator().getId(), "The creator's id should be the same");
        Assertions.assertEquals(creator.getUsername(), addedBet.getCreator().getUsername(), "The creator's username should be the same");
        Assertions.assertEquals(creator.getBalance(), addedBet.getCreator().getBalance(), "The creator's balance should be the same");
    }

    /**
     * Creating a new bet with invalid values
     */
    @ParameterizedTest
    @CsvSource({
            "-1, 1.5, 1",           // Invalid odds for
            "0, 1.5, 1",            // Invalid odds for
            "1, 1.5, 1",            // Invalid odds for
            "10.1, 1.5, 1",         // Invalid odds for
            "1.5, -1, 1",           // Invalid odds against
            "1.5, 0, 1",            // Invalid odds against
            "1.5, 1, 1",            // Invalid odds against
            "1.5, 10.1, 1",         // Invalid odds against
            "1.5, 0, -1",           // Invalid expiration
            "-1, 11, -2147483648",  // Invalid everything
    })
    public void testAddInvalidBet(double oddsFor, double oddsAgainst, int expiresInHours) {

        String name = "testBet";
        Instant closing = Instant.now().plus(expiresInHours, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        assertThrows(IllegalArgumentException.class, () -> {
            betService.addBet(name, oddsFor, oddsAgainst, closing, creator);
        }, "IllegalArgumentException should be raised");

        Bet bet = betRepository.findByName(name);
        assertNull(bet, "The bet should be not be created");
    }

    /**
     * Placing a wager on an existing bet
     */
    @Transactional
    @ParameterizedTest
    @CsvSource({
            "2.5, 1.5, 1, FOR, 100",
            "2.5, 1.5, 1, AGAINST, 100",
            "1.8, 2.2, 2, FOR, 1",
            "1.8, 2.2, 2, AGAINST,1",
            "10, 10, 8000, FOR, 12345",
            "10, 10, 8000, AGAINST, 12345",
    })
    public void testPlaceValidBet(double oddsFor, double oddsAgainst, int expiresInHours, BetOutcome outcome, Long amount) {

        String name = "testBetPlace";
        Instant closing = Instant.now().plus(expiresInHours, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(3L, "creator");

        betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        Bet bet = betRepository.findByNameWithPlacedBets(name);

        betService.placeBet(creator, bet.getId(), amount, outcome);

        List<PlacedBet> placedBets = placedBetRepository.getAllForBet(bet);
        PlacedBet placedBet = placedBets.stream()
                .filter(pb -> pb.getUser().equals(creator))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(bet, "The bet should be found in the database");
        Assertions.assertNotNull(placedBet, "The placed bet should be found in the database");
        Assertions.assertEquals(amount, placedBet.getAmount(), "The placed bet amount should be the same");
        Assertions.assertEquals(outcome, placedBet.getDecision(), "The placed bet decision should be the same");
        Assertions.assertEquals(creator, placedBet.getUser(), "The user who placed the bet should be the same");
        Assertions.assertEquals(bet, placedBet.getBet(), "The wager should be placed on the correct bet");
    }

    /**
     * Placing an invalid wager on an existing bet
     */
    @ParameterizedTest
    @CsvSource({
            "2.5, 1.5, 1, FOR, 0",
            "2.5, 1.5, 1, AGAINST, 0",
            "10, 2.2, 2, FOR, -1",
            "10, 2.2, 2, AGAINST,-1",
            "2.2, 10, 8000, FOR, 2147483648",
            "2.2, 10, 8000, AGAINST, 2147483648",
    })
    public void testPlaceInvalidBet(double oddsFor, double oddsAgainst, int expiresInHours, BetOutcome outcome, Long amount) {

        String name = "testBetPlace";
        Instant closing = Instant.now().plus(expiresInHours, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        Bet bet = betRepository.findByNameWithPlacedBets(name);

        assertThrows(IllegalArgumentException.class, () -> {
            betService.placeBet(creator, bet.getId(), amount, outcome);
        }, "IllegalArgumentException should be raised");

        List<PlacedBet> placedBets = placedBetRepository.getAllForBet(bet);
        assertTrue(placedBets.isEmpty(), "The wager should be not be created");
    }


    /**
     * Setting the ID of the message representing the bet in Discord
     */
    @Test
    @Transactional
    public void testSetMessageId() {
        String name = "testSetMsg";
        double oddsFor = 2.45, oddsAgainst = 3.2;
        Instant closing = Instant.now().plus(1L, ChronoUnit.HOURS);
        User creator = userRepository.findOrCreateById(1L, "creator");
        betService.addBet(name, oddsFor, oddsAgainst, closing, creator);
        Bet bet = betRepository.findByName(name);

        Assertions.assertNull(bet.getMessageId());

        // Set message ID
        betService.setMessageId(bet, "test-message-id");

        // Update bet
        Bet updatedBet = betRepository.findByName(name);
        Assertions.assertEquals("test-message-id", updatedBet.getMessageId());
    }

}
