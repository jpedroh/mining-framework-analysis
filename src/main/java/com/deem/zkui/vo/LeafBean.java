package com.deem.zkui.vo;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeafBean implements Comparable<LeafBean> {
  private final static Logger logger = LoggerFactory.getLogger(LeafBean.class);

  private String path;

  private String name;

  private byte[] value;

  private String strValue;

  private String description;

  public LeafBean(String path, String name, byte[] value) {
    super();
    this.path = path;
    this.name = name;
    this.value = value;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte[] getValue() {
    return value;
  }

  public void setValue(byte[] value) {
    this.value = value;
  }

  public String getStrValue() {
    return new String(this.value, StandardCharsets.UTF_8);
  }

  public void setStrValue(String strValue) {
    this.strValue = strValue;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override public int compareTo(LeafBean o) {
    return (this.path + this.name).compareTo((o.path + o.name));
  }
}