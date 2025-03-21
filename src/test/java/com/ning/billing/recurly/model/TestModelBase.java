package com.ning.billing.recurly.model;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.testng.annotations.BeforeMethod;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.type.TypeFactory;

public abstract class TestModelBase {
  protected XmlMapper xmlMapper;

  @BeforeMethod(alwaysRun = true) public void setUp() throws Exception {
    xmlMapper = RecurlyObject.newXmlMapper();

<<<<<<< Unknown file: This is a bug in JDime.
=======
    final AnnotationIntrospector secondary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
>>>>>>> /usr/src/app/output/killbilling/recurly-java-library/500781acecc44ce57590effaab9a7369c2358035/src/test/java/com/ning/billing/recurly/model/TestModelBase.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    final AnnotationIntrospector pair = new AnnotationIntrospectorPair(primary, secondary);
>>>>>>> /usr/src/app/output/killbilling/recurly-java-library/500781acecc44ce57590effaab9a7369c2358035/src/test/java/com/ning/billing/recurly/model/TestModelBase.java/right.java
  }
}