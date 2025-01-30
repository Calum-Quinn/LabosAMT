package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull Film getFilm() {
        return film;
    }

    public void setFilm(@NotNull Film film) {
        this.film = film;
    }

    public @NotNull Store getStore() {
        return store;
    }

    public void setStore(@NotNull Store store) {
        this.store = store;
    }
}
