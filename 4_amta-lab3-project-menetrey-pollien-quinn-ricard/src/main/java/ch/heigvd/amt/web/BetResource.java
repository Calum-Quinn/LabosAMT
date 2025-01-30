package ch.heigvd.amt.web;

import ch.heigvd.amt.repository.BetRepository;
import ch.heigvd.amt.beans.BetDTO;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.service.BetMapper;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * List of all open bets
 */
@Path("/bets")
public class BetResource {

    @Inject
    BetRepository repository;
    @Inject
    BetMapper betMapper;

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
    @Inject
    BetRepository betRepository;

    @CheckedTemplate(requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance bets(List<BetDTO> bets);
        public static native TemplateInstance archive(List<BetDTO> bets);
        public static native TemplateInstance betDetails(BetDTO bet);
        public static native TemplateInstance error(String message);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getBets() {
        List<BetDTO> betDTOS = repository.listOpenBets().stream()
                .map(betMapper::map)
                .toList();

        return Templates.bets(betDTOS);
    }

    /**
     * Presentation of an individual bet
     * @param id of the bet in question
     * @return the page
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getBetDetails(@PathParam("id") String id) {
        if (!UUID_PATTERN.matcher(id).matches()) {
            return Templates.error("Invalid UUID format");
        }

        Bet bet = betRepository.findById(UUID.fromString(id));
        if (bet == null) {
            return Templates.error("Bet not found");
        }

        BetDTO betDTO = betMapper.map(bet);

        return Templates.betDetails(betDTO);
    }

    @GET
    @Path("/archive")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getOldBets() {
        List<BetDTO> betDTOS = repository.listClosedBets().stream()
                .map(betMapper::map)
                .toList();

        return Templates.archive(betDTOS);
    }
}
