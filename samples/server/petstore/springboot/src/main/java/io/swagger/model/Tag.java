package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.swagger.annotations.*;

import java.util.Objects;


@ApiModel(description = "")
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/5a7e0fd1816abb4f59a185685d5c4d53eac1efb3/samples/server/petstore/springboot/src/main/java/io/swagger/model/Tag.java/left.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringBootServerCodegen", date = "2016-05-05T15:10:34.669+08:00")
||||||| /usr/src/app/output/swagger-api/swagger-codegen/5a7e0fd1816abb4f59a185685d5c4d53eac1efb3/samples/server/petstore/springboot/src/main/java/io/swagger/model/Tag.java/base.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringBootServerCodegen", date = "2016-05-04T16:34:30.253+02:00")
=======
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringBootServerCodegen", date = "2016-05-05T15:30:42.322+08:00")
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/5a7e0fd1816abb4f59a185685d5c4d53eac1efb3/samples/server/petstore/springboot/src/main/java/io/swagger/model/Tag.java/right.java
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
