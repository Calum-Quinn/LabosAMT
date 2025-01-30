package ch.heigvd.amt.web;

import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.BetRepository;
import ch.heigvd.amt.repository.PlacedBetRepository;
import ch.heigvd.amt.repository.UserRepository;
import ch.heigvd.amt.service.BetService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class BetResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    BetService betService;

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
     * Empty bets page
     */
    @Test
    void testEmptyBetsEndpoint() {
        // DOES NOT PASS BECAUSE OTHER TESTS ARE LAUNCHED FIRST, THEREFORE IT IS NOT EMPTY
        given()
                .when().get("/bets")
                .then()
                .statusCode(200)
                .body(containsString("<h2>Open Bets</h2>"))
                .body(containsString("There are no open bets at the moment."));
    }

    /**
     * Non-empty bets page
     */
    @Test
    void testBetsEndpoint() {
        String name = "testBets";
        double oddsFor = 2.45, oddsAgainst = 3.2;
        Instant closing = Instant.now().plus(1L, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(1L, "creator");

        betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        given()
                .when().get("/bets")
                .then()
                .statusCode(200)
                .body(containsString("<h2>Open Bets</h2>"))
                .body(containsString("<strong>" + name + "</strong>"));
    }

    /**
     * Details of a bet page
     */
    @Test
    void testBetsIdEndpoint() {
        String name = "testBet";
        double oddsFor = 2.45, oddsAgainst = 3.2;
        Instant closing = Instant.now().plus(1L, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(3L, "creator2");

        betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        given()
                .when().get("/bets/" + betRepository.findByName(name).getId())
                .then()
                .statusCode(200)
                .body(containsString("<h2>" + name + "</h2>"));
    }

    /**
     * Error page
     */
    @Test
    void testErrorPage() {
        given()
                .when().get("/bets/1")
                .then()
                .statusCode(200)
                .body(containsString("<h1>Error</h1>"));
    }

    /**
     * Empty bets archive page
     */
    @Test
    void testEmptyBetsArchiveEndpoint() {
        given()
                .when().get("/bets/archive")
                .then()
                .statusCode(200)
                .body(containsString("<h2>Closed Bets</h2>"))
                .body(containsString("No bets have exceeded their betting limit yet."));
    }

    /**
     * Non-empty bets archive page
     */
    @Test
    void testBetsArchiveEndpoint() {
        String name = "testArchivedBet";
        double oddsFor = 2.45, oddsAgainst = 3.2;
        Instant closing = Instant.now().minus(1L, ChronoUnit.HOURS);

        User creator = userRepository.findOrCreateById(3L, "creator3");

        betService.addBet(name, oddsFor, oddsAgainst, closing, creator);

        given()
                .when().get("/bets/archive")
                .then()
                .statusCode(200)
                .body(containsString("<h2>Closed Bets</h2>"))
                .body(containsString("<strong>" + name + "</strong>"));
    }
}
