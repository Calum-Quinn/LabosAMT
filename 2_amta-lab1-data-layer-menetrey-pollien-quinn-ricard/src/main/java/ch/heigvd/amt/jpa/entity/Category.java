package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "name", length = 25)
    private String name;

    @ManyToMany(mappedBy="categories")
    private List<Film> films = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
