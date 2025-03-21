package com.ning.billing.recurly.model;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "plans") @XmlAccessorType(value = XmlAccessType.FIELD) public class Plans extends RecurlyObjects<Plan> {
  @XmlTransient public static final String PLANS_RESOURCE = "/plans";


<<<<<<< /usr/src/app/output/killbilling/recurly-java-library/08f428502f99854cbe37544d682bdc674b5ebc95/src/main/java/com/ning/billing/recurly/model/Plans.java/left.java
  @XmlElement(name = "plan") private List<Plan> plans;
=======
>>>>>>> Unknown file: This is a bug in JDime.
}