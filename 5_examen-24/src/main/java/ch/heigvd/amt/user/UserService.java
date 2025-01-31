package ch.heigvd.amt.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserService {

    @Inject
    EntityManager em;

    @Transactional
    public void registerUser(String username, String password) {
        // Check if username already exists
        if (usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        // Create and persist the user
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        em.persist(user);
    }

    public boolean usernameExists(String username) {
        try {
            return em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    public void updateLastLogin(String userName) {
        // TODO
    }
}
