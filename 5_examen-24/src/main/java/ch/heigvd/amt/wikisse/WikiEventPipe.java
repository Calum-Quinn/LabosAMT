package ch.heigvd.amt.wikisse;

import ch.heigvd.amt.wiki.WikiEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class WikiEventPipe {

    @Channel("wiki-events")
    Emitter<WikiSse> emitter;

    @Inject
    Logger log;

    // TODO: partial implementation to complete
    public void onWikiEvent(WikiEvent event) {
        // TODO add mising fields
        try {
            if (emitter.hasRequests()) {
                emitter.send(new WikiSse(event.id(), event.title(), event.type(), event.url()));
            }
        } catch (Exception e) {
            log.warn("Error while emitting to wiki-events channel");
        }
    }

}
