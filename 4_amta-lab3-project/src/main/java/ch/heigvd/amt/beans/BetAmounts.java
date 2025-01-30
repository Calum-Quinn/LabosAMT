package ch.heigvd.amt.beans;

/**
 * Used to store the amounts having been bet
 * @param forAmounts amount bet for the positive outcome
 * @param againstAmounts amount bet for the negative outcome
 */
public record BetAmounts(
        Long forAmounts,
        Long againstAmounts
) {
}