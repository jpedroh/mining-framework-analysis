package net.helpscout.api.model.ref;
import net.helpscout.api.cbo.PersonType;

public abstract class AbstractRef implements PersonRef {
  private int id;

  private String firstName;

  private String lastName;

  private String email;


<<<<<<< /usr/src/app/output/helpscout/helpscout-api-java/397295ddede909edd6af0276694088841a9d5039/src/main/java/net/helpscout/api/model/ref/AbstractRef.java/left.java
  private PersonType type;
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public int getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }


<<<<<<< /usr/src/app/output/helpscout/helpscout-api-java/397295ddede909edd6af0276694088841a9d5039/src/main/java/net/helpscout/api/model/ref/AbstractRef.java/left.java
  public PersonType getType() {
    return type;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.
}