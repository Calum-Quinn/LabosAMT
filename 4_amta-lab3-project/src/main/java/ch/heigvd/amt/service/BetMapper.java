package ch.heigvd.amt.service;

import ch.heigvd.amt.beans.BetAmounts;
import ch.heigvd.amt.beans.BetDTO;
import ch.heigvd.amt.beans.BetOutcome;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.repository.PlacedBetRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Used to represent the corresponding bet in different situations
 */
@ApplicationScoped
public class BetMapper {

    private final PlacedBetRepository placedBetRepository;

    @Inject
    public BetMapper(PlacedBetRepository placedBetRepository) {
        this.placedBetRepository = placedBetRepository;
    }

    public BetDTO map(Bet source) {
        BetDTO betDTO = new BetDTO();
        betDTO.setId(source.getId());
        betDTO.setName(source.getName());
        betDTO.setCreatorId(source.getCreator().getId());

        betDTO.setOddsFor(source.getOddsFor());
        betDTO.setOddsAgainst(source.getOddsAgainst());

        betDTO.setClosingTime(source.getClosing());
        betDTO.setClosed(source.getOutcome() != null);
        betDTO.setOutcome(source.getOutcome() == BetOutcome.FOR);

        // Get all linked bets from the database
        BetAmounts amounts = placedBetRepository.getAmountsForBet(source);

        betDTO.setForAmount(amounts.forAmounts());
        betDTO.setAgainstAmount(amounts.againstAmounts());

        return betDTO;
    }
}
