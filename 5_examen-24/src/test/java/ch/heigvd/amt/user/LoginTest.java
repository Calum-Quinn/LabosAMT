package ch.heigvd.amt.user;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class LoginTest {

    @Test
    void getLoginForm() {
        given()
                .when().get("/user/login")
                .then()
                .statusCode(200)
                .body(containsString("Login"));
    }


    @Test
    void testLogin() {
        given()
                .auth().form("bob", "test123")
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(containsString("Username: bob"));
    }

    @Test
    void testLoginError() {
        given()
                .redirects().follow(false)
                .auth().form("bob", "none")
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
                .header("Location", Matchers.endsWith("login"));
    }

    @Test
    void testLoginErrorRedirects() {
        given()
                .when()
                .formParam("j_username", "bob")
                .formParam("j_password", "none")
                .post("/j_security_check")
                .then()
                .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
                .header("Location", Matchers.endsWith("error"));
    }

    @Test
    void testLoginErrorContent() {
        given()
                .when()
                .get("/user/error")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(containsString("Authentication error"));
    }
}
