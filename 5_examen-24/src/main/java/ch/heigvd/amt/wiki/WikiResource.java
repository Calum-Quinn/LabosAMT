package ch.heigvd.amt.wiki;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path(WikiResource.path)
@Authenticated
public class WikiResource {

    static final String path = "/wiki";

    @Inject
    WikiService wikiService;

    @Location("wiki/service")
    Template service;

    @Location("wiki/wall")
    Template wall;

    @Inject
    Template home;

    @GET
    public TemplateInstance get() {
        return home.instance();
    }

    @GET
    @Path("service")
    public TemplateInstance state() {
        return service.data("state", wikiService.isRunning(),
                "nb", wikiService.getNbProcessed(),
                "lastnb", wikiService.getLastNbProcessed());
    }

    @GET
    @Path("wall")
    public TemplateInstance events() {
        return wall.instance();
    }

    @GET
    @Path("/start")
    public Response start() {
        wikiService.start();
        return Response.seeOther(URI.create(WikiResource.path+"/service")).build();
    }

    @GET
    @Path("/stop")
    public Response stop() {
        wikiService.stop();
        return Response.seeOther(URI.create(WikiResource.path+"/service")).build();
    }

    @GET
    @Path("/status")
    public String status() {
        var b = wikiService.isRunning();
        return b ? "running" : "stopped";
    }
}
