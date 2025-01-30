package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Film;
import ch.heigvd.amt.jpa.entity.Language;
import ch.heigvd.amt.jpa.entity.Rating;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FilmRepository {
    @Inject
    private EntityManager em;

    private FilmDTO fromEntityToDTO(Film film) {
        if (film == null) {
            return null;
        }

        return new FilmDTO(
                film.getId(),
                film.getTitle(),
                film.getLanguage().getName(),
                film.getRating().toString()
                );
    }

    public record FilmDTO (Integer id, String title, String language, String rating) {
    }

    public FilmDTO read(Integer id) {
        // TODO: implement solution
        Film film = em.find(Film.class, id);
        return this.fromEntityToDTO(film);
    }

    @Transactional
    public Integer create(String title, String language, String rating) {
        // TODO: implement solution
        Film film = new Film();
        film.setTitle(title);
        film.setLanguage(getLanguage(language));
        film.setRating(Rating.fromString(rating));

        em.persist(film);
        return film.getId();
    }

    private Language getLanguage(String language) {
        TypedQuery<Language> query = em.createQuery("SELECT l FROM language l WHERE LOWER(l.name) LIKE CONCAT('%', LOWER(?1), '%')", Language.class);
        return query.setParameter(1, language).getSingleResult();
    }

    @Transactional
    public void update(Integer id, String title, String language, String rating) {
        // TODO: implement solution
        Film film = em.find(Film.class, id);

        if (film == null) {
            throw new IllegalArgumentException("Film with id " + id + " does not exist");
        }

        film.setTitle(title);
        film.setLanguage(getLanguage(language));
        film.setRating(Rating.fromString(rating));

        em.merge(film);
    }

    @Transactional
    public void delete(Integer id) {
        // TODO: implement solution
        Film film = em.find(Film.class, id);

        if (film == null) {
            throw new IllegalArgumentException("Film with id " + id + " does not exist");
        }
        em.remove(film);
    }
}
