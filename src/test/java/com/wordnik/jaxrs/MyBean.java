package com.wordnik.jaxrs;
import javax.ws.rs.QueryParam;
import io.swagger.annotations.ApiParam;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import java.util.List;

public class MyBean extends MyParentBean {
  @ApiParam(value = "ID of pet that needs to be updated", required = true) @PathParam(value = "petId") private String petId;

  @ApiParam(value = "Updated name of the pet", required = false, defaultValue = "defaultValue") @FormParam(value = "name") private String name;

  @ApiParam(value = "Updated status of the pet", required = false, allowableValues = "value1, value2") @FormParam(value = "status") private String status;

  @HeaderParam(value = "myHeader") private String myHeader;

  @HeaderParam(value = "intValue") private int intValue;

  @ApiParam(value = "hidden", hidden = true) @QueryParam(value = "hiddenValue") private String hiddenValue;

  public String getMyheader() {
    return myHeader;
  }

  public void setmyHeader(String myHeader) {
    this.myHeader = myHeader;
  }

  public String getPetId() {
    return petId;
  }

  public void setPetId(String petId) {
    this.petId = petId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(int intValue) {
    this.intValue = intValue;
  }

  @QueryParam(value = "listValue") private List<String> listValue;

  @ApiParam(value = "testIntegerAllowableValues", defaultValue = "25", allowableValues = "25, 50, 100") @QueryParam(value = "testIntegerAllowableValues") @DefaultValue(value = "25") public Integer testIntegerAllowableValues;

  public List<String> getListValue() {
    return listValue;
  }

  public void setListValue(List<String> listValue) {
    this.listValue = listValue;
  }
}