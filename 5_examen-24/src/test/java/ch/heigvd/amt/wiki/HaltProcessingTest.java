package ch.heigvd.amt.wiki;

import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@ConnectWireMock
@TestProfile(HaltProcessingTest.class)
public class HaltProcessingTest implements QuarkusTestProfile {
    @Inject
    WikiService wikiService;

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.rest-client.wiki.url", "http://localhost:${quarkus.wiremock.devservices.port}/mock-me-football"
        );
    }

    @Test
    void testHaltBehavior() throws InterruptedException {
        wikiService.start();
        waitCompletionUntil(Duration.ofSeconds(20));
        assertEquals(1024, wikiService.getLastNbProcessed());
    }

    void waitCompletionUntil(Duration duration) throws InterruptedException  {
        Instant start = Instant.now();
        while(wikiService.isRunning()) {
            if (Instant.now().isBefore(start.plus(duration))) {
                Thread.sleep(500);
            }
            else {
                throw new AssertionError("Service did not finish after " + duration);
            }
        }
    }
}
