package ch.heigvd.amt.discord;

import ch.heigvd.amt.Configuration;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Used to easily retrieve the DiscordClient to interact with the Discord API
 */
@Singleton
public class GatewayProvider {

    private final GatewayDiscordClient gateway;

    @Inject
    public GatewayProvider(Configuration configuration) {
        DiscordClient client = DiscordClient.create(configuration.botToken());
        this.gateway = client.login().block();
    }

    public GatewayDiscordClient getGateway() {
        return gateway;
    }
}