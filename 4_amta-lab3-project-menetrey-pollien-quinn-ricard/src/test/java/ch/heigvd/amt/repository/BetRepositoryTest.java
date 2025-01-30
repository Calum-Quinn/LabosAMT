package ch.heigvd.amt.repository;

import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.service.BetService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@QuarkusTest
public class BetRepositoryTest {

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
    public void testListAll() {
        String name1 = "test1", name2 = "test2", name3 = "test3";
        double oddsFor1 = 2.45, oddsFor2 = 3.45, oddsFor3 = 5.45;
        double oddsAgainst1 = 3.2, oddsAgainst2 = 5.4, oddsAgainst3 = 6.5;
        Instant     closing1 = Instant.now().plus(1L, ChronoUnit.HOURS),
                    closing2 = Instant.now().plus(2L, ChronoUnit.HOURS),
                    closing3 = Instant.now().plus(3L, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        betService.addBet(name1, oddsFor1, oddsAgainst1, closing1, creator);
        betService.addBet(name2, oddsFor2, oddsAgainst2, closing2, creator);
        betService.addBet(name3, oddsFor3, oddsAgainst3, closing3, creator);

        List<Bet> betList = betRepository.listAll();

        Bet bet1 = betRepository.findByName(name1);
        Bet bet2 = betRepository.findByName(name2);
        Bet bet3 = betRepository.findByName(name3);

        Assertions.assertEquals(3, betList.size(), "Wrong number of bets listed");
        Assertions.assertEquals(betRepository.findByName(name1), betList.get(0), "The first bet should be the same");
        Assertions.assertEquals(betRepository.findByName(name2), betList.get(1), "The second bet should be the same");
        Assertions.assertEquals(betRepository.findByName(name3), betList.get(2), "The third bet should be the same");
    }

    @Test
    public void testListOpenBets() {
        String name1 = "test1", name2 = "test2", name3 = "test3";
        double oddsFor1 = 2.45, oddsFor2 = 3.45, oddsFor3 = 5.45;
        double oddsAgainst1 = 3.2, oddsAgainst2 = 5.4, oddsAgainst3 = 6.5;
        Instant     closing1 = Instant.now().plus(1L, ChronoUnit.HOURS),
                closing2 = Instant.now().minus(2L, ChronoUnit.HOURS), // Already closed
                closing3 = Instant.now().plus(3L, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        betService.addBet(name1, oddsFor1, oddsAgainst1, closing1, creator);
        betService.addBet(name2, oddsFor2, oddsAgainst2, closing2, creator);
        betService.addBet(name3, oddsFor3, oddsAgainst3, closing3, creator);

        List<Bet> betList = betRepository.listOpenBets();

        Assertions.assertEquals(2, betList.size(), "Wrong number of bets listed");
        Assertions.assertEquals(betRepository.findByName(name1), betList.get(0), "The first bet should be the same");
        Assertions.assertEquals(betRepository.findByName(name3), betList.get(1), "The third bet should be the same");
    }

    @Test
    public void testListClosedBets() {
        String name1 = "test1", name2 = "test2", name3 = "test3";
        double oddsFor1 = 2.45, oddsFor2 = 3.45, oddsFor3 = 5.45;
        double oddsAgainst1 = 3.2, oddsAgainst2 = 5.4, oddsAgainst3 = 6.5;
        Instant     closing1 = Instant.now().plus(1L, ChronoUnit.HOURS),
                closing2 = Instant.now().minus(2L, ChronoUnit.HOURS), // Already closed
                closing3 = Instant.now().plus(3L, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        betService.addBet(name1, oddsFor1, oddsAgainst1, closing1, creator);
        betService.addBet(name2, oddsFor2, oddsAgainst2, closing2, creator);
        betService.addBet(name3, oddsFor3, oddsAgainst3, closing3, creator);

        List<Bet> betList = betRepository.listClosedBets();

        Assertions.assertEquals(1, betList.size(), "Wrong number of bets listed");
        Assertions.assertEquals(betRepository.findByName(name2), betList.get(0), "The second bets should be the same");
    }

    @Test
    public void testListClosableBetsForCreator() {
        String name1 = "test1", name2 = "test2", name3 = "test3";
        double oddsFor1 = 2.45, oddsFor2 = 3.45, oddsFor3 = 5.45;
        double oddsAgainst1 = 3.2, oddsAgainst2 = 5.4, oddsAgainst3 = 6.5;
        Instant     closing1 = Instant.now().plus(1L, ChronoUnit.HOURS),
                closing2 = Instant.now().minus(2L, ChronoUnit.HOURS), // Already closed
                closing3 = Instant.now().minus(3L, ChronoUnit.HOURS); // Already closed

        User creator = userRepository.findOrCreateById(1L, "creator");
        User notCreator = userRepository.findOrCreateById(2L, "notCreator");

        betService.addBet(name1, oddsFor1, oddsAgainst1, closing1, creator);
        betService.addBet(name2, oddsFor2, oddsAgainst2, closing2, creator);
        betService.addBet(name3, oddsFor3, oddsAgainst3, closing3, creator);

        List<Bet> creatorBetList = betRepository.listClosableBetsForCreator(creator);
        List<Bet> notCreatorBetList = betRepository.listClosableBetsForCreator(notCreator);

        // List lengths
        Assertions.assertEquals(2, creatorBetList.size(), "Wrong number of bets listed for creator");
        Assertions.assertEquals(0, notCreatorBetList.size(), "Wrong number of bets listed for non-creator");

        Assertions.assertEquals(betRepository.findByName(name2), creatorBetList.get(0), "The second bets should be the same");
        Assertions.assertEquals(betRepository.findByName(name3), creatorBetList.get(1), "The third bets should be the same");
    }

    @Test
    public void testFindByName() {
        String name1 = "test1", name2 = "test2", name3 = "test3";
        double oddsFor1 = 2.45, oddsFor2 = 3.45, oddsFor3 = 5.45;
        double oddsAgainst1 = 3.2, oddsAgainst2 = 5.4, oddsAgainst3 = 6.5;
        Instant     closing1 = Instant.now().plus(1L, ChronoUnit.HOURS),
                closing2 = Instant.now().plus(2L, ChronoUnit.HOURS),
                closing3 = Instant.now().plus(3L, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        // Make sure the bets are not found before their creation
        Assertions.assertNull(betRepository.findByName(name1), "The first bet should not be found");
        Assertions.assertNull(betRepository.findByName(name2), "The second bet should not be found");
        Assertions.assertNull(betRepository.findByName(name3), "The third bet should not be found");

        Bet bet1 = betService.addBet(name1, oddsFor1, oddsAgainst1, closing1, creator);
        Bet bet2 = betService.addBet(name2, oddsFor2, oddsAgainst2, closing2, creator);
        Bet bet3 = betService.addBet(name3, oddsFor3, oddsAgainst3, closing3, creator);

        Bet bet1Found = betRepository.findByName(name1);
        Bet bet2Found = betRepository.findByName(name2);
        Bet bet3Found = betRepository.findByName(name3);

        // Make sure the bets are found after their creation
        Assertions.assertEquals(bet1, bet1Found, "The first bet should be the same");
        Assertions.assertEquals(bet2, bet2Found, "The second bet should be the same");
        Assertions.assertEquals(bet3, bet3Found, "The third bet should not be found");
    }

    @Test
    @Transactional
    public void testFindByNameWithPlacedBets() {
        String name1 = "test1", name2 = "test2", name3 = "test3";
        double oddsFor1 = 2.45, oddsFor2 = 3.45, oddsFor3 = 5.45;
        double oddsAgainst1 = 3.2, oddsAgainst2 = 5.4, oddsAgainst3 = 6.5;
        Instant     closing1 = Instant.now().plus(1L, ChronoUnit.HOURS),
                closing2 = Instant.now().plus(2L, ChronoUnit.HOURS),
                closing3 = Instant.now().plus(3L, ChronoUnit.HOURS);

        User creator1 = userRepository.findOrCreateById(1L, "creator1");
        User creator2 = userRepository.findOrCreateById(2L, "creator2");
        User creator3 = userRepository.findOrCreateById(3L, "creator3");

        // Make sure the bets are not found before their creation
        Assertions.assertNull(betRepository.findByNameWithPlacedBets(name1), "The first bet should not be found");
        Assertions.assertNull(betRepository.findByNameWithPlacedBets(name2), "The second bet should not be found");
        Assertions.assertNull(betRepository.findByNameWithPlacedBets(name3), "The third bet should not be found");

        Bet bet1 = betService.addBet(name1, oddsFor1, oddsAgainst1, closing1, creator3);
        Bet bet2 = betService.addBet(name2, oddsFor2, oddsAgainst2, closing2, creator1);
        Bet bet3 = betService.addBet(name3, oddsFor3, oddsAgainst3, closing3, creator2);

        betService.placeBet(creator1, bet1.getId(), 1000L, BetOutcome.FOR);
        betService.placeBet(creator2, bet2.getId(), 123L, BetOutcome.AGAINST);
        betService.placeBet(creator2, bet1.getId(), 254L, BetOutcome.FOR);

        Bet bet1Found = betRepository.findByNameWithPlacedBets(name1);
        Bet bet2Found = betRepository.findByNameWithPlacedBets(name2);
        Bet bet3Found = betRepository.findByNameWithPlacedBets(name3);

        // Make sure the bets are found after their creation
        Assertions.assertEquals(bet1, bet1Found, "The first bet should be the same");
        Assertions.assertEquals(bet2, bet2Found, "The second bet should be the same");
        Assertions.assertEquals(bet3, bet3Found, "The third bet should not be found");

        // Make sure the placed bets are found
        Assertions.assertEquals(2, bet1Found.getPlacedBets().size(), "There should be two placed bets for the first bet");
        Assertions.assertEquals(1, bet2Found.getPlacedBets().size(), "There should be 1 placed bet for the first bet");
        Assertions.assertEquals(0, bet3Found.getPlacedBets().size(), "There should be no placed bets for the first bet");
    }

    @Test
    public void testRemoveAll() {
        String name1 = "test1", name2 = "test2", name3 = "test3";
        double oddsFor1 = 2.45, oddsFor2 = 3.45, oddsFor3 = 5.45;
        double oddsAgainst1 = 3.2, oddsAgainst2 = 5.4, oddsAgainst3 = 6.5;
        Instant     closing1 = Instant.now().plus(1L, ChronoUnit.HOURS),
                closing2 = Instant.now().plus(2L, ChronoUnit.HOURS),
                closing3 = Instant.now().plus(3L, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        Assertions.assertEquals(0, betRepository.count(), "There should be no bets before they are created");

        betService.addBet(name1, oddsFor1, oddsAgainst1, closing1, creator);
        betService.addBet(name2, oddsFor2, oddsAgainst2, closing2, creator);
        betService.addBet(name3, oddsFor3, oddsAgainst3, closing3, creator);

        Assertions.assertEquals(3, betRepository.count(), "There should be 3 bets after creation");

        betRepository.removeAll();

        Assertions.assertEquals(0, betRepository.count(), "There should be no bets after they are removed");
    }
}
