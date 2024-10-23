package fr.insee.pearljam.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class JsonTestHelper {

    public JsonTestHelper() {
        throw new IllegalArgumentException("Utility class");
    }

    public static String toJson(Object object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
