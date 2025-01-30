package ch.heigvd.amt.service;

import ch.heigvd.amt.beans.BetDTO;
import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.BetRepository;
import ch.heigvd.amt.repository.UserRepository;
import discord4j.rest.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@QuarkusTest
public class BetMapperTest {
    @Inject
    BetService betService;

    @Inject
    UserRepository userRepository;

    @Inject
    BetRepository betRepository;

    @Inject
    BetMapper betMapper;

    @Test
    public void testMap() {
        String name = "test";
        double oddsFor = 2.45, oddsAgainst = 3.2;
        Instant closing = Instant.now().plus(1L, ChronoUnit.HOURS);
        User creator = userRepository.findOrCreateById(1L, "creator");
        betService.addBet(name, oddsFor, oddsAgainst, closing, creator);
        Bet bet = betRepository.findByName(name);
        bet.setOutcome(BetOutcome.FOR);

        BetDTO betDTO = betMapper.map(bet);

        Assertions.assertNotNull(betDTO);
        Assertions.assertEquals(bet.getId(), betDTO.getId(), "Original bet id and DTO id should be the same");
        Assertions.assertEquals(bet.getName(), betDTO.getName(), "Original bet name and DTO name should be the same");
        Assertions.assertEquals(bet.getCreator().getId(), betDTO.getCreatorId(), "Original bet creator id and DTO creator id should be the same");
        Assertions.assertEquals(bet.getOddsFor(), betDTO.getOddsFor(), "Original bet odds for and DTO odds for should be the same");
        Assertions.assertEquals(bet.getOddsAgainst(), betDTO.getOddsAgainst(), "Original bet odds against and DTO odds against should be the same");
        Assertions.assertEquals(bet.getClosing(), betDTO.getClosingTime(), "Original bet closing time and DTO time should be the same");
        Assertions.assertTrue(betDTO.getOutcome(), "DTO outcome should be true");
        Assertions.assertTrue(betDTO.getForAmount() == 0, "BetDTO amount for should be zero");
        Assertions.assertTrue(betDTO.getAgainstAmount() == 0, "BetDTO amount against should be zero");
    }
}
