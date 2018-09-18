package com.gdsoft.jjparser.Beans;

import com.gdsoft.jjparser.ParserConstants;
import com.gdsoft.jjparser.ParserException;
import com.gdsoft.jjparser.SchemaParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
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
        int minimumProperties = -1;
        int maximumProperties = -1;
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
        if (schema.has(MIN_PROPERTIES)) {
            String minPropertiesString = schema.get(MIN_PROPERTIES).getAsString().replaceAll
                    (ParserConstants.REGEX, "");
            if (!minPropertiesString.isEmpty()) {
                minimumProperties = DataTypeConverter.convertToInt(minPropertiesString);
            }
        }
        if (schema.has(MAX_PROPERTIES)) {
            String maxPropertiesString = schema.get(MAX_PROPERTIES).getAsString().replaceAll
                    (ParserConstants.REGEX, "");
            if (!maxPropertiesString.isEmpty()) {
                maximumProperties = DataTypeConverter.convertToInt(maxPropertiesString);
            }
        }
        JsonObject schemaObject = (JsonObject) schema.get("properties");
        Set<Map.Entry<String, JsonElement>> entryInput = object.entrySet();
        int numOfProperties = entryInput.size();
        if(minimumProperties != -1 && numOfProperties < minimumProperties) {
            throw new ParserException("Object violates the minimum number of properties constraint");
        }
        if(maximumProperties != -1 && numOfProperties > maximumProperties) {
            throw new ParserException("Object violates the maximum number of properties constraint");
        }
        if (!additionalProperties) {
            for (Map.Entry<String, JsonElement> temp : entryInput) {
                if (!schemaObject.has(temp.getKey())) {
                    throw new ParserException("Object have additional elements not specified in the schema");
                }
            }
        }
        if (schema.has(PATTERN_PROPERTIES)) {
            JsonObject patternsObject = schema.getAsJsonObject(PATTERN_PROPERTIES);
            Set<Map.Entry<String, JsonElement>> patterns = patternsObject.entrySet();
            for (Map.Entry<String, JsonElement> pattern : patterns) {
                String regex = pattern.getKey();
                JsonObject tempSchema = pattern.getValue().getAsJsonObject();
                String type = tempSchema.get(ParserConstants.TYPE_KEY).getAsString().replaceAll(ParserConstants.REGEX, "");

            }
        }
    }
}
