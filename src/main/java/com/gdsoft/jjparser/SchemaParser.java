package com.gdsoft.jjparser;

import com.gdsoft.jjparser.Beans.NominalParser;
import com.gdsoft.jjparser.Beans.NumericParser;
import com.google.gson.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parsing a JSON according to the given schema
 */
public class SchemaParser {

    private static final String REGEX = "^\"|\"$";
    private static final String ITEM_KEY = "items";
    private static final String TYPE_KEY = "type";
    private static final String NULL_STRING = "null";


    private static final Set<String> NUMERIC_KEYS = new HashSet<>(Arrays.asList(
            new String[]{"numeric", "integer"}
    ));
    private static final Set<String> BOOLEAN_KEYS = new HashSet<>(Arrays.asList(
            new String[]{"boolean"}
    ));
    private static final Set<String> NOMINAL_KEYS = new HashSet<>(Arrays.asList(
            new String[]{"String", "string"}
    ));
    private static final Set<String> OBJECT_KEYS = new HashSet<>(Arrays.asList(
            new String[]{"object"}
    ));
    private static final Set<String> ARRAY_KEYS = new HashSet<>(Arrays.asList(
            new String[]{"array"}
    ));

    // restriction keywords

    private static final Set<String> RESTRICTION_OBJECT = new HashSet<>(Arrays.asList(
            new String[]{"properties", "additionalProperties", "required", "minProperties", "maxProperties",
                    "dependencies", "patternProperties", "regexp"}
    ));

    private void replacePrimitive(Map.Entry<String, JsonElement> input, JsonObject schemaObject) throws
            ParserException {
        String type = schemaObject.get(TYPE_KEY).getAsString().replaceAll(REGEX, "");
        String valueString = input.getValue().toString().replaceAll(REGEX, "");
        if (NOMINAL_KEYS.contains(type)) {
            input.setValue(NominalParser.parseNominal(schemaObject, valueString));
        } else if (NUMERIC_KEYS.contains(type)) {
            input.setValue(NumericParser.parseNumeric(schemaObject, valueString));
        } else if (BOOLEAN_KEYS.contains(type)) {
            input.setValue(new JsonPrimitive(Boolean.valueOf(valueString)));
        }
    }

    private JsonPrimitive replacePrimitive(String value, String type) {
        JsonPrimitive primitive = null;
        if (!value.equals(NULL_STRING)) {
            if (NUMERIC_KEYS.contains(type)) {
                primitive = new JsonPrimitive(Float.valueOf(value));
            } else if (BOOLEAN_KEYS.contains(type)) {
                primitive = new JsonPrimitive(Boolean.valueOf(value));
            } else if (NOMINAL_KEYS.contains(type)) {
                primitive = new JsonPrimitive(value);
            }
        } else {
            primitive = new JsonPrimitive(new JsonNull().toString());
        }
        return primitive;
    }

    private void parseArray(Map.Entry<String, JsonElement> input, JsonObject schema) throws ParserException {
        //Convert single element to an array
        if (input.getValue().isJsonPrimitive() || input.getValue().isJsonNull()) {
            JsonArray array = new JsonArray();
            array.add(input.getValue());
            input.setValue(array);
        } else {
            JsonArray jsonArray = schema.getAsJsonArray(ITEM_KEY);
            JsonArray arr = new JsonArray();
            if (input.getValue().isJsonArray()) {
                arr = (JsonArray) input.getValue();
            } else if (input.getValue().isJsonNull()) {
                return;
            }
            for (int j = 0; j < arr.size(); j++) {
                String type = ((JsonObject) (jsonArray.get(j))).get(TYPE_KEY).toString().replaceAll
                        (REGEX, "");
                if (BOOLEAN_KEYS.contains(type) || NOMINAL_KEYS.contains(type) || NUMERIC_KEYS.contains(type)) {
                    String tempString = arr.get(j).toString().replaceAll(REGEX, "");
                    JsonPrimitive primitive = replacePrimitive(tempString, type);
                    arr.set(j, primitive);
                } else if (OBJECT_KEYS.contains(type)) {
                    JsonObject tempObj = (JsonObject) schema.getAsJsonArray(ITEM_KEY).get(j);
                    JsonObject tempele = (JsonObject) arr.get(j);
                    this.parseObject(tempele, tempObj);
                } else if (ARRAY_KEYS.contains(type)) {
                    JsonObject tempObj = (JsonObject) schema.getAsJsonArray(ITEM_KEY).get(j);
                    JsonObject sample = new JsonObject();
                    sample.add("test", arr.get(j));
                    Set<Map.Entry<String, JsonElement>> entries = sample.entrySet();
                    for (Map.Entry<String, JsonElement> entry : entries) {
                        parseArray(entry, tempObj);
                    }
                }
            }
        }
    }

    private void parseObject(JsonObject inputObject, JsonObject schema) throws ParserException {
        JsonObject schemaObject = schema.getAsJsonObject("properties");
        Set<Map.Entry<String, JsonElement>> entries = inputObject.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            JsonObject schemaObj = schemaObject.getAsJsonObject(entry.getKey());
            String type = schemaObj.get(TYPE_KEY).toString().replaceAll(REGEX, "");
            if (BOOLEAN_KEYS.contains(type) || NOMINAL_KEYS.contains(type) || NUMERIC_KEYS.contains(type)) {
                replacePrimitive(entry, schemaObj);
            } else if (ARRAY_KEYS.contains(type)) {
                parseArray(entry, schemaObj);
            } else if (OBJECT_KEYS.contains(type)) {
                parseObject((JsonObject) entry.getValue(), schemaObj);
            }
        }
    }

    /**
     * Parsing inputJson according to the inputSchema.
     *
     * @param inputJson JSON needed to be parsed.
     * @param inputSchema JSON schema.
     * @return Parsed JSON as a String.
     */
    public String parse(String inputJson, String inputSchema) throws ParserException {
        JsonParser parser = new JsonParser();
        JsonElement schema = parser.parse(inputSchema);
        JsonObject schemaObject = (JsonObject) schema;

        JsonElement input = parser.parse(inputJson);
        JsonObject inputObject = (JsonObject) input;

        schemaObject = (JsonObject) schemaObject.get("properties");
        Set<Map.Entry<String, JsonElement>> entryInput = inputObject.entrySet();

        for (Map.Entry<String, JsonElement> temp : entryInput) {
            JsonObject tempSchema = (JsonObject) schemaObject.get(temp.getKey());
            String type = tempSchema.get(TYPE_KEY).getAsString().replaceAll(REGEX, "");
            if (NOMINAL_KEYS.contains(type) || BOOLEAN_KEYS.contains(type) || NUMERIC_KEYS.contains(type)) {
                this.replacePrimitive(temp, tempSchema);
            } else if (ARRAY_KEYS.contains(type)) {
                this.parseArray(temp, tempSchema);
            } else if (OBJECT_KEYS.contains(type)) {
                this.parseObject((JsonObject) temp.getValue(), tempSchema);
            }
        }
        return inputObject.toString();
    }
}