package ch.heigvd.amt.user;


import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.Date;

@Path(UserResource.path)
public class UserResource {
    static final String path = "/user";

    @Location("user/login")
    Template login;

    @Location("user/error")
    Template error;

    @Location("user/user")
    Template user;

    @Location("user/register")
    Template register;

    @Location("user/success")
    Template success;

    @Location("user/registrationError")
    Template registrationError;

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String cookieName;
    @Inject
    UserService userService;

    @GET
    @Path("logout")
    public Response logout() {
        final NewCookie removeCookie = new NewCookie.Builder(cookieName)
                .maxAge(0)
                .expiry(Date.from(Instant.EPOCH))
                .path("/")
                .build();
        return Response
                .seeOther(UriBuilder.fromUri("/wiki").build())
                .cookie(removeCookie)
                .build();
    }

    @GET
    @Authenticated
    public TemplateInstance user(@Context SecurityContext sc) {
        return user.data("username", sc.getUserPrincipal().getName());
    }

    @GET
    @Path("error")
    public TemplateInstance error() {
        return error.instance();
    }

    @GET
    @Path("success")
    public TemplateInstance success() {return success.instance();}

    @GET
    @Path("login")
    public TemplateInstance login() {
        return login.instance();
    }

    @GET
    @Path("/register")
    public TemplateInstance getregisterUser() {
        // TODO
        return register.instance();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response registerUser(@FormParam("username") String username, @FormParam("password") String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(registrationError.data("error", "Username and password are required."))
                    .build();
        }

        try {
            // Logic to create and store the user securely
            userService.registerUser(username, password);
            return Response.seeOther(UriBuilder.fromPath("/user/success").build()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(registrationError.data("error", "Registration failed: " + e.getMessage()))
                    .build();
        }
    }


}
