package ch.heigvd.amt.repository;

import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class UserRepositoryTest {

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

    @Test
    public void testFindOrCreateById() {
        String name = "userTest";
        long id = 1;

        // Make sure there are no users before creation
        Assertions.assertEquals(0, userRepository.findAll().count());

        User user1 = userRepository.findOrCreateById(id, name);

        // Make sure the user has been created
        Assertions.assertEquals(1, userRepository.findAll().count());

        User user2 = userRepository.findOrCreateById(id, name);

        // Make sure the user count doesn't go up with an existing user
        Assertions.assertEquals(1, userRepository.findAll().count());
    }

    @Test
    @Transactional
    public void testListLeaderboard() {
        String name1 = "user1", name2 = "user2", name3 = "user3";
        long id1 = 1, id2 = 2, id3 = 3;
        long balance1 = 1000, balance2 = 4000, balance3 = 3000;

        User user1 = userRepository.findOrCreateById(id1, name1);
        User user2 = userRepository.findOrCreateById(id2, name2);
        User user3 = userRepository.findOrCreateById(id3, name3);

        user1.setBalance(balance1);
        user2.setBalance(balance2);
        user3.setBalance(balance3);

        userRepository.persist(user1);
        userRepository.persist(user2);
        userRepository.persist(user3);

        List<User> leaderboard = userRepository.listLeaderboard();

        Assertions.assertEquals(3, leaderboard.size());
        Assertions.assertEquals(user2, leaderboard.get(0));
        Assertions.assertEquals(user3, leaderboard.get(1));
        Assertions.assertEquals(user1, leaderboard.get(2));
    }

    @Test
    @Transactional
    public void testListLeaderboardWithBetCounts() {
        String name1 = "user1", name2 = "user2", name3 = "user3";
        long id1 = 1, id2 = 2, id3 = 3;
        long balance1 = 1000, balance2 = 4000, balance3 = 3000;

        User user1 = userRepository.findOrCreateById(id1, name1);
        User user2 = userRepository.findOrCreateById(id2, name2);
        User user3 = userRepository.findOrCreateById(id3, name3);

        user1.setBalance(balance1);
        user2.setBalance(balance2);
        user3.setBalance(balance3);

        userRepository.persist(user1);
        userRepository.persist(user2);
        userRepository.persist(user3);

        Bet bet1 = new Bet();
        bet1.setCreator(user2);
        bet1.setName("Bet1");
        betRepository.persist(bet1);

        Bet bet2 = new Bet();
        bet2.setCreator(user2);
        bet2.setName("Bet2");
        betRepository.persist(bet2);

        Bet bet3 = new Bet();
        bet3.setCreator(user2);
        bet3.setName("Bet3");
        betRepository.persist(bet3);

        Bet bet4 = new Bet();
        bet4.setCreator(user1);
        bet4.setName("Bet4");
        betRepository.persist(bet4);

        Bet bet5 = new Bet();
        bet5.setCreator(user2);
        bet5.setName("Bet5");
        betRepository.persist(bet5);

        Bet bet6 = new Bet();
        bet6.setCreator(user2);
        bet6.setName("Bet6");
        betRepository.persist(bet6);

        List<Object[]> leaderboard = userRepository.listLeaderboardWithBetCounts();

        Assertions.assertEquals(3, leaderboard.size());

        Assertions.assertEquals(user2, leaderboard.get(0)[0]);
        Assertions.assertEquals(5L, ((Integer) leaderboard.get(0)[1]).longValue());

        Assertions.assertEquals(user3, leaderboard.get(1)[0]);
        Assertions.assertEquals(0L, ((Integer) leaderboard.get(1)[1]).longValue());

        Assertions.assertEquals(user1, leaderboard.get(2)[0]);
        Assertions.assertEquals(1L, ((Integer) leaderboard.get(2)[1]).longValue());
    }

    @Test
    public void testRemoveAll() {
        String name1 = "userTest1", name2 = "userTest2", name3 = "userTest3";
        long id1 = 1, id2 = 2, id3 = 3;

        // Make sure there are no users before creation
        Assertions.assertEquals(0, userRepository.findAll().count(), "There should be no users before they are created");

        User user1 = userRepository.findOrCreateById(id1, name1);
        User user2 = userRepository.findOrCreateById(id2, name2);
        User user3 = userRepository.findOrCreateById(id3, name3);

        // Make sure the user has been created
        Assertions.assertEquals(3, userRepository.findAll().count(), "There should be 3 users");

        userRepository.removeAll();

        // Make sure the user count doesn't go up with an existing user
        Assertions.assertEquals(0, userRepository.findAll().count(), "There should be no users after they are removed");
    }
}
