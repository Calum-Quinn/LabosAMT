package ch.heigvd.amt.jpa.entity;

import io.quarkus.security.jpa.RolesValue;
import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.*;

@Entity(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role", columnDefinition = "text")
    @NotNull
    @RolesValue
    private String role;

    public Role(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
