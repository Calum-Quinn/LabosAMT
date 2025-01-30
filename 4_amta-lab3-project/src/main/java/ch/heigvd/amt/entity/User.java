package ch.heigvd.amt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Database entity representing a User
 */
@Entity(name = "app_user")
public class User {
    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "username", length = 50)
    @NotNull
    private String username;

    // Amount of credits the user has
    @Column(name = "balance")
    private Long balance;

    @OneToMany(mappedBy = "creator")
    private List<Bet> createdBets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public List<Bet> getCreatedBets() {
        return createdBets;
    }

    public void setCreatedBets(List<Bet> createdBets) {
        this.createdBets = createdBets;
    }

    @Override
    public boolean equals(Object o) {
        // Check if the object is the same instance
        if (this == o) return true;

        // Check if the object is an instance of User
        if (!(o instanceof User other)) return false;

        // Compare all relevant fields for equality
        return id != null && id.equals(other.id) &&
                Objects.equals(username, other.username) &&
                Objects.equals(balance, other.balance);
    }
}
