package ch.heigvd.amt.db;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.Path;

@Path("/stats")
public class StatsResource {

    @Location("stats/topusers")
    Template topUsers;

    @Path("topusers")
    public TemplateInstance getStats() {
        //TODO
        return topUsers.instance();
    }
}
