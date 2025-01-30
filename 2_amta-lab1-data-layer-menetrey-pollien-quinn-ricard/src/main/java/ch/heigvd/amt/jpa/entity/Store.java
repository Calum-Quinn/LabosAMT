package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToMany(mappedBy="store")
    private List<Staff> staff = new ArrayList<>();

    @ManyToOne
    @NotNull
    @JoinColumn(name = "manager_staff_id", nullable = false)
    private Staff manager_staff;

    @OneToMany(mappedBy = "store")
    private List<Inventory> inventory = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Customer> customers  = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull Address getAddress() {
        return address;
    }

    public void setAddress(@NotNull Address address) {
        this.address = address;
    }

    public List<Staff> getStaff() {
        return staff;
    }

    public void setStaff(List<Staff> inventory) {
        this.staff = staff;
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
