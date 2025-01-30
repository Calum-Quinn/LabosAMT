package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.TimeZoneColumn;
import org.hibernate.annotations.Type;
import org.hibernate.type.NumericBooleanConverter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.util.List;

@Entity(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "first_name", length = 45)
    @NotNull
    private String firstName;

    @Column(name = "last_name", length = 45)
    @NotNull
    private String lastName;

    @Column(name = "email", length = 50)
    private String email;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "active")
    @Convert(converter = NumericBooleanConverter.class)
    @NotNull
    private Boolean active = true;

    @Column(name = "create_date", columnDefinition = "DATE")
    private Instant creationDate;

    @OneToMany(mappedBy="customer")
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Rental> rentals = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull Store getStore() {
        return store;
    }

    public void setStore(@NotNull Store store) {
        this.store = store;
    }

    public @NotNull String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull String firstName) {
        this.firstName = firstName;
    }

    public @NotNull String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public @NotNull Address getAddress() {
        return address;
    }

    public void setAddress(@NotNull Address address) {
        this.address = address;
    }

    @NotNull
    public boolean isActive() {
        return active;
    }

    public void setActive(@NotNull boolean active) {
        this.active = active;
    }

    public @NotNull Boolean getActive() {
        return active;
    }

    public void setActive(@NotNull Boolean active) {
        this.active = active;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
