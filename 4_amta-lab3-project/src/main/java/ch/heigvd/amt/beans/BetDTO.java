package ch.heigvd.amt.beans;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents the necessary information about a bet, mostly for the web interface
 */
public class BetDTO {
    private static final DateTimeFormatter CLOSING_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    private UUID id;

    private String name;

    private long creatorId;

    private Instant closingTime;

    private Double oddsFor;
    private Double oddsAgainst;

    private Long forAmount;
    private Long againstAmount;

    private boolean closed;
    private Boolean outcome;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public Instant getClosingTime() {
        return closingTime;
    }

    public String formattedClosingTime() {
        return CLOSING_FORMATTER.format(LocalDateTime.ofInstant(closingTime, ZoneOffset.systemDefault()));
    }

    public void setClosingTime(Instant closingTime) {
        this.closingTime = closingTime;
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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Boolean getOutcome() {
        return outcome;
    }

    public void setOutcome(Boolean outcome) {
        this.outcome = outcome;
    }

    public Long getForAmount() {
        return forAmount;
    }

    public void setForAmount(Long forAmount) {
        this.forAmount = forAmount;
    }

    public Long getAgainstAmount() {
        return againstAmount;
    }

    public void setAgainstAmount(Long againstAmount) {
        this.againstAmount = againstAmount;
    }

    public Double forPercentage() {
        return (double) forAmount / ((double) forAmount + againstAmount);
    }
}
