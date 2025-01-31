package ch.heigvd.amt.wiki;

import ch.heigvd.amt.wiki.dtos.MediaWikiRecentChange;

public interface Processor {
    void process(MediaWikiRecentChange event);
}
