package com.pedidos360.notifications.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.regex.Pattern;

/**
 * Valida que el issuer tenga el formato esperado de Azure AD v2.0 y que el
 * tenant en la URL coincida con el claim "tid" del propio token - necesario
 * porque la app esta registrada como multi-tenant + cuentas personales, asi
 * que no hay un unico issuer fijo contra el cual comparar.
 */
public class MultiTenantIssuerValidator implements OAuth2TokenValidator<Jwt> {

    private static final Pattern ISSUER_PATTERN =
            Pattern.compile("^https://login\\.microsoftonline\\.com/([a-zA-Z0-9-]+)/v2\\.0$");

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : null;
        String tid = jwt.getClaimAsString("tid");

        if (issuer == null || tid == null) {
            return fallo("El token no tiene issuer o claim tid validos");
        }

        var matcher = ISSUER_PATTERN.matcher(issuer);
        if (!matcher.matches()) {
            return fallo("El issuer no tiene el formato esperado de Azure AD: " + issuer);
        }

        if (!matcher.group(1).equals(tid)) {
            return fallo("El tenant del issuer no coincide con el claim tid del token");
        }

        return OAuth2TokenValidatorResult.success();
    }

    private OAuth2TokenValidatorResult fallo(String mensaje) {
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", mensaje, null));
    }
}
