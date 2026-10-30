package fr.isaaconnect.sweb.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JWTRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        ArrayList<GrantedAuthority> default_authorities = new ArrayList<>(grantedAuthoritiesConverter.convert(jwt));
        List<SimpleGrantedAuthority> roles = extractRoles(jwt);

        default_authorities.addAll(roles);

        return new JwtAuthenticationToken(jwt, default_authorities);
    }

    /// Convert the JWT payload `resource_access` -> `coursm2` -> `roles` -> `ROLE[]` into `ROLE_XXX` for Spring
    ///
    /// @param jwt The JWT granted by the user
    /// @return A list of SimpleGrantedAuthority because it's just a string
    private List<SimpleGrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> resources = jwt.getClaim("resource_access");
        if (resources == null) return List.of();

        Map<String, List<String>> coursm2 = (Map<String, List<String>>) resources.get("coursm2");
        if (coursm2 == null) return List.of();

        List<String> roles = coursm2.get("roles");
        if (roles == null) return List.of();

        return roles.stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
            .toList();
    }
}
