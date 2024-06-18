package fr.insee.pearljam.infrastructure.security.config;

import fr.insee.pearljam.api.configuration.properties.RoleProperties;
import fr.insee.pearljam.domain.security.model.AuthorityRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GrantedAuthorityConverterTest {

    private GrantedAuthorityConverter converter;

    private RoleProperties roleProperties;

    private static final String jwtRoleInterviewer = "interviewer";
    private static final String jwtRoleLocalUser = "localUser";
    private static final String jwtRoleNationalUser = "nationalUser";
    private static final String jwtRoleAdmin = "admin";
    private static final String jwtRoleWebclient = "webclient";

    @BeforeEach
    void init() {
        roleProperties = new RoleProperties(jwtRoleInterviewer, jwtRoleLocalUser, jwtRoleNationalUser, jwtRoleAdmin, jwtRoleWebclient);
        converter = new GrantedAuthorityConverter(roleProperties);
    }

    @Test
    @DisplayName("Given a JWT, when converting null or empty JWT role, then converting ignore these roles")
    void testConverter01() {
        List<String> tokenRoles = new ArrayList<>();
        tokenRoles.add(null);
        tokenRoles.add("");
        Jwt jwt = createJwt(tokenRoles);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Given a JWT, when converting roles, then convert only JWT roles matching roles in role properties")
    void testConverter02() {

        List<String> tokenRoles = List.of("dummyRole1", roleProperties.local_user(), "dummyRole2", roleProperties.national_user(), "dummyRole3", roleProperties.interviewer());
        Jwt jwt = createJwt(tokenRoles);

        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        new SimpleGrantedAuthority(AuthorityRole.INTERVIEWER.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRole.LOCAL_USER.securityRole()),
                        new SimpleGrantedAuthority(AuthorityRole.NATIONAL_USER.securityRole()));
    }

    @ParameterizedTest
    @MethodSource("provideJWTRoleWithAppRoleAssociated")
    @DisplayName("Given a JWT, when converting roles, then assure each JWT role is converted to equivalent app role")
    void testConverter03(String jwtRole, AuthorityRole appRole) {
        converter = new GrantedAuthorityConverter(roleProperties);
        List<String> tokenRoles = List.of(jwtRole);
        Jwt jwt = createJwt(tokenRoles);
        Collection<GrantedAuthority> authorities = converter.convert(jwt);
        assertThat(authorities)
                .hasSize(1)
                .contains(new SimpleGrantedAuthority(AuthorityRole.ROLE_PREFIX + appRole));
    }

    private static Stream<Arguments> provideJWTRoleWithAppRoleAssociated() {
        return Stream.of(
                Arguments.of(jwtRoleInterviewer, AuthorityRole.INTERVIEWER),
                Arguments.of(jwtRoleLocalUser, AuthorityRole.LOCAL_USER),
                Arguments.of(jwtRoleNationalUser, AuthorityRole.NATIONAL_USER),
                Arguments.of(jwtRoleAdmin, AuthorityRole.ADMIN),
                Arguments.of(jwtRoleWebclient, AuthorityRole.WEBCLIENT));
    }

    private Jwt createJwt(List<String> tokenRoles) {
        Map<String, Object> jwtHeaders = new HashMap<>();
        jwtHeaders.put("header", "headerValue");

        Map<String, Object> claims = new HashMap<>();
        Map<String, List<String>> realmRoles = new HashMap<>();
        realmRoles.put(GrantedAuthorityConverter.REALM_ACCESS_ROLE, tokenRoles);
        claims.put(GrantedAuthorityConverter.REALM_ACCESS, realmRoles);

        return new Jwt("user-id", Instant.now(), Instant.MAX, jwtHeaders, claims);
    }
}

