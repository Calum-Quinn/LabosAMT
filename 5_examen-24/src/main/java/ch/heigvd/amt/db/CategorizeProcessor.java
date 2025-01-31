package ch.heigvd.amt.db;

import ch.heigvd.amt.wiki.Processor;
import ch.heigvd.amt.wiki.dtos.MediaWikiRecentChange;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class CategorizeProcessor implements Processor {

    @Inject
    EntityManager em;

    @Transactional
    @Override
    public void process(MediaWikiRecentChange event) {
        boolean eventIsValid = event.id() != null && event.type() != null;
        assert event.type() != null;
        boolean eventIsCategorize = event.type().matches("categorize");
        if (eventIsValid && eventIsCategorize)  {
            var find = em.find(WikiCategorize.class, event.id());
            var entity = Optional.ofNullable(find).orElse(new WikiCategorize());
            entity.setId(event.id());
            entity.setUser(event.user());
            entity.setTitle(event.title());
            entity.setBot(event.bot());
            entity.setNotify_url(event.notify_url());
            em.persist(entity);
        }
    }
}
