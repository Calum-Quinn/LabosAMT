package ch.heigvd.amt.entity;

import ch.heigvd.amt.beans.BetOutcome;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Database entity representing a Bet
 */
@Entity(name = "bet")
public class Bet {
    @Id
    @Column(name = "bet_id", updatable = false, nullable = false)
    private UUID id;

    // Reference the user that created the bet so we know who can close it
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "name", length = 256, unique = true, nullable = false)
    private String name;

    @Column(name = "odds_for")
    private Double oddsFor;

    @Column(name = "odds_against")
    private Double oddsAgainst;

    @Column(name = "closing")
    private Instant closing;

    @Column(name = "outcome")
    private Boolean outcome;

    @Column(name = "message_id")
    private String messageId;

    @OneToMany(mappedBy = "bet", cascade = CascadeType.ALL)
    private List<PlacedBet> placedBets;

    public Bet() {
        this.id = UUID.randomUUID();
        this.placedBets = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public BetOutcome getOutcome() {
        if (outcome == null) {
            return null;
        }
        return outcome ? BetOutcome.FOR : BetOutcome.AGAINST;
    }

    public void setOutcome(BetOutcome outcome) {
        this.outcome = outcome.equals(BetOutcome.FOR);
    }

    public Double getOddsFor() {
        return oddsFor;
    }

    public void setOddsFor(Double oddsFor) {
        this.oddsFor = oddsFor;
    }

    public Double getOddsAgainst() {
        return oddsAgainst;
    }

    public void setOddsAgainst(Double oddsAgainst) {
        this.oddsAgainst = oddsAgainst;
    }

    public Instant getClosing() {
        return closing;
    }

    public void setClosing(Instant closing) {
        this.closing = closing;
    }

    public List<PlacedBet> getPlacedBets() {
        return placedBets;
    }

    public void setPlacedBets(List<PlacedBet> placedBets) {
        this.placedBets = placedBets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Check for reference equality

        // Check if the object is an instance of Bet
        if (!(o instanceof Bet other)) {
            return false;
        }

        // Compare all relevant fields for equality
        return id != null && id.equals(other.id) &&
                (Objects.equals(creator, other.creator)) &&
                (Objects.equals(name, other.name)) &&
                (Objects.equals(oddsFor, other.oddsFor)) &&
                (Objects.equals(oddsAgainst, other.oddsAgainst)) &&
                (Math.abs(closing.toEpochMilli() - other.closing.toEpochMilli()) <= 1) &&
                (Objects.equals(outcome, other.outcome)) &&
                (Objects.equals(messageId, other.messageId));
    }
}
