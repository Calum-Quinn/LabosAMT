package ch.heigvd.amt.web;

import ch.heigvd.amt.entity.User;
import ch.heigvd.amt.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class LeaderboardResourceTest {

    @Inject
    UserRepository userRepository;

    /**
     * Empty leaderboard page
     */
    @Test
    void testEmptyLeaderboardEndpoint() {
        given()
                .when().get("/leaderboard")
                .then()
                .statusCode(200)
                .body(containsString("<h2>Leaderboard</h2>"))
                .body(containsString("No users were found."));
    }

    /**
     * Non-empty leaderboard page
     */
    @Test
    void testLeaderboardEndpoint() {
        String username = "test-user";
        User testUser = userRepository.findOrCreateById(4L, username);

        given()
                .when().get("/leaderboard")
                .then()
                .statusCode(200)
                .body(containsString("<h2>Leaderboard</h2>"))
                .body(containsString("<strong>" + testUser.getUsername() + "</strong>"));
    }
}
