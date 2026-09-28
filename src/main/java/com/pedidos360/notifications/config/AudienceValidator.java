package com.pedidos360.notifications.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

/**
 * Acepta tanto "api://<client-id>" como el client-id sin prefijo, porque
 * Azure AD emite el "aud" de una u otra forma segun el tipo de cuenta.
 */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final Collection<String> audiencesValidas;

    public AudienceValidator(Collection<String> audiencesValidas) {
        this.audiencesValidas = audiencesValidas;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getAudience() != null && jwt.getAudience().stream().anyMatch(audiencesValidas::contains)) {
            return OAuth2TokenValidatorResult.success();
        }
        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "El token no contiene ninguno de los audiences esperados: " + audiencesValidas,
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
