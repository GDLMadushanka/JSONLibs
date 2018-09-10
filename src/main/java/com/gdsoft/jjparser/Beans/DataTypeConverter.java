package com.gdsoft.jjparser.Beans;

import com.gdsoft.jjparser.ParserException;
import org.apache.commons.lang.BooleanUtils;

public class DataTypeConverter {

    public static Boolean convertToBoolean(String value) {
        boolean result = BooleanUtils.toBoolean(value);
        return result;
    }

    public int convertToInt(String value) throws ParserException {
        if (!value.isEmpty()) {
            try {
                int i = Integer.parseInt(value.trim());
                return i;
            } catch (NumberFormatException nfe) {
                System.out.println("NumberFormatException: " + nfe.getMessage());
            }
        }
        throw new ParserException("Empty value cannot convert to int");
    }

    public double converToDouble(String value) throws ParserException {
        if (!value.isEmpty()) {
            try {
                double i = Double.parseDouble(value.trim());
                return i;
            } catch (NumberFormatException nfe) {
                System.out.println("NumberFormatException: " + nfe.getMessage());
            }
        }
        throw new ParserException("Empty value cannot convert to double");
    }

}
