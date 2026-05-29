package fr.insee.pearljam.api.utils;

import tools.jackson.databind.json.JsonMapper;

public final class JsonTestHelper {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    public JsonTestHelper() {
        throw new IllegalArgumentException("Utility class");
    }

    public static String toJson(Object object) {
        return JSON_MAPPER.writeValueAsString(object);
    }
}
