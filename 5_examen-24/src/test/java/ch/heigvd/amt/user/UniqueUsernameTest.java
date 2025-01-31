package ch.heigvd.amt.user;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UniqueUsernameTest {
    @Inject
    EntityManager em;

    @Test
    @Transactional
    /**
     * TODO: transform this test after the fix to ensure that that is not possible to register more than once with the same username
     */
    public void testUniqueUsername() {
        insertUser("amt", "amt123");
        insertUser("amt", "amt123");

        Assertions.assertEquals(1L, nbUsersWithSameUsername("amt"));
    }

    private Long nbUsersWithSameUsername(String username) {
        return em.createQuery("SELECT count(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    private void insertUser(String username, String password) {
        var user = new User();
        user.setUsername(username);
        user.setPassword(password);
        em.persist(user);
    }
}
