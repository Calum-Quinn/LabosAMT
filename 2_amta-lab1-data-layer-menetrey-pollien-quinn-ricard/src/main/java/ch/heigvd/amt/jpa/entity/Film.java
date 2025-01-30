package ch.heigvd.amt.jpa.entity;

import ch.heigvd.amt.jpa.mappings.StringArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Entity(name = "film")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Integer id;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "release_year")
    private Integer releaseYear;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @ManyToOne
    @JoinColumn(name = "original_language_id")
    private Language originalLanguage;

    @Column(name = "rental_duration", columnDefinition = "SMALLINT")
    @NotNull
    private short rentalDuration = 3;

    @Column(name = "rental_rate")
    @NotNull
    private BigDecimal rentalRate = BigDecimal.valueOf(4.99);

    @Column(name = "length", columnDefinition = "SMALLINT")
    private short length;

    @Column(name = "replacement_cost")
    @NotNull
    private BigDecimal replacementCost = BigDecimal.valueOf(19.99);

    @Column(name = "rating")
    @ColumnTransformer(write = "?::mpaa_rating")
    private Rating rating = Rating.G;

    @Column(name = "special_features", columnDefinition = "text[]")
    @Type(StringArrayType.class)
    private String[] specialFeatures;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "film_actor",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "actor_id")}
    )
    private List<Actor> actors;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "film_category",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")}
    )
    private List<Category> categories;

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    @NotNull
    public short getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(@NotNull short rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public @NotNull BigDecimal getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(@NotNull BigDecimal rentalRate) {
        this.rentalRate = rentalRate;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public @NotNull BigDecimal getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(@NotNull BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String[] getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(String[] specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", releaseYear=" + releaseYear +
                ", language=" + language +
                ", originalLanguage=" + originalLanguage +
                ", rentalDuration=" + rentalDuration +
                ", rentalRate=" + rentalRate +
                ", length=" + length +
                ", replacementCost=" + replacementCost +
                ", rating=" + rating +
                ", specialFeatures=" + Arrays.toString(specialFeatures) +
                ", actors=" + actors +
                ", categories=" + categories +
                '}';
    }
}
