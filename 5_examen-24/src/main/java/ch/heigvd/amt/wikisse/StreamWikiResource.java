package ch.heigvd.amt.wikisse;

import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestStreamElementType;

@Path("/wiki/sse")
public class StreamWikiResource {

    @Channel("wiki-events")
    Multi<WikiSse> events;

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Authenticated
    public Multi<WikiSse> sse() {
        return events;
    }

}
