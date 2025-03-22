package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.swagger.annotations.*;

import java.util.Objects;


@ApiModel(description = "")
<<<<<<< /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/model/Tag.java/left.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringMVCServerCodegen", date = "2016-04-15T00:38:43.027+08:00")
||||||| /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/model/Tag.java/base.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringMVCServerCodegen", date = "2016-02-26T13:58:54.483Z")
=======
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringMVCServerCodegen", date = "2016-04-14T23:14:04.836+08:00")
>>>>>>> /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/model/Tag.java/right.java
public class Tag  {
  
  private Long id = null;
  private String name = null;

  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("id")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Tag tag = (Tag) o;
    return Objects.equals(id, tag.id) &&
        Objects.equals(name, tag.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Tag {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
