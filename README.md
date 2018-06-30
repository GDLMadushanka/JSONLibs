# JSONLibs
1. Creating a JSON schema from a given JSON
2. Parse a JSON according to a given schema

**Sample Usage**
```java
//creating instances
SchemaBuilder schemaBuilder = new SchemaBuilder();
SchemaParser parser = new SchemaParser();

//creating the schema from input JSON
String schema = schemaBuilder.createSchema(inputJson, FileType.JSON);
System.out.println("Schema : " + schema);

//validatingInput.json
System.out.println("Before parsing");
System.out.println(validatingInput);

//Parsing validatingInput.json using above generated schema
String parsedJson = parser.parse(validatingInput, schema);

System.out.println("After parsing");
System.out.println(parsedJson);
```
**TODO**

1. Schema generation for array inside array
2. Schema generation for object inside object
