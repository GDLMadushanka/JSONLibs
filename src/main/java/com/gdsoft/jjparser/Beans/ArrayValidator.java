package com.gdsoft.jjparser.Beans;

import com.gdsoft.jjparser.ParserConstants;
import com.gdsoft.jjparser.ParserException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;

public class ArrayValidator {

    private static final String MIN_ITEMS = "minItems";
    private static final String MAX_ITEMS = "maxItems";
    private static final String ITEMS = "items";
    private static final String UNIQUE_ITEMS = "uniqueItems";

    private static int minItems;
    private static int maxItems;
    private static String arrayItems;
    private static boolean uniqueItems;
    private static int currentCount;

    public static void validateArray(Map.Entry<String, JsonElement> input, JsonObject
            schema) throws ParserException {
        minItems = -1;
        maxItems = -1;
        currentCount = 0;
        if (schema.has(UNIQUE_ITEMS)) {
            String uniqueItemsString = schema.get(UNIQUE_ITEMS).getAsString().replaceAll(ParserConstants.REGEX, "");
            if (!uniqueItemsString.isEmpty()) {
                uniqueItems = DataTypeConverter.convertToBoolean(uniqueItemsString);
            }
        }
        if (schema.has(MIN_ITEMS)) {
            String minItemsString = schema.get(MIN_ITEMS).getAsString().replaceAll(ParserConstants.REGEX, "");
            if (!minItemsString.isEmpty()) {
                minItems = DataTypeConverter.convertToInt(minItemsString);
                if (minItems < 0) {
                    throw new ParserException("Invalid minItems constraint in the schema");
                }
            }
        }
        if (schema.has(MAX_ITEMS)) {
            String maxItemsString = schema.get(MAX_ITEMS).getAsString().replaceAll(ParserConstants.REGEX, "");
            if (!maxItemsString.isEmpty()) {
                maxItems = DataTypeConverter.convertToInt(maxItemsString);
                if (maxItems < 0) {
                    throw new ParserException("Invalid maxItems constraint in the schema");
                }
            }
        }
        if (input.getValue().isJsonPrimitive() || input.getValue().isJsonNull()) {
            if (minItems != -1 && minItems > 1) {
                throw new ParserException("Array violated the minimum no of items constraint");
            }
        } else {
           // JsonArray jsonArray = schema.getAsJsonArray(ParserConstants.ITEM_KEY);
            JsonArray arr;
            if (input.getValue().isJsonArray()) {
                arr = (JsonArray) input.getValue();
                int arrSize = arr.size();
                if (minItems != -1 && minItems > arrSize) {
                    throw new ParserException("Array violated the minimum no of items constraint");
                } else if (maxItems != -1 && maxItems < arrSize) {
                    throw new ParserException("Array violated the maximum no of items constraint");
                }
                if(uniqueItems) {
                    JsonArray tempArray = new JsonArray();
                    tempArray.add(arr.get(0));
                    if(arrSize>1) {
                        for(int i=1;i<arrSize;i++) {
                            if(tempArray.contains(arr.get(i))) {
                                throw new ParserException("Array has duplicate elements");
                            } tempArray.add(arr.get(i));
                        }
                    }
                }
            }
        }
    }
}
