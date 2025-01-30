package ch.heigvd.amt.repository;

import ch.heigvd.amt.beans.BetAmounts;
import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.entity.PlacedBet;
import ch.heigvd.amt.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used for interactions with a bet that was placed
 */
@ApplicationScoped
public class PlacedBetRepository implements PanacheRepository<PlacedBet> {
    // Retrieve all the information pertaining to the bet on which this was placed
    @Transactional
    public List<PlacedBet> getAllForBet(Bet bet) {
        return getEntityManager().createQuery("SELECT pb FROM placed_bet pb WHERE pb.bet = :bet", PlacedBet.class)
                .setParameter("bet", bet)
                .getResultList();
    }

    // Retrieve the amounts bet for and against the given bet
    @Transactional
    public BetAmounts getAmountsForBet(Bet bet) {
        Map<BetOutcome, Long> betsAmounts = getEntityManager()
                .createQuery("select pb.decision as decision, sum(pb.amount) as amount from placed_bet pb where pb.bet.id = :betId group by pb.decision", Tuple.class)
                .setParameter("betId", bet.getId())
                .getResultStream()
                .collect(Collectors.toMap(
                        tuple -> ((BetOutcome) tuple.get("decision")),
                        tuple -> ((Number) tuple.get("amount")).longValue()
                ));

        return new BetAmounts(
                betsAmounts.getOrDefault(BetOutcome.FOR, 0L),
                betsAmounts.getOrDefault(BetOutcome.AGAINST, 0L)
        );
    }

    // Returns whether the given user has already bet on the given bet
    @Transactional
    public boolean hasMadeABet(User user, Bet bet) {
        return getEntityManager().createQuery("SELECT count(*) FROM placed_bet pb where pb.bet = :bet and pb.user = :user", Long.class)
                .setParameter("bet", bet)
                .setParameter("user", user)
                .getSingleResult() > 0;
    }

    @Transactional
    public void removeAll() {
        getEntityManager().createQuery("DELETE FROM placed_bet").executeUpdate();
    }
}
