package ch.heigvd.amt;

import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.Dependent;

import java.net.URL;

@ConfigMapping(prefix = "app")
@Dependent
public interface Configuration {
    Long guildId();
    Long bettingBoardChannelId();
    String botToken();
    URL externalUrl();
}
