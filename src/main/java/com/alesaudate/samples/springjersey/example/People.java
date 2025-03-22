package com.alesaudate.samples.springjersey.example;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.alesaudate.samples.springjersey.entities.EntityCollection;


@XmlRootElement
public class People extends EntityCollection<Person> {

	
	
	@Override
	@XmlElement(name="person")
	public Collection<Person> getEntities() {
		return super.getEntities();
	}
	
	
	/*
	 * Overriden to prevent JAXB's NPE
	 */
	@Override
<<<<<<< /usr/src/app/output/alesaudate/kickstart-springjerseyhibernate/feb36bfc8ee3a9e0d4394ef8e845a97c8c489840/src/main/java/com/alesaudate/samples/springjersey/example/People.java/left.java
	public void setEntities(Collection<Person> entities) {
		// TODO Auto-generated method stub
		super.setEntities(entities);
	}
||||||| /usr/src/app/output/alesaudate/kickstart-springjerseyhibernate/feb36bfc8ee3a9e0d4394ef8e845a97c8c489840/src/main/java/com/alesaudate/samples/springjersey/example/People.java/base.java
	public void setEntities(Collection<Person> entities) 
=======
	public void setEntities(Collection<Person> entities) {
		super.setEntities(entities);
	}
>>>>>>> /usr/src/app/output/alesaudate/kickstart-springjerseyhibernate/feb36bfc8ee3a9e0d4394ef8e845a97c8c489840/src/main/java/com/alesaudate/samples/springjersey/example/People.java/right.java
}
