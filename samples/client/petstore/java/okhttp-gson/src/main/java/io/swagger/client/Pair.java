package io.swagger.client;


<<<<<<< Unknown file: This is a bug in JDime.
=======
@javax.annotation.Generated(value = { "class io.swagger.codegen.languages.JavaClientCodegen" }, date = "2016-06-09T08:56:08.812+02:00")
>>>>>>> /usr/src/app/output/openapitools/openapi-generator/bc42d941ec8c184829816c05236cf58f5c59a3ff/samples/client/petstore/java/okhttp-gson/src/main/java/io/swagger/client/Pair.java/right.java
 public class Pair {
  private String name = "";

  private String value = "";

  public Pair(String name, String value) {
    setName(name);
    setValue(value);
  }

  private void setName(String name) {
    if (!isValidString(name)) {
      return;
    }
    this.name = name;
  }

  private void setValue(String value) {
    if (!isValidString(value)) {
      return;
    }
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public String getValue() {
    return this.value;
  }

  private boolean isValidString(String arg) {
    if (arg == null) {
      return false;
    }
    if (arg.trim().isEmpty()) {
      return false;
    }
    return true;
  }
}