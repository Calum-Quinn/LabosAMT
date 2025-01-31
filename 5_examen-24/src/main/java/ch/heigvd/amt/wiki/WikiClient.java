package ch.heigvd.amt.wiki;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.client.SseEvent;


// https://stream.wikimedia.org/v2/stream/recentchange
@Path("/v2/stream/recentchange")
@RegisterRestClient(configKey = "wiki")
public interface WikiClient {
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    Multi<SseEvent<String>> get();
}
