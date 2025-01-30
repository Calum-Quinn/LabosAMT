package ch.heigvd.amt.repository;

import ch.heigvd.amt.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Used for interactions with a User
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    // Return the user or creates it if it does not exist
    @Transactional
    public User findOrCreateById(long id, String username) {
        User user = findById(id);
        if (user == null) {
            user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setBalance(82_000L); // Credits at beginning of S5 (* 1000)
            persist(user);
        }

        return user;
    }

    // List all users in descending order of current credit balance
    @Transactional
    public List<User> listLeaderboard() {
        return getEntityManager()
                .createQuery("SELECT u FROM app_user u ORDER BY u.balance DESC")
                .getResultList();
    }

    // Return the amount of bets a user has created
    @Transactional
    public List<Object[]> listLeaderboardWithBetCounts() {
        return getEntityManager()
                .createQuery("SELECT u, SIZE(u.createdBets) FROM app_user u ORDER BY u.balance DESC", Object[].class)
                .getResultList();
    }

    @Transactional
    public void removeAll() {
        getEntityManager().createQuery("DELETE FROM app_user ").executeUpdate();
    }
}
