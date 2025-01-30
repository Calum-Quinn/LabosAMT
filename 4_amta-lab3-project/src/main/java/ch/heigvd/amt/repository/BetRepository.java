package ch.heigvd.amt.repository;

import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Used for interactions with a bet
 */
@ApplicationScoped
public class BetRepository implements PanacheRepositoryBase<Bet, UUID> {

    // List all bets
    @Transactional
    public List<Bet> listAll() {
        return getEntityManager()
                .createQuery("SELECT b FROM bet b", Bet.class)
                .getResultList();
    }

    // List all bets that can still be bet on
    @Transactional
    public List<Bet> listOpenBets() {
        return getEntityManager()
                .createQuery("SELECT b FROM bet b WHERE b.closing > CURRENT_TIMESTAMP ORDER BY closing", Bet.class)
                .getResultList();
    }

    // List all previous bets which can no longer be bet on
    @Transactional
    public List<Bet> listClosedBets() {
        return getEntityManager()
                .createQuery("SELECT b FROM bet b WHERE b.closing <= CURRENT_TIMESTAMP ORDER BY closing DESC", Bet.class)
                .getResultList();
    }

    // List all the bets the given user can close at the moment
        // They have to be the creator
        // The bet has to have ended
    @Transactional
    public List<Bet> listClosableBetsForCreator(User creator) {
        try {
            TypedQuery<Bet> query = getEntityManager().createQuery(
                    "SELECT b FROM bet b WHERE b.creator = :creator AND b.closing <= CURRENT_TIMESTAMP AND b.outcome IS NULL",
                    Bet.class
            );
            query.setParameter("creator", creator);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of(); // Return an empty list if no results are found
        }
    }

    // Retrieve the bet represented by the given message
    @Transactional
    public Bet findByMessageId(String messageId) {
        // Query the database to find the Bet with the given messageId
        return getEntityManager().createQuery("SELECT b FROM bet b WHERE b.messageId = :messageId", Bet.class)
                .setParameter("messageId", messageId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public Bet findByName(String name) {
        return getEntityManager().createQuery("SELECT b FROM bet b WHERE b.name = :name", Bet.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public Bet findByNameWithPlacedBets(String name) {
        try {
            Bet bet = getEntityManager().createQuery("SELECT b FROM bet b LEFT JOIN fetch b.placedBets WHERE b.name = :name", Bet.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return bet;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public void removeAll() {
        getEntityManager().createQuery("DELETE FROM bet").executeUpdate();
    }
}
