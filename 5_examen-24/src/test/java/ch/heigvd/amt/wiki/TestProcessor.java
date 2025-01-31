package ch.heigvd.amt.wiki;

import ch.heigvd.amt.wiki.dtos.MediaWikiRecentChange;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestProcessor implements Processor {

    @Override
    public void process(MediaWikiRecentChange event) {
        // do nothing
    }
}
