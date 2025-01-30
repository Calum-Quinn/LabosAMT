package ch.heigvd.amt.service;

import ch.heigvd.amt.beans.BetDTO;
import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.beans.UserDTO;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.BetRepository;
import ch.heigvd.amt.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@QuarkusTest
public class UserMapperTest {
    @Inject
    BetService betService;

    @Inject
    UserRepository userRepository;

    @Inject
    BetRepository betRepository;

    @Inject
    UserMapper userMapper;

    @Test
    public void testMap() {
        User user = userRepository.findOrCreateById(1L, "test-username");
        int placedBetCount = 5;
        Object[] userObject = new Object[]{user, placedBetCount};

        UserDTO userDTO = userMapper.map(userObject);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getId(), userDTO.getId(), "Original user id and DTO id should be the same");
        Assertions.assertEquals(user.getUsername(), userDTO.getUsername(), "Original username and DTO username should be the same");
        Assertions.assertEquals(user.getBalance(), userDTO.getBalance(), "Original user balance and DTO balance should be the same");
        Assertions.assertEquals(placedBetCount, userDTO.getCreatedBets(), "Original placed bet count and DTO placed bet count should be the same");
    }
}
