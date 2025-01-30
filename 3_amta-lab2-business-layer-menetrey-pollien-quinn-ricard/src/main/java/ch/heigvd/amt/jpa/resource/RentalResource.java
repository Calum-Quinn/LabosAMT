package ch.heigvd.amt.jpa.resource;

import ch.heigvd.amt.jpa.entity.Customer;
import ch.heigvd.amt.jpa.entity.Inventory;
import ch.heigvd.amt.jpa.entity.Staff;
import ch.heigvd.amt.jpa.service.RentalService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

// The existing annotations on this class must not be changed (i.e. new ones are allowed)
@Path("rental")
public class RentalResource {

    private final RentalService rentalService;
    private final EntityManager em;

    @jakarta.inject.Inject
    public RentalResource(RentalService rentalService, EntityManager em) {
        this.rentalService = rentalService;
        this.em = em;
    }

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance rental(String username);

        public static native TemplateInstance rental$success(RentalService.RentalDTO rental);

        public static native TemplateInstance rental$failure(String message);

        public static native TemplateInstance searchFilmsResults(
                List<RentalService.FilmInventoryDTO> films);

        public static native TemplateInstance searchFilmsSelect(
                RentalService.FilmInventoryDTO film);

        public static native TemplateInstance searchCustomersResults(
                List<RentalService.CustomerDTO> customers);

        public static native TemplateInstance searchCustomersSelect(
                RentalService.CustomerDTO customer);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public TemplateInstance rental(@Context SecurityContext securityContext) {
        return Templates.rental(securityContext.getUserPrincipal().getName());
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    @Authenticated
    public TemplateInstance registerRental(@Context SecurityContext securityContext,
                                           @RestForm Integer inventory, @RestForm Integer customer) {
        if (inventory == null || customer == null) {
            return Templates.rental$failure("The submission is not valid, missing inventory or customer");
        }

        Inventory inventoryObj = new Inventory();
        inventoryObj.setId(inventory);

        Customer customerObj = new Customer();
        customerObj.setId(customer);

        // Get staff by username
        Staff staff = em.createQuery("SELECT s from Staff s where s.username = :username", Staff.class)
                .setParameter("username", securityContext.getUserPrincipal().getName())
                .getSingleResult();

        var rental = rentalService.rentFilm(inventoryObj, customerObj, staff);
        if (rental.isPresent()) {
            return Templates.rental$success(rental.get());
        } else {
            return Templates.rental$failure("The selected item is not available.");
        }
    }

    @GET
    @Path("/film/{inventory}")
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    public TemplateInstance selectFilmsGet(@PathParam("inventory") Integer inventory) {
        RentalService.FilmInventoryDTO filmInventory = rentalService.searchFilmInventory(inventory);
        return Templates.searchFilmsSelect(filmInventory);
    }

    @POST
    @Path("/film/search")
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    public TemplateInstance searchFilmsPost(@Context SecurityContext securityContext, @RestForm String query) {
        // Get staff by username
        Staff staff = em.createQuery("SELECT s from Staff s where s.username = :username", Staff.class)
                .setParameter("username", securityContext.getUserPrincipal().getName())
                .getSingleResult();

        List<RentalService.FilmInventoryDTO> results = rentalService.searchFilmInventory(query, staff.getStore());
        return Templates.searchFilmsResults(results);
    }

    @POST
    @Path("/customer/search")
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    public TemplateInstance searchCustomersPost(@Context SecurityContext securityContext, @RestForm String query) {
        // Get staff by username
        Staff staff = em.createQuery("SELECT s from Staff s where s.username = :username", Staff.class)
                .setParameter("username", securityContext.getUserPrincipal().getName())
                .getSingleResult();

        return Templates.searchCustomersResults(rentalService.searchCustomer(query, staff.getStore()));
    }

    @GET
    @Path("/customer/{customer}")
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    public TemplateInstance selectCustomerGet(@PathParam("customer") Integer customer) {
        return Templates.searchCustomersSelect(rentalService.searchCustomer(customer));
    }
}
