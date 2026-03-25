package fr.insee.pearljam.api.configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fr.insee.pearljam.api.configuration.log.InvalidTokenUserExtractor;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidTokenUserExtractorTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef"; // 32 chars for HS256

    private final InvalidTokenUserExtractor extractor = new InvalidTokenUserExtractor("sub");

    @Test
    void shouldReturnUserIdWhenClaimPresent() throws Exception {
        String token = generateSignedTokenWithClaim("sub", "user123");

        String result = extractor.extractUserId(token);

        assertThat(result).isEqualTo("user123");
    }

    @Test
    void shouldReturnNullWhenClaimMissing() throws Exception {
        String token = generateSignedTokenWithoutClaim();

        String result = extractor.extractUserId(token);

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullWhenClaimBlank() throws Exception {
        String token = generateSignedTokenWithClaim("sub", "   ");

        String result = extractor.extractUserId(token);

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullWhenTokenIsNull() {
        String result = extractor.extractUserId(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullWhenTokenIsMalformed() {
        String result = extractor.extractUserId("not-a-jwt");

        assertThat(result).isNull();
    }

    // ----------------- Helpers -----------------

    private String generateSignedTokenWithClaim(String claimName, String claimValue) throws JOSEException {
        JWTClaimsSet claims = baseClaimsBuilder()
                .claim(claimName, claimValue)
                .build();
        return sign(claims);
    }

    private String generateSignedTokenWithoutClaim() throws JOSEException {
        JWTClaimsSet claims = baseClaimsBuilder().build();
        return sign(claims);
    }

    private JWTClaimsSet.Builder baseClaimsBuilder() {
        return new JWTClaimsSet.Builder()
                .issuer("test-issuer")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(3600)));
    }

    private String sign(JWTClaimsSet claims) throws JOSEException {
        JWSSigner signer = new MACSigner(SECRET.getBytes(StandardCharsets.UTF_8));
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
}
