package com.GDSoft.JSONLibs;

import com.google.gson.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Parsing a JSON according to the given schema
 */
public class SchemaParser {

    private boolean replacePrimitive(Map.Entry<String, JsonElement> input, JsonObject schemaObject) {
        String type = schemaObject.get("type").getAsString().replaceAll("^\"|\"$", "");
        JsonPrimitive primitive = null;
        String tempString = input.getValue().toString().replaceAll("^\"|\"$", "");
        try {
            switch (type) {
                case "integer":
                case "number": {
                    primitive = new JsonPrimitive(Float.valueOf(tempString));
                    input.setValue(primitive);
                    return true;
                }
                case "boolean": {
                    primitive = new JsonPrimitive(Boolean.valueOf(tempString));
                    input.setValue(primitive);
                    return true;
                }
                case "string": {
                    primitive = new JsonPrimitive(tempString);
                    input.setValue(primitive);
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    private boolean parseArray(Map.Entry<String, JsonElement> input, JsonObject schema) {
        //Convert single element to an array
        if (input.getValue().isJsonPrimitive()) {
            JsonArray array = new JsonArray();
            array.add(input.getValue());
            input.setValue(array);
        } else {
            JsonArray jsonArray = schema.getAsJsonArray("items");
            JsonArray arr = (JsonArray) input.getValue();
            for (int j = 0; j < jsonArray.size(); j++) {
                String type = ((JsonObject) (jsonArray.get(j))).get("type").toString().replaceAll
                        ("^\"|\"$", "");
                if (type.equals("number") || type.equals("boolean") || type.equals("string") || type.equals("integer")) {
                    String tempString = arr.get(j).toString().replaceAll("^\"|\"$", "");
                    JsonPrimitive primitive = null;
                    if (type.equals("number") || type.equals("integer")) {
                        primitive = new JsonPrimitive(Float.parseFloat(tempString));
                    } else if (type.equals("boolean")) {
                        primitive = new JsonPrimitive(Boolean.parseBoolean(tempString));
                    } else primitive = new JsonPrimitive(tempString);
                    arr.set(j, primitive);
                } else if (type.equals("object")) {
                    JsonObject tempObj = (JsonObject) schema.getAsJsonArray("items").get(j);
                    JsonObject tempele = (JsonObject) arr.get(j);
                    Set<Map.Entry<String, JsonElement>> entryInput = tempele.entrySet();
                    this.parseObject(tempele,tempObj);
                }
            }
            return true;
        } return true;
    }

    private boolean parseObject(JsonObject inputObject, JsonObject schema) {
        JsonObject schemaObject = schema.getAsJsonObject("properties");
        Set<Map.Entry<String, JsonElement>> entries = inputObject.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            JsonObject schemaObj = schemaObject.getAsJsonObject(entry.getKey());
            String type = schemaObj.get("type").toString().replaceAll("^\"|\"$", "");
            if (type.equals("number") || type.equals("boolean") || type.equals("string") || type.equals("integer")) {
                replacePrimitive(entry, schemaObj);
            } else if (type.equals("array")) {
                parseArray(entry, schemaObj);
            }
        }
        return true;
    }

    private boolean parseArrayObject(JsonObject inputObject, JsonObject schemaObject) {
        Set<Map.Entry<String, JsonElement>> entries = inputObject.entrySet();
        String type = schemaObject.get("type").toString().replaceAll("^\"|\"$", "");
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (type.equals("number") || type.equals("boolean") || type.equals("string")) {
                replacePrimitive(entry, schemaObject);
            } else if (type.equals("array")) {
                parseArray(entry, schemaObject);
            }
        }
        return true;
    }

    /**
     * Parsing inputJson according to the inputSchema.
     *
     * @param inputJson   JSON needed to be parsed.
     * @param inputSchema JSON schema.
     * @return Parsed JSON as a String.
     */
    public String parse(String inputJson, String inputSchema) {
        JsonParser parser = new JsonParser();
        JsonElement schema = parser.parse(inputSchema);
        JsonObject schemaObject = (JsonObject) schema;

        JsonElement input = parser.parse(inputJson);
        JsonObject inputObject = (JsonObject) input;

        schemaObject = (JsonObject) schemaObject.get("properties");
        Set<Map.Entry<String, JsonElement>> entryInput = inputObject.entrySet();
        Iterator<Map.Entry<String, JsonElement>> inputIterator = entryInput.iterator();
        while (inputIterator.hasNext()) {
            Map.Entry<String, JsonElement> temp = inputIterator.next();
            JsonObject tempSchema = (JsonObject) schemaObject.get(temp.getKey());
            String type = tempSchema.get("type").getAsString().replaceAll("^\"|\"$", "");
            if (type.equals("string") || type.equals("number") || type.equals("boolean") || type.equals("integer")) {
                this.replacePrimitive(temp, tempSchema);
            } else if (type.equals("array")) {
                this.parseArray(temp, tempSchema);
            } else if (type.equals("object")) {
                this.parseObject((JsonObject)temp.getValue(), tempSchema);
            }
        }
        return inputObject.toString();
    }
}
