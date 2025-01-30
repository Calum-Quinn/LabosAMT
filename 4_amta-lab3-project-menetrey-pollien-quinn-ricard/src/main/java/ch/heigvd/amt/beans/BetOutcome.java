package ch.heigvd.amt.beans;

/**
 * Represents the 2 states that can be defined for a bet
 */
public enum BetOutcome {
    FOR,
    AGAINST;

    @Override
    public String toString() {
        return switch (this) {
            case FOR -> "for";
            case AGAINST -> "against";
        };
    }
}
