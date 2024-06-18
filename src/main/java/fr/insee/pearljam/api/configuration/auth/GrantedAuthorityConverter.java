package fr.insee.pearljam.api.configuration.auth;

import fr.insee.pearljam.api.configuration.properties.RoleProperties;
import lombok.AllArgsConstructor;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
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
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Map<String, Object> realmAccess = (Map<String, Object>) claims.get(REALM_ACCESS);
        List<String> roles = (List<String>) realmAccess.get(REALM_ACCESS_ROLE);

        return roles.stream()
                .map(role -> switch (role) {
                    case String r when r.equals(roleProperties.local_user()) ->
                        new SimpleGrantedAuthority(AuthConstants.generateAuthority(AuthorityRoleEnum.LOCAL_USER));
                    case String r when r.equals(roleProperties.national_user()) ->
                        new SimpleGrantedAuthority(AuthConstants.generateAuthority(AuthorityRoleEnum.NATIONAL_USER));
                    case String r when r.equals(roleProperties.interviewer()) ->
                        new SimpleGrantedAuthority(AuthConstants.generateAuthority(AuthorityRoleEnum.INTERVIEWER));
                    case String r when r.equals(roleProperties.admin()) ->
                        new SimpleGrantedAuthority(AuthConstants.generateAuthority(AuthorityRoleEnum.ADMIN));
                    case String r when r.equals(roleProperties.webclient()) ->
                        new SimpleGrantedAuthority(AuthConstants.generateAuthority(AuthorityRoleEnum.WEBCLIENT));
                    default -> null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}