package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.util.Collections;
import java.util.List;

/**
 * Exercise Actors with films of PG rating.
 * Signature of methods (actorInPGRatings_*) must not be changed.
 */
@ApplicationScoped
public class ActorsInPGService {

    @Inject
    EntityManager em;

    public record ActorInPGRating(String firstName, String lastName, Long nbFilms) {
    }

    public List<ActorInPGRating> actorInPGRatings_NativeSQL() {
        //noinspection unchecked
        return em.createNativeQuery("""
                SELECT actor.first_name as firstName, actor.last_name as lastName, COUNT(distinct f.film_id) as nbFilms
                FROM actor
                LEFT JOIN film_actor fa ON actor.actor_id = fa.actor_id
                LEFT JOIN public.film f on fa.film_id = f.film_id
                WHERE f.rating = 'PG'
                GROUP BY actor.first_name, actor.last_name, actor.actor_id
                ORDER BY nbFilms DESC, first_name, last_name, actor.actor_id
                """, ActorInPGRating.class).getResultList();
    }

    public List<ActorInPGRating> actorInPGRatings_JPQL() {
        return em.createQuery("""
                        select a.firstName, a.lastName, count(f.id) as nbFilms
                        from actor a
                        left join a.films f
                        where f.rating = 'PG'
                        group by a.firstName, a.lastName, a.id
                        order by nbFilms desc, a.firstName, a.lastName, a.id
                        """, ActorInPGRating.class)
                .getResultList();
    }

    public List<ActorInPGRating> actorInPGRatings_CriteriaString() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ActorInPGRating> q = cb.createQuery(ActorInPGRating.class);

        Root<Actor> actor = q.from(Actor.class);
        Join<Actor, Film> films = actor.join("films");

        q.multiselect(
                actor.get("firstName"),
                actor.get("lastName"),
                cb.count(films.get("id"))
        );

        q.where(cb.equal(films.get("rating"), cb.literal(Rating.PG)));

        q.groupBy(actor.get("id"), actor.get("firstName"), actor.get("lastName"));
        q.orderBy(
                cb.desc(cb.count(films.get("id"))),
                cb.asc(actor.get("firstName")),
                cb.asc(actor.get("lastName")),
                cb.asc(actor.get("id"))
        );

        return em.createQuery(q).getResultList();

    }

    public List<ActorInPGRating> actorInPGRatings_CriteriaMetaModel() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ActorInPGRating> q = cb.createQuery(ActorInPGRating.class);

        Root<Actor> actor = q.from(Actor.class);
        ListJoin<Actor, Film> films = actor.join(Actor_.films);

        Expression<Long> nbFilms = cb.count(films.get(Film_.id));
        q.multiselect(
                actor.get(Actor_.firstName),
                actor.get(Actor_.lastName),
                nbFilms
        );

        q.where(cb.equal(films.get(Film_.rating), cb.literal(Rating.PG)));

        q.groupBy(actor.get(Actor_.id), actor.get(Actor_.firstName), actor.get(Actor_.lastName));
        q.orderBy(
                cb.desc(nbFilms),
                cb.asc(actor.get(Actor_.firstName)),
                cb.asc(actor.get(Actor_.lastName)),
                cb.asc(actor.get(Actor_.id))
        );

        return em.createQuery(q).getResultList();
    }
}
