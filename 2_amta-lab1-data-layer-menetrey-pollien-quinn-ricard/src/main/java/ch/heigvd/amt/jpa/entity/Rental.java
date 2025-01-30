package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Integer id;

    @Column(name = "rental_date")
    @NotNull
    private Instant rentalDate;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "return_date")
    private Instant returnDate;

    @OneToMany(mappedBy="rental")
    private List<Payment> payments = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull Instant getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(@NotNull Instant rentalDate) {
        this.rentalDate = rentalDate;
    }

    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setInventory(@NotNull Inventory inventory) {
        this.inventory = inventory;
    }

    public @NotNull Customer getCustomer() {
        return customer;
    }

    public void setCustomer(@NotNull Customer customer) {
        this.customer = customer;
    }

    public Instant getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Instant returnDate) {
        this.returnDate = returnDate;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
