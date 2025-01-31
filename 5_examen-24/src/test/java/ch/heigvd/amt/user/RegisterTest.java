package ch.heigvd.amt.user;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class RegisterTest {

    @Test
    void registerUser() {
        given()
                .when()
                .formParam("username", "mark")
                .formParam("password", "test123")
                .post("/user/register")
                .then()
                .statusCode(200)
                .body(containsString("Your account has been created successfully."));
    }
}
