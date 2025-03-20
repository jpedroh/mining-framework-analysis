package at.kc.tugraz.ss.datatypes.datatypes;
import at.kc.tugraz.socialserver.utils.SSObjU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.ss.serv.jsonld.datatypes.api.SSJSONLDPropI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SSEntityA implements SSJSONLDPropI {
  protected String val = null;

  @Override public String toString() {
    return val;
  }

  protected SSEntityA(final String value) {
    this.val = value;
  }

  protected SSEntityA(final SSEntityA entity) {
    if (SSStrU.isNotEmpty(SSStrU.toString(entity))) {
      this.val = entity.toString();
    }
  }

  protected SSEntityA(int value) {
    if (SSStrU.isNotEmpty(SSStrU.toString(value))) {
      this.val = SSStrU.toString(value);
    }
  }

  public static Boolean contains(final List<? extends SSEntityA> entities, final SSEntityA entityToContain) {
    for (SSEntityA entity : entities) {
      if (isSame(entity, entityToContain)) {
        return true;
      }
    }
    return false;
  }

  public static Boolean containsNot(List<? extends SSEntityA> entities, SSEntityA entityToContain) {
    return !contains(entities, entityToContain);
  }

  public static void remove(List<? extends SSEntityA> entities, SSEntityA entityToRemove) {
    for (SSEntityA entity : entities) {
      if (isSame(entity, entityToRemove)) {
        entities.remove(entity);
        remove(entities, entityToRemove);
        return;
      }
    }
  }

  public static SSEntityA[] toArray(Collection<? extends SSEntityA> entities) {
    return (SSEntityA[]) entities.toArray(new SSEntityA[entities.size()]);
  }

  public static List<String> toDistinctStringArray(final List<? extends SSEntityA> entities) {
    final List<String> result = new ArrayList<String>();
    for (SSEntityA entity : entities) {
      if (entity != null && !result.contains(entity.toString())) {
        result.add(entity.toString());
      }
    }
    return result;
  }

  public static Boolean isSame(final SSEntityA entity1, final SSEntityA entity2) {
    if (SSObjU.isNull(entity1, entity2)) {
      return false;
    }
    return entity1.toString().equals(entity2.toString());
  }
}