package com.GDSoft.JSONLibs;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SampleCodes {
    public static void main(String[] args) throws IOException {
        //Reading input.json and validatingInput.json from files
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        //InputStream inputStream = classloader.getResourceAsStream("input.json");
        //String inputJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        InputStream inputStream = classloader.getResourceAsStream("schema.json");
        String inputJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        inputStream = classloader.getResourceAsStream("validatingInput.json");
        String validatingInput = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        //creating instances
        SchemaBuilder schemaBuilder = new SchemaBuilder();
        SchemaParser parser = new SchemaParser();

        //creating the schema from input JSON
        //String schema = schemaBuilder.createSchema(inputJson, FileType.JSON);
        //System.out.println("Schema : " + schema);

        //validatingInput.json
        System.out.println("Before parsing");
        System.out.println(validatingInput);

        //Parsing validatingInput.json using above generated schema
        String parsedJson = parser.parse(validatingInput, inputJson);

        System.out.println("After parsing");
        System.out.println(parsedJson);
    }
}
