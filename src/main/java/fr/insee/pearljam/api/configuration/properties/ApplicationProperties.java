package fr.insee.pearljam.api.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
        String host,
        String[] publicUrls,
        @NotEmpty(message = "cors origins must be specified (application.corsOrigins)") List<String> corsOrigins,
        @NotEmpty(message = "Folder where temp files will be created cannot be empty.") String tempFolder,
        @NotNull(message = "application.auth must be specified (KEYCLOAK or NOAUTH)") AuthEnumProperties auth,
        String guestOU) {

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ApplicationProperties that = (ApplicationProperties) o;
        return Objects.equals(host, that.host)
                && Arrays.equals(publicUrls, that.publicUrls)
                && Objects.equals(guestOU, that.guestOU)
                && auth.equals(that.auth);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(host, guestOU, auth);
        result = 31 * result + Arrays.hashCode(publicUrls);
        return result;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "host='" + host + '\'' +
                ", guestOU='" + guestOU + '\'' +
                ", publicUrls=" + Arrays.toString(publicUrls) +
                ", auth='" + auth + '\'' +
                '}';
    }

}