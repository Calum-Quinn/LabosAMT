package ch.heigvd.amt.service;

import ch.heigvd.amt.Configuration;
import ch.heigvd.amt.discord.GatewayProvider;
import ch.heigvd.amt.embeds.Embeds;
import ch.heigvd.amt.entity.Bet;
import ch.heigvd.amt.render.HtmlRenderer;
import ch.heigvd.amt.repository.BetRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateFields;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

/**
 * Maintains the bet image up to date
 */
@ApplicationScoped
public class BettingBoardService {

    @Inject
    GatewayProvider gatewayProvider;

    @Inject
    BetService service;

    @Inject
    BetRepository repository;

    @Inject
    BetMapper mapper;

    @Inject
    Configuration configuration;

    @Inject
    HtmlRenderer renderer;

    @Incoming("board-update-consumer")
    @Blocking
    @Transactional
    public void updateBettingBoard(String betId) {
        Bet bet = repository.findById(UUID.fromString(betId), LockModeType.PESSIMISTIC_READ);

        // Generate the embed
        InputStream embed = renderer.renderEmbed(Embeds.bet(mapper.map(bet)).render());

        Optional<Message> message = Optional.ofNullable(bet.getMessageId())
                .map(e -> {
                    // Get the message if it exists
                    return gatewayProvider.getGateway().getMessageById(Snowflake.of(configuration.bettingBoardChannelId()), Snowflake.of(e))
                            .block();
                });

        if (message.isPresent()) {
            // Remove the existing attachment. We add a text, as otherwise the message would be empty,
            // something that discord does not accept.
            //
            // We also cannot do a single update, as the api **adds** new files, instead of replacing them when the name
            // is the same.
            message.get().edit()
                    .withContentOrNull("Updating...")
                    .withAttachmentsOrNull(null)
                    .withFlagsOrNull(null)
                    .block();

            message.get().edit()
                    // Remove the updating message
                    .withContentOrNull(null)
                    .withFiles(MessageCreateFields.File.of("image.png", embed))
                    .withComponents(
                            ActionRow.of(
                                    Button.success("bet-for", "Bet - For"),
                                    Button.danger("bet-against", "Bet - Against"),
                                    Button.link(service.getUrl(bet), ReactionEmoji.unicode("\uD83C\uDF10"))
                            )
                    )
                    .block();
        } else {
            Message result = gatewayProvider.getGateway().getChannelById(Snowflake.of(configuration.bettingBoardChannelId()))
                    .ofType(TextChannel.class).block()
                    .createMessage()
                    .withFiles(MessageCreateFields.File.of("image.png", embed))
                    .withComponents(
                            ActionRow.of(
                                    Button.success("bet-for", "Bet - For"),
                                    Button.danger("bet-against", "Bet - Against"),
                                    Button.link(service.getUrl(bet), ReactionEmoji.unicode("\uD83C\uDF10"))
                            )
                    )
                    .block();

            if (result != null) {
                service.setMessageId(bet, result.getId().asString());
            }
        }
    }
}
