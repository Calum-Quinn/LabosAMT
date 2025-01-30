package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Integer id;

    @Column(name = "city", length = 50)
    private String city;

    @OneToMany(mappedBy = "city")
    private List<Address> addresses = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
