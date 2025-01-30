package ch.heigvd.amt.jpa.entity;

import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.*;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer id;

    @Column(name = "first_name", length = 45)
    @NotNull
    private String first_name;

    @Column(name = "last_name", length = 45)
    @NotNull
    private String last_name;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "picture")
    private DocFlavor.BYTE_ARRAY picture;

    @Column(name = "email", length = 50)
    private String email;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "manager_staff")
    private List<Store> stores = new ArrayList<>();

    @Column(name = "active")
    @NotNull
    private boolean active;

    @Column(name = "username", length = 16)
    @NotNull
    private String username;

    @Column(name = "password", length = 40)
    private String password;

    @OneToMany(mappedBy="staff")
    private List<Payment> payments = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public DocFlavor.BYTE_ARRAY getPicture() {
        return picture;
    }

    public void setPicture(DocFlavor.BYTE_ARRAY picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
