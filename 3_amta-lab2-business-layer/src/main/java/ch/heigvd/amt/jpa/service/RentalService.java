package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Signature of existing methods must not be changed.
 */
@ApplicationScoped
public class RentalService {

    @Inject
    EntityManager em;

    // The following records must not be changed
    public record RentalDTO(Integer inventory, Integer customer) {
    }

    public record FilmInventoryDTO(String title, String description, Integer inventoryId) {
    }

    public record CustomerDTO(Integer id, String firstName, String lastName) {
    }

    /**
     * Rent a film out of store's inventory for a given customer.
     *
     * @param inventory the inventory to rent
     * @param customer  the customer to which the inventory is rented
     * @param staff     the staff that process the customer's request in the store
     * @return an Optional that is present if rental is successful, if Optional is empty rental failed
     */
    @Transactional
    public Optional<RentalDTO> rentFilm(Inventory inventory, Customer customer, Staff staff) {
        // NOTE: This is a variable that is not dropped for the entire duration of the request in order to
        // ensure that the lock is persisted for the entirety of the operations.
        @SuppressWarnings("unused")
        Inventory lockedInventory = em.find(Inventory.class, inventory.getId(), LockModeType.PESSIMISTIC_WRITE);

        TypedQuery<Rental> foundRentalQuery = em.createQuery("SELECT r FROM rental r WHERE r.returnDate IS NULL AND r.inventory.id = :inventoryId", Rental.class);
        foundRentalQuery.setParameter("inventoryId", inventory.getId());

        Rental foundRental;
        try {
            foundRental = foundRentalQuery.getSingleResult();
        } catch (NoResultException e) {
            foundRental = null;
        }

        if (foundRental != null) return Optional.empty();

        Rental rentalToAdd = new Rental();
        rentalToAdd.setRentalDate(Timestamp.from(Instant.now()));
        rentalToAdd.setCustomer(customer);
        rentalToAdd.setInventory(inventory);
        rentalToAdd.setStaff(staff);
        em.persist(rentalToAdd);

        return Optional.of(new RentalDTO(inventory.getId(), customer.getId()));
    }

    /**
     * @param query the searched string
     * @return films matching the query
     */
    public List<FilmInventoryDTO> searchFilmInventory(String query, Store store) {
        // Search in the film inventory
        Query filmSearchQuery = em.createNativeQuery("""
                SELECT f.title, description, inventory_id from inventory i
                join film f on i.film_id = f.film_id
                where i.store_id = :storeId AND (to_tsvector(f.title) || to_tsvector(f.description) || to_tsvector(i.inventory_id::text)) @@ plainto_tsquery(:query);
                """, FilmInventoryDTO.class);
        filmSearchQuery.setParameter("storeId", store.getId());
        filmSearchQuery.setParameter("query", query);

        //noinspection unchecked
        return (List<FilmInventoryDTO>) filmSearchQuery.getResultList();
    }

    public FilmInventoryDTO searchFilmInventory(Integer inventoryId) {
        TypedQuery<FilmInventoryDTO> query = em.createQuery("""
                select f.title, f.description, i.id from inventory i
                join i.film f
                where i.id = :inventoryId""", FilmInventoryDTO.class);
        query.setParameter("inventoryId", inventoryId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CustomerDTO searchCustomer(Integer customerId) {
        TypedQuery<CustomerDTO> query = em.createQuery("""
                select c.id, c.firstName, c.lastName from customer c
                where c.id = :customerId""",CustomerDTO.class);
        query.setParameter("customerId", customerId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CustomerDTO> searchCustomer(String query, Store store) {
        Query customerSearchQuery = em.createNativeQuery("""
                SELECT c.customer_id, c.first_name, c.last_name from customer c
                where c.store_id = :storeId AND (to_tsvector(c.first_name) || to_tsvector(c.last_name) || to_tsvector(c.customer_id::text)) @@ plainto_tsquery(:query);
                """, CustomerDTO.class);
        customerSearchQuery.setParameter("storeId", store.getId());
        customerSearchQuery.setParameter("query", query);

        //noinspection unchecked
        return (List<CustomerDTO>) customerSearchQuery.getResultList();
    }
}
