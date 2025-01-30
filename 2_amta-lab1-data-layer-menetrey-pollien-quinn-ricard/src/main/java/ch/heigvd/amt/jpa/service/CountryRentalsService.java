package ch.heigvd.amt.jpa.service;

import ch.heigvd.amt.jpa.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.util.Collections;
import java.util.List;

/**
 * Exercise Country by rentals.
 * Signature of methods (countryRentals_*) must not be changed.
 */
@ApplicationScoped
public class CountryRentalsService {

    @Inject
    private EntityManager em;

    public record CountryRentals(String country, Long rentals) {
    }

    public List<CountryRentals> countryRentals_NativeSQL() {
        //noinspection unchecked
        return em.createNativeQuery("""
                SELECT c.country, count(distinct r.rental_id) as rentals
                FROM country c
                INNER JOIN city cty ON c.country_id = cty.country_id
                INNER JOIN public.address a on cty.city_id = a.city_id
                INNER JOIN public.customer c2 on a.address_id = c2.address_id
                INNER JOIN public.rental r on c2.customer_id = r.customer_id
                GROUP BY c.country, c.country_id
                ORDER BY rentals DESC, c.country, c.country_id
                """, CountryRentals.class).getResultList();
    }

    public List<CountryRentals> countryRentals_JPQL() {
        return em.createQuery("""
                        select c.country, count(distinct r.id) as rentals
                        from country c
                        inner join c.cities cty
                        inner join cty.addresses a
                        inner join a.customers cst
                        inner join cst.rentals r
                        group by c.country, c.id
                        order by rentals desc, c.country, c.id
                        """, CountryRentals.class)
                .getResultList();
    }

    public List<CountryRentals> countryRentals_CriteriaString() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CountryRentals> q = cb.createQuery(CountryRentals.class);

        Root<Country> country = q.from(Country.class);
        Join<Country, City> city = country.join("cities");
        Join<City, Address> address = city.join("addresses");
        Join<Address, Customer> customer = address.join("customers");
        Join<Customer, Rental> rental = customer.join("rentals");

        Expression<Long> rentalCount = cb.countDistinct(rental.get("id"));
        q.multiselect(
                country.get("country"),
                rentalCount
        );

        q.groupBy(country.get("country"), country.get("id"));
        q.orderBy(
                cb.desc(rentalCount),
                cb.asc(country.get("country")),
                cb.asc(country.get("id"))
        );

        return em.createQuery(q).getResultList();
    }

    public List<CountryRentals> countryRentals_CriteriaMetaModel() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CountryRentals> q = cb.createQuery(CountryRentals.class);

        Root<Country> country = q.from(Country.class);
        Join<Country, City> city = country.join(Country_.cities);
        Join<City, Address> address = city.join(City_.addresses);
        Join<Address, Customer> customer = address.join(Address_.customers);
        Join<Customer, Rental> rental = customer.join(Customer_.rentals);

        Expression<Long> rentalCount = cb.countDistinct(rental.get(Rental_.id));
        q.multiselect(
                country.get(Country_.country),
                rentalCount
        );

        q.groupBy(country.get(Country_.country), country.get(Country_.id));
        q.orderBy(
                cb.desc(rentalCount),
                cb.asc(country.get(Country_.country)),
                cb.asc(country.get(Country_.id))
        );

        return em.createQuery(q).getResultList();
    }
}
