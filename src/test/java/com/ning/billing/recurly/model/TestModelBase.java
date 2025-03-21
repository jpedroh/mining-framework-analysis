package com.ning.billing.recurly.model;
import org.testng.annotations.BeforeMethod;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public abstract class TestModelBase {
  protected XmlMapper xmlMapper;

  @BeforeMethod(alwaysRun = true) public void setUp() throws Exception {
    xmlMapper = RecurlyObject.newXmlMapper();
  }
}