package org.gedcom4j.factory;
import java.util.Arrays;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.FamilySpouse;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualReference;

/**
 * Class to build a {@link Family} object
 * 
 * @author frizbog
 */
public class FamilyFactory {
  /**
     * Create a family, add people to it, and put it in the gedcom
     * 
     * @param g
     *            the gedcom
     * @param father
     *            the father - optional, but if supplied, must already exist in the gedcom (by xref)
     * @param mother
     *            the mother - optional, but if supplied, must already exist in the gedcom (by xref)
     * @param children
     *            the children - optional, but if supplied, must already exist in the gedcom (by xref)
     * @return the family created and added to the gedcom
     */
  public Family create(Gedcom g, Individual father, Individual mother, Individual... children) {
    if (father != null && !g.getIndividuals().containsKey(father.getXref())) {
      throw new IllegalArgumentException("Father could not be found by xref in supplied gedcom object: " + father.getXref());
    }
    if (mother != null && !g.getIndividuals().containsKey(mother.getXref())) {
      throw new IllegalArgumentException("Mother could not be found by xref in supplied gedcom object: " + mother.getXref());
    }
    if (children != null) {
      for (Individual kid : children) {
        if (!g.getIndividuals().containsKey(kid.getXref())) {
          throw new IllegalArgumentException("Child could not be found by xref in supplied gedcom object: " + kid.getXref());
        }
      }
    }
    Family result = new Family();
    for (int xref = g.getFamilies().size(); !g.getFamilies().containsKey("@F" + xref + "@") && result.getXref() == null; xref++) {
      result.setXref("@F" + xref + "@");
      g.getFamilies().put(result.getXref(), result);
    }
    result.setHusband(new IndividualReference(father));
    result.setWife(new IndividualReference(mother));

<<<<<<< /usr/src/app/output/frizbog/gedcom4j/dd044dc5d9b0969c4e6b6be853d35c02c10df487/src/main/java/org/gedcom4j/factory/FamilyFactory.java/left.java
    if (children != null) {
      for (Individual child : children) {
        result.getChildren(true).add(new IndividualReference(child));
      }
    }
=======
    if (children != null && children.length > 0) {
      result.getChildren(true).addAll(Arrays.asList(children));
    }
>>>>>>> /usr/src/app/output/frizbog/gedcom4j/dd044dc5d9b0969c4e6b6be853d35c02c10df487/src/main/java/org/gedcom4j/factory/FamilyFactory.java/right.java

    if (father != null) {
      FamilySpouse fams = new FamilySpouse();
      fams.setFamily(result);
      father.getFamiliesWhereSpouse(true).add(fams);
    }
    if (mother != null) {
      FamilySpouse fams = new FamilySpouse();
      fams.setFamily(result);
      mother.getFamiliesWhereSpouse(true).add(fams);
    }
    if (children != null) {
      for (Individual kid : children) {
        FamilyChild famc = new FamilyChild();
        famc.setFamily(result);
        kid.getFamiliesWhereChild(true).add(famc);
      }
    }
    return result;
  }
}