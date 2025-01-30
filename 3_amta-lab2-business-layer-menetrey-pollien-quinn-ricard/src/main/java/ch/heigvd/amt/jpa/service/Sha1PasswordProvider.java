package ch.heigvd.amt.jpa.service;

import io.quarkus.security.jpa.PasswordProvider;
import jakarta.xml.bind.DatatypeConverter;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.interfaces.SimpleDigestPassword;

public class Sha1PasswordProvider implements PasswordProvider {
    @Override
    public Password getPassword(String passwordFromDatabase) {
        byte[] digest = DatatypeConverter.parseHexBinary(passwordFromDatabase);

        return SimpleDigestPassword.createRaw(SimpleDigestPassword.ALGORITHM_SIMPLE_DIGEST_SHA_1, digest);
    }
}
