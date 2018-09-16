package com.gdsoft.jjparser.Beans;

import com.gdsoft.jjparser.ParserConstants;
import com.gdsoft.jjparser.ParserException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ObjectValidator {

    private final static String ADDITIONAL_PROPERTIES = "additionalProperties";
    private final static String REQUIRED = "required";
    private final static String MIN_PROPERTIES = "minProperties";
    private final static String MAX_PROPERTIES = "maxProperties";
    private final static String DEPENDENCIES = "dependencies";
    private final static String PATTERN_PROPERTIES = "patternProperties";
    private final static String REGEXP = "regexp";

    public static void validateObject(JsonObject object, JsonObject schema) throws ParserException {
        boolean additionalProperties = true;
        if (schema.has(REQUIRED)) {
            if (schema.get(REQUIRED).isJsonArray()) {
                JsonArray requiredArray = schema.getAsJsonArray(REQUIRED);
                for (JsonElement element : requiredArray) {
                    if (!object.has(element.getAsString())) {
                        throw new ParserException("Object does not have all the elements required in the schema");
                    }
                }
            }
        }
        if (schema.has(ADDITIONAL_PROPERTIES)) {
            String additionalPropertyString = schema.get(ADDITIONAL_PROPERTIES).getAsString().replaceAll
                    (ParserConstants.REGEX, "");
            if (!additionalPropertyString.isEmpty()) {
                additionalProperties = DataTypeConverter.convertToBoolean(additionalPropertyString);
            }
        }
        JsonObject schemaObject = (JsonObject) schema.get("properties");
        Set<Map.Entry<String, JsonElement>> entryInput = object.entrySet();

        if (!additionalProperties) {
            for (Map.Entry<String, JsonElement> temp : entryInput) {
                if (!schemaObject.has(temp.getKey())) {
                    throw new ParserException("Object have additional elements not specified in the schema");
                }
            }
        }

    }
}
