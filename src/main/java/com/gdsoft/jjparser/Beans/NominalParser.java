package com.gdsoft.jjparser.Beans;


import com.gdsoft.jjparser.ParserConstants;
import com.gdsoft.jjparser.ParserException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class NominalParser {

    private static int minLength;
    private static int maxLength;
    private static String pattern;

    public static final String MIN_LENGTH = "minLength";
    public static final String MAX_LENGTH = "maxLength";
    public static final String STR_PATTERN = "pattern";

    public static JsonPrimitive parseNominal(JsonObject inputObject, String value) throws ParserException {
        if (inputObject.has(MAX_LENGTH)) {
            String maxLengthString = inputObject.get(MAX_LENGTH).getAsString().replaceAll(ParserConstants.REGEX, "");
            if (!maxLengthString.isEmpty()) {
                maxLength = DataTypeConverter.convertToInt(maxLengthString);
                if (value.length() > maxLength) {
                    throw new ParserException("String \"" + value + "\" violated the max length constraint");
                }
            }
        }
        if (inputObject.has(MIN_LENGTH)) {
            String minLengthString = inputObject.get(MIN_LENGTH).getAsString().replaceAll(ParserConstants.REGEX, "");
            if (!minLengthString.isEmpty()) {
                minLength = DataTypeConverter.convertToInt(minLengthString);
                if (value.length() < minLength) {
                    throw new ParserException("String \"" + value + "\" violated the min length constraint");
                }
            }
        }
        return new JsonPrimitive(value);
    }
}
