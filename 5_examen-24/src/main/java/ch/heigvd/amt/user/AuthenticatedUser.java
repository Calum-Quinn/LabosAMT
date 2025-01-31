package ch.heigvd.amt.user;

import io.quarkus.security.identity.CurrentIdentityAssociation;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.io.Serializable;

@Named("currentUser")
@Singleton
public class AuthenticatedUser implements Serializable{
    @Inject
    public CurrentIdentityAssociation identity;

    public boolean isAnonymous() {
        return identity.getIdentity().isAnonymous();
    }

    public String getUsername() {
        return identity.getIdentity().getPrincipal().getName();
    }
}

