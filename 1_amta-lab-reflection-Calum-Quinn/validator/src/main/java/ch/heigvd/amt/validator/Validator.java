package ch.heigvd.amt.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * This class is used to validate annotated objects at runtime.
 */
public class Validator {

    /**
     * This method validates an object by checking all its fields.
     *
     * @param object the object to validate.
     */
    public static void validate(Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        System.out.println("Validating " + object);

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            Annotation[] annotations = field.getAnnotations();

            boolean isValid = true;
            for (Annotation annotation : annotations) {
                if (annotation instanceof NotNull) {
                    isValid = validateNotNull(value);
                    if (!isValid) {
                        System.out.println("- " + field.getName() + " is null");
                    }
                } else if (annotation instanceof Range) {
                    isValid = validateRange(value, (Range) annotation);
                    if (!isValid) {
                        System.out.println("- " + field.getName() + " is not in range [" +
                                ((Range) annotation).min() + ", " + ((Range) annotation).max() + "]");
                    }
                } else if (annotation instanceof Regex) {
                    isValid = validateRegex(value, (Regex) annotation);
                    if (!isValid) {
                        System.out.println("- " + field.getName() + " does not match regex " + ((Regex) annotation).value());
                    }
                }
                if (isValid) {
                    System.out.println("- " + field.getName() + " is valid");
                }
            }
        }
    }

    private static boolean validateNotNull(Object value) {
        return value != null;
    }

    private static boolean validateRange(Object value, Range range) {
        if (value instanceof Integer) {
            int intValue = (int) value;
            return intValue >= range.min() && intValue <= range.max();
        }
        return false;
    }

    private static boolean validateRegex(Object value, Regex regex) {
        if (value instanceof String stringValue) {
            return Pattern.matches(regex.value(), stringValue);
        }
        return false;
    }

    /**
     * This class is used to test the validator.
     * Its fields are annotated with the validator annotations.
     */
    static class Person {

        @NotNull
        String username;

        @Range(min = 0, max = 100)
        int age;

        @Regex("[a-z]+@[a-z]+\\.[a-z]+")
        String email;

        @Regex("\\+[0-9]{2} [0-9]{2} [0-9]{3} [0-9]{2} [0-9]{2}")
        String phoneNumber;

        public Person(String username, int age, String email, String phoneNumber) {
            this.username = username;
            this.age = age;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "username='" + username + '\'' +
                    ", age=" + age +
                    ", email='" + email + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    '}';
        }
    }

    public static void main(String... args) {
        validate(new Person("john", 42, "john@example.com", "+41 79 123 45 67"));
        validate(new Person(null, 200, "john@example", "079 123 45 67"));
    }
}
