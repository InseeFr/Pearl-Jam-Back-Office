package fr.insee.pearljam.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonTestHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JsonTestHelper() {
        throw new IllegalArgumentException("Utility class");
    }

    public static String toJson(Object object) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }
}
