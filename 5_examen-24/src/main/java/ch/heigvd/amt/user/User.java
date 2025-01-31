package ch.heigvd.amt.user;

import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * An entity representing a user.
 */
@UserDefinition
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Username
    private String username;

    @Password
    private String password;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Roles
    private String role = "";

    /**
     * Default constructor.
     */
    public User() {

    }

    /**
     * Returns the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() {   return password; }

    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
