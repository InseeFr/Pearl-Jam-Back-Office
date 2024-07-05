package fr.insee.pearljam.infrastructure.security.config;

import lombok.AllArgsConstructor;
import fr.insee.pearljam.domain.security.model.AuthorityRole;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Converter used to map roles from jwt token to app roles
 */
@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    public static final String REALM_ACCESS_ROLE = "roles";
    public static final String REALM_ACCESS = "realm_access";

    private final Map<String, SimpleGrantedAuthority> grantedRoles;

    public GrantedAuthorityConverter(RoleProperties roleProperties) {
        this.grantedRoles = new HashMap<>();
        this.grantedRoles.put(roleProperties.local_user(),
                new SimpleGrantedAuthority(AuthorityRole.LOCAL_USER.securityRole()));
        this.grantedRoles.put(roleProperties.national_user(),
                new SimpleGrantedAuthority(AuthorityRole.NATIONAL_USER.securityRole()));
        this.grantedRoles.put(roleProperties.interviewer(),
                new SimpleGrantedAuthority(AuthorityRole.INTERVIEWER.securityRole()));
        this.grantedRoles.put(roleProperties.admin(),
                new SimpleGrantedAuthority(AuthorityRole.ADMIN.securityRole()));
        this.grantedRoles.put(roleProperties.webclient(),
                new SimpleGrantedAuthority(AuthorityRole.WEBCLIENT.securityRole()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Map<String, Object> realmAccess = (Map<String, Object>) claims.get(REALM_ACCESS);
        List<String> roles = (List<String>) realmAccess.get(REALM_ACCESS_ROLE);

        return roles.stream()
                .filter(Objects::nonNull)
                .filter(role -> !role.isBlank())
                .filter(grantedRoles::containsKey)
                .map(grantedRoles::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}