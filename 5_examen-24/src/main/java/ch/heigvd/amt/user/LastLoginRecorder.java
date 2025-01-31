package ch.heigvd.amt.user;

import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.FormAuthenticationEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;


@ApplicationScoped
public class LastLoginRecorder {

    @Inject
    EntityManager entityManager;

    /**
     * Method observing the authentication event based of Quarkus Security Forms Authentication
     * https://quarkus.io/guides/security-customization#observe-security-events
     * @param event
     */
    void observeAuthenticationSuccess(@ObservesAsync FormAuthenticationEvent event) {
        // Get the SecurityIdentity from the event
        SecurityIdentity identity = event.getSecurityIdentity();

        // Get the username from the SecurityIdentity
        String username = identity.getPrincipal().getName();

        // Update the last_login timestamp for the user
        entityManager.createQuery("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.username = :username")
                .setParameter("lastLogin", LocalDateTime.now())
                .setParameter("username", username)
                .executeUpdate();
    }
}
