package fr.insee.pearljam.api.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * Utility class used to sanitize user provided text values in order to prevent
 * HTML/JavaScript execution when the content is rendered in a web
 * application.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SanitizationUtils {

    private static final Safelist SAFE_LIST = Safelist.none();

    /**
     * Cleans a raw text value by stripping any HTML markup or script payloads.
     *
     * @param value the raw value supplied by the user.
     * @return a sanitized version of the input or {@code null} when the input is
     *         {@code null}.
     */
    public static String sanitize(String value) {
        if (value == null) {
            return null;
        }
        return Jsoup.clean(value, SAFE_LIST);
    }
}

