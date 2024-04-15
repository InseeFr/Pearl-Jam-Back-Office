package fr.insee.pearljam.api.configuration.auth;

import fr.insee.pearljam.api.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class GrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String REALM_ACCESS_ROLE = "roles";
    private static final String REALM_ACCESS = "realm_access";

    private final RoleProperties roleProperties;

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Map<String, Object> realmAccess = (Map<String, Object>) claims.get(REALM_ACCESS);
        List<String> roles = (List<String>) realmAccess.get(REALM_ACCESS_ROLE);

        return roles.stream()
                .map(role -> {
                    if (role.equals(roleProperties.local_user())) {
                        return new SimpleGrantedAuthority(
                                AuthConstants.generateAuthority(AuthorityRoleEnum.LOCAL_USER));
                    }
                    if (role.equals(roleProperties.national_user())) {
                        return new SimpleGrantedAuthority(
                                AuthConstants.generateAuthority(AuthorityRoleEnum.NATIONAL_USER));
                    }
                    if (role.equals(roleProperties.interviewer())) {
                        return new SimpleGrantedAuthority(
                                AuthConstants.generateAuthority(AuthorityRoleEnum.INTERVIEWER));
                    }
                    if (role.equals(roleProperties.admin())) {
                        return new SimpleGrantedAuthority(AuthConstants.generateAuthority(AuthorityRoleEnum.ADMIN));
                    }
                    if (role.equals(roleProperties.webclient())) {
                        return new SimpleGrantedAuthority(AuthConstants.generateAuthority(AuthorityRoleEnum.WEBCLIENT));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}