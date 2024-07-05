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

    private final Map<String, List<SimpleGrantedAuthority>> roles;

    public GrantedAuthorityConverter(RoleProperties roleProperties) {
        this.roles = new HashMap<>();
        initRole(roleProperties.local_user(), AuthorityRole.LOCAL_USER);
        initRole(roleProperties.national_user(), AuthorityRole.NATIONAL_USER);
        initRole(roleProperties.interviewer(), AuthorityRole.INTERVIEWER);
        initRole(roleProperties.admin(), AuthorityRole.ADMIN);
        initRole(roleProperties.webclient(), AuthorityRole.WEBCLIENT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Map<String, Object> realmAccess = (Map<String, Object>) claims.get(REALM_ACCESS);
        List<String> userRoles = (List<String>) realmAccess.get(REALM_ACCESS_ROLE);

        return userRoles.stream()
                .filter(this.roles::containsKey)
                .map(this.roles::get)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void initRole(String configRole, AuthorityRole authorityRole)  {
        // config role is not set
        if(configRole == null || configRole.isBlank()) {
            return;
        }

        this.roles.compute(configRole, (key, grantedAuthorities) -> {
            if(grantedAuthorities == null) {
                grantedAuthorities = new ArrayList<>();
            }
            grantedAuthorities.add(new SimpleGrantedAuthority(authorityRole.securityRole()));
            return grantedAuthorities;
        });
    }
}