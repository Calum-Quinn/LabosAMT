package ch.heigvd.amt.jpa.repository;

import ch.heigvd.amt.jpa.entity.Film;
import ch.heigvd.amt.jpa.entity.Inventory;
import ch.heigvd.amt.jpa.entity.Store;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InventoryRepository {
    @Inject
    private EntityManager em;

    private InventoryDTO fromEntityToDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        return new InventoryDTO(
                inventory.getId(),
                inventory.getFilm().getId(),
                inventory.getStore().getId()
        );
    }

    public record InventoryDTO (Integer id, Integer filmdId, Integer storeId) {
        public static InventoryDTO create(Integer filmdId, Integer storeId) {
            return new InventoryDTO(null, filmdId, storeId);
        }
    }

    public InventoryDTO read(Integer id) {
        // TODO: implement solution
        Inventory inventory = em.find(Inventory.class, id);
        return this.fromEntityToDTO(inventory);
    }

    @Transactional
    public InventoryDTO create(InventoryDTO inventory) {
        // TODO: implement solution
        Inventory newInventory = new Inventory();
        newInventory.setFilm(em.find(Film.class, inventory.filmdId));
        newInventory.setStore(em.find(Store.class, inventory.storeId));

        em.persist(newInventory);
        return fromEntityToDTO(newInventory);
    }

    @Transactional
    public void update(InventoryDTO inventory) {
        // TODO: implement solution
        Inventory newInventory = em.find(Inventory.class, inventory.id);

        if (newInventory == null) {
            throw new IllegalArgumentException("Inventory with id "+ inventory.id + " does not exist");
        }

        newInventory.setFilm(em.find(Film.class, inventory.filmdId));
        newInventory.setStore(em.find(Store.class, inventory.storeId));

        em.merge(newInventory);
    }

    @Transactional
    public void delete(Integer id) {
        // TODO: implement solution
        Inventory inventory = em.find(Inventory.class, id);

        if (inventory == null) {
            throw new IllegalArgumentException("Inventory with id "+ id + " does not exist");
        }

        em.remove(inventory);
    }

}
