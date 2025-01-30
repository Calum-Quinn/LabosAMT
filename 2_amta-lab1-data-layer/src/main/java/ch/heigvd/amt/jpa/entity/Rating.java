package ch.heigvd.amt.jpa.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Objects;

public enum Rating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");


    private final String value;

    Rating(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Rating fromString(String value) {
        return Arrays.stream(values())
                .filter(e -> Objects.equals(e.value, value))
                .findAny()
                .orElse(null);
    }


    @Converter(autoApply = true)
    public static class RatingToStringConverter implements AttributeConverter<Rating, String> {

        @Override
        public String convertToDatabaseColumn(Rating mpaaRating) {
            return mpaaRating.toString();
        }

        @Override
        public Rating convertToEntityAttribute(String s) {
            return Rating.fromString(s);
        }
    }
}
