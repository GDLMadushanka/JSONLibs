package com.gdsoft.jjparser.Beans;

import com.gdsoft.jjparser.ParserConstants;
import com.gdsoft.jjparser.ParserException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import org.apache.commons.lang3.math.NumberUtils;

public class NumericParser {

    private static int minimum;
    private static int maximum;
    private static int maxLength;
    private static String pattern;

    private static final String INTEGER_STRING = "integer";
    private static final String NUMBER_STRING = "number";

    public static final String MINIMUM_VALUE = "minimum";
    public static final String MAXIMUM_VALUE = "maximum";
    public static final String EXCLUSIVE_MINIMUM = "exclusiveMinimum";
    public static final String EXCLUSIVE_MAXIMUM = "exclusiveMaximum";
    public static final String MULTIPLE_OF = "multipleOf";

    public static JsonPrimitive parseNumeric(JsonObject inputObject, String value) throws ParserException {
        if (NumberUtils.isCreatable(value)) {
            String type = inputObject.get(ParserConstants.TYPE_KEY).getAsString().replaceAll(ParserConstants.REGEX, "");
            Double doubleValue = Double.parseDouble(value);
            if (inputObject.has(MINIMUM_VALUE)) {
                String minimumString = inputObject.get(MINIMUM_VALUE).getAsString().replaceAll(ParserConstants.REGEX,
                        "");
                if (!minimumString.isEmpty()) {
                    minimum = Integer.valueOf(minimumString);
                    if (doubleValue < minimum) {
                        throw new ParserException("Number " + value + " is less than the minimum allowed value");
                    }
                }
            }
            if (inputObject.has(MAXIMUM_VALUE)) {
                String maximumString = inputObject.get(MAXIMUM_VALUE).getAsString().replaceAll(ParserConstants.REGEX,
                        "");
                if (!maximumString.isEmpty()) {
                    maximum = Integer.valueOf(maximumString);
                    if (doubleValue > maximum) {
                        throw new ParserException("Number " + value + " is greater than the maximum allowed value");
                    }
                }
            }
            if (type.equals(INTEGER_STRING)) {
                return new JsonPrimitive(Integer.parseInt(value));
            } else if (type.equals(NUMBER_STRING)) {
                return new JsonPrimitive(Double.parseDouble(value));
            }
        }
        throw new ParserException("\"" + value + "\"" + " is not a number");
    }
}
