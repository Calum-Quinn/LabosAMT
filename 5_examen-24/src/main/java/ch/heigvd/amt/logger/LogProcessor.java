package ch.heigvd.amt.logger;

import ch.heigvd.amt.wiki.Processor;
import ch.heigvd.amt.wiki.dtos.MediaWikiRecentChange;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LogProcessor implements Processor {

    @Inject
    Logger log;

    @Override
    public void process(MediaWikiRecentChange event) {
        log.debug("Processor logging: " + event);
    }
}
