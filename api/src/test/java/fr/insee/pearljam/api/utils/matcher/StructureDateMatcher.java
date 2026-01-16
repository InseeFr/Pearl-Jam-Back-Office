package fr.insee.pearljam.api.utils.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StructureDateMatcher extends TypeSafeMatcher<String> {
    private final DateTimeFormatter dateFormatter;
    private final String dateFormat;

    public StructureDateMatcher() {
        this.dateFormat = "dd/MM/yyyy HH:mm:ss";
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    public StructureDateMatcher(String dateFormat) {
        this.dateFormat = dateFormat;
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Override
    protected boolean matchesSafely(String dateString) {
        try {
            LocalDateTime.parse(dateString, dateFormatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Check date format structure");
    }

    @Override
    protected void describeMismatchSafely(String item, Description mismatchDescription) {
        mismatchDescription.appendText("was ")
                .appendValue(item)
                .appendValue(", expected format ")
                .appendValue(dateFormat);
    }
}