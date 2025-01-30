package ch.heigvd.amt.repository;

import ch.heigvd.amt.beans.BetAmounts;
import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.PlacedBet;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.service.BetService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@QuarkusTest
public class PlacedBetRepositoryTest {
    @Inject
    UserRepository userRepository;

    @Inject
    BetRepository betRepository;

    @Inject
    PlacedBetRepository placedBetRepository;

    @Inject
    BetService betService;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        placedBetRepository.removeAll();
        betRepository.removeAll();
        userRepository.removeAll();
    }

    @Test
    @Transactional
    public void testGetAllForBet() {
        String name = "testBetPlace";
        double oddsFor = 10, oddsAgainst = 2.45;
        Instant closing = Instant.now().plus(1L, ChronoUnit.HOURS);
        Long amount = 1000L, amount2 = 200L;
        BetOutcome outcome = BetOutcome.FOR, outcome2 = BetOutcome.AGAINST;

        User creator = userRepository.findOrCreateById(1L, "creator");
        User creator2 = userRepository.findOrCreateById(2L, "creator2");

        Bet bet = betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        List<PlacedBet> placed = placedBetRepository.getAllForBet(bet);

        Assertions.assertEquals(0, placed.size(), "There should be no placed bets yet");

        PlacedBet placedBet1 = betService.placeBet(creator, bet.getId(), amount, outcome);
        PlacedBet placedBet2 = betService.placeBet(creator2, bet.getId(), amount2, outcome2);

        placed = placedBetRepository.getAllForBet(bet);

        Assertions.assertEquals(2, placed.size(), "There should be 2 placed bets now");
        Assertions.assertEquals(placedBet1, placed.get(0), "The first placed bet should be the same");
        Assertions.assertEquals(placedBet2, placed.get(1), "The second placed bet should be the same");
    }

    @Test
    @Transactional
    public void testGetAmountsForBet() {
        String name = "testBetPlace";
        double oddsFor = 10, oddsAgainst = 2.45;
        Instant closing = Instant.now().plus(1L, ChronoUnit.HOURS);
        Long amount = 1000L, amount2 = 200L;
        BetOutcome outcome = BetOutcome.FOR, outcome2 = BetOutcome.AGAINST;

        User creator = userRepository.findOrCreateById(1L, "creator");
        User creator2 = userRepository.findOrCreateById(2L, "creator2");

        Bet bet = betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        betService.placeBet(creator, bet.getId(), amount, outcome);
        betService.placeBet(creator2, bet.getId(), amount2, outcome2);

        BetAmounts betAmounts = placedBetRepository.getAmountsForBet(bet);

        Assertions.assertEquals(amount, betAmounts.forAmounts(), "The amount bet for should be the same");
        Assertions.assertEquals(amount2, betAmounts.againstAmounts(), "The amount bet against should be the same");
    }

    @Test
    @Transactional
    public void testHasMadeABet() {
        String name = "testBetPlace";
        double oddsFor = 10, oddsAgainst = 2.45;
        Instant closing = Instant.now().plus(1L, ChronoUnit.HOURS);
        Long amount = 1000L, amount2 = 200L;
        BetOutcome outcome = BetOutcome.FOR, outcome2 = BetOutcome.AGAINST;

        User creator = userRepository.findOrCreateById(1L, "creator");
        User creator2 = userRepository.findOrCreateById(2L, "creator2");

        Bet bet = betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        Assertions.assertFalse(placedBetRepository.hasMadeABet(creator, bet), "The user should not have made a bet");
        Assertions.assertFalse(placedBetRepository.hasMadeABet(creator2, bet), "The user should not have made a bet");

        betService.placeBet(creator, bet.getId(), amount, outcome);

        Assertions.assertTrue(placedBetRepository.hasMadeABet(creator, bet), "The user should have made a bet");
        Assertions.assertFalse(placedBetRepository.hasMadeABet(creator2, bet), "The user should not have made a bet");
    }

    @Test
    @Transactional
    public void testRemoveAll() {
        String name = "testBetPlace";
        double oddsFor = 10, oddsAgainst = 2.45;
        Instant closing = Instant.now().plus(1L, ChronoUnit.HOURS);
        Long amount = 1000L, amount2 = 200L;
        BetOutcome outcome = BetOutcome.FOR, outcome2 = BetOutcome.AGAINST;

        User creator = userRepository.findOrCreateById(1L, "creator");
        User creator2 = userRepository.findOrCreateById(2L, "creator2");

        Bet bet = betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        Assertions.assertEquals(0, placedBetRepository.findAll().count(), "There should be no placed bets yet");

        betService.placeBet(creator, bet.getId(), amount, outcome);
        betService.placeBet(creator2, bet.getId(), amount2, outcome2);

        Assertions.assertEquals(2, placedBetRepository.findAll().count(), "There should be 2 placed bets now");

        placedBetRepository.removeAll();

        Assertions.assertEquals(0, placedBetRepository.findAll().count(), "There should be no bets again");
    }
}
