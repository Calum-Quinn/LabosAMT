package ch.heigvd.amt.entity;

import ch.heigvd.amt.beans.BetOutcome;
import jakarta.persistence.*;

import java.util.Objects;

/**
 * Database entity representing a bet placed by a User on a Bet
 */
@Entity(name = "placed_bet")
public class PlacedBet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bet_placed_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bet_id", nullable = false)
    private Bet bet;

    @Column(name = "amount", nullable = false)
    private Long amount;

    // Whether the user bet for or against the bet
    @Column(name = "decision")
    private BetOutcome decision;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public BetOutcome getDecision() {
        return decision;
    }

    public void setDecision(BetOutcome decision) {
        this.decision = decision;
    }

    @Override
    public boolean equals(Object o) {
        // Check if the object is the same instance
        if (this == o) return true;

        // Check if the object is an instance of PlacedBet
        if (!(o instanceof PlacedBet other)) return false;

        // Compare all relevant fields for equality
        return id != null && id.equals(other.id) &&
                    (Objects.equals(user, other.user)) &&
                    (Objects.equals(bet, other.bet)) &&
                    (Objects.equals(amount, other.amount)) &&
                    (Objects.equals(decision, other.decision));
    }
}
