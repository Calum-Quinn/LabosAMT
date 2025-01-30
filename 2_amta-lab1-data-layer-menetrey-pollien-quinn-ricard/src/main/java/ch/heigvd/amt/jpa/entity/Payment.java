package ch.heigvd.amt.jpa.entity;

import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.Instant;

@Entity(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = true)
    private Rental rental;

    @Column(name = "amount")
    @NotNull
    private BigInteger amount;

    @Column(name = "payment_date")
    @NotNull
    private Instant payment_date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public Instant getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(Instant payment_date) {
        this.payment_date = payment_date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }
}
