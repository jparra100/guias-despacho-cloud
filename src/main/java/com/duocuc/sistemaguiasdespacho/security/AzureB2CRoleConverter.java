package com.duocuc.sistemaguiasdespacho.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Azure AD B2C entrega los roles personalizados como un "custom attribute"
 * (extension_..._Role) dentro del JWT, NO como el claim estandar "roles".
 *
 * Este conversor:
 *  1. Lee el claim configurado en el nombre completo que Azure B2C genera
 *     (ej: extension_a1b2c3d4e5_Role), configurable por application.yml.
 *  2. Lo transforma en una GrantedAuthority con prefijo ROLE_, que es lo que
 *     Spring Security espera para usar hasRole("OPERADOR") en los controllers.
 *
 * IMPORTANTE: el nombre exacto del claim (extension_<appId>_Role) se obtiene
 * desde Azure AD B2C -> App registrations -> tu API -> Token configuration,
 * o desde el propio JWT decodificado en jwt.io una vez que un usuario de
 * prueba inicia sesion. Debes reemplazar el valor en application.yml.
 */
@Component
public class AzureB2CRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${azure.b2c.role-claim-name}")
    private String roleClaimName;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extraerAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities, obtenerPrincipalName(jwt));
    }

    private Collection<GrantedAuthority> extraerAuthorities(Jwt jwt) {
        Object claimValue = jwt.getClaims().get(roleClaimName);

        if (claimValue == null) {
            return Collections.emptyList();
        }

        // El custom claim puede venir como un solo String o como una lista,
        // dependiendo de como se configuro el user flow en Azure B2C.
        List<String> roles;
        if (claimValue instanceof List<?> lista) {
            roles = lista.stream().map(Object::toString).collect(Collectors.toList());
        } else {
            roles = List.of(claimValue.toString());
        }

        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()))
                .collect(Collectors.toList());
    }

    private String obtenerPrincipalName(Jwt jwt) {
        String nombre = jwt.getClaimAsString("emails");
        if (nombre == null) {
            nombre = jwt.getSubject();
        }
        return nombre;
    }
}
