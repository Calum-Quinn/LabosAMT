package ch.heigvd.amt.wiki;

import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;

@QuarkusTest
@ConnectWireMock
public class WikiProcessorTest implements QuarkusTestProfile {
    @Inject
    WikiService wikiService;

    @InjectSpy
    TestProcessor processor;

    @Test
    void testDetectionOfProcessor() throws InterruptedException {
        wikiService.start();
        waitAtLeastUntil(Duration.ofSeconds(20), 5L);
        Mockito.verify(processor, Mockito.atLeastOnce()).process(Mockito.any());
    }

    void waitAtLeastUntil(Duration duration, Long nb) throws InterruptedException  {
        Instant start = Instant.now();
        while(wikiService.getNbProcessed() < nb) {
            if (Instant.now().isBefore(start.plus(duration))) {
                Thread.sleep(500);
            }
            else {
                throw new AssertionError("Service did not finish after " + duration);
            }
        }
    }
}
