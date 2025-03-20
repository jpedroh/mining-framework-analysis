package org.cts;
import java.util.ArrayList;
import java.util.List;

/**
 * Identifier used to identify objects such as Datums, Ellipsoids or
 * CoordinateReferenceSystems.<p>
 * Identifier encapsulates all identification info of {@link Identifiable}
 * objects in a special instance to make object creation clearer.<p>
 * Identifier also offers new unique ids for every object created in the LOCAL
 * namespace.
 * @see Identifiable
 * @see IdentifiableComponent
 *
 * @author Michaël Michaud
 */
public class Identifier implements Identifiable {
  private static int localId = 0;

  /**
    * Return an identifier which is unique for this program session.
    * This identifier is usually associated with the LOCAL namespace
    */
  public static int getNewId() {
    return localId++;
  }

  private String authorityName;

  private String authorityKey;

  private String name;

  private String shortName;

  private String remarks;

  private List<Identifiable> aliases;

  /**
    * Creates a complete identifier.
    * @param authorityName namespace of the identifier ie EPSG, IGNF
    * @param authorityKey unique key in the namespace
    * @param name name or description
    * @param shortName short name used for user interfaces
    * @param remarks
    * @param aliases synonyms of this Identifiable
    */
  public Identifier(String authorityName, String authorityKey, String name, String shortName, String remarks, List<Identifiable> aliases) {
    this.authorityName = authorityName;
    this.authorityKey = authorityKey;
    this.name = name;
    this.shortName = shortName;
    this.remarks = remarks;
    this.aliases = aliases;
  }

  /**
    * Creates a local identifier.
    * @param clazz the class of the identified object
    */
  public Identifier(Class clazz) {
    this(Identifiable.LOCAL + "_" + clazz.getSimpleName(), "" + getNewId(), Identifiable.UNKNOWN, null, null, null);
  }

  /**
    * Create a local identifier.
    * @param clazz the class of the identified object
    * @param name the name of the identified object
    */
  public Identifier(Class clazz, String name) {
    this(Identifiable.LOCAL + "_" + clazz.getSimpleName(), "" + getNewId(), name, null, null, null);
  }

  /**
    * Creates a complete identifier.
    * @param authorityName ie EPSG, IGNF
    * @param authorityKey ie 4326, LAMB
    * @param name
    */
  public Identifier(String authorityName, String authorityKey, String name) {
    this(authorityName, authorityKey, name, null, null, null);
  }

  /**
    * Creates a complete identifier.
    * @param authorityName ie EPSG, IGNF
    * @param authorityKey ie 4326, LAMB
    * @param name
	* @param shortName a short name to use in user interfaces
    */
  public Identifier(String authorityName, String authorityKey, String name, String shortName) {
    this(authorityName, authorityKey, name, shortName, null, null);
  }

  /**
    * Return the authority name of this identifier (ex. EPSG, IGN-F)
    * The namespace may represent a database name, a URL, a URN...
    */
  @Override public String getAuthorityName() {
    return authorityName;
  }

  /**
    * Returns the key of this identifier (id must be unique inside the authority name).
    */
  @Override public String getAuthorityKey() {
    return authorityKey;
  }

  /**
    * Returns a code formed with a namespace, ':' and the id value of identifier
    * (ex. EPSG:27572).
    * @return a String of the form namespace:identifier
    */
  @Override public String getCode() {
    return authorityKey;
  }

  /**
    * Returns a string used to identify clearly the object.
    */
  @Override public String getName() {
    return name;
  }

  /**
    * Returns a short string used to identify unambiguously the object.
    * A short name should have less than 16 characters whenever possible, and
    * should never exceed 48 characters.
    */
  @Override public String getShortName() {
    return shortName == null ? name : shortName;
  }

  /**
    * Change the short string used to identify unambiguously the object.
    * A short name should have less than 16 characters whenever possible, and
    * should never exceed 48 characters.
    */
  @Override public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  /**
    * Returns remarks.
    */
  @Override public String getRemarks() {
    return remarks;
  }

  /**
    * Change the remarks.
    */
  @Override public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  /**
    * Add remarks.
    */
  @Override public void addRemark(String new_remark) {
    this.remarks = this.remarks + "\n" + new_remark;
  }

  /**
    * Get aliases
    */
  @Override public List<Identifiable> getAliases() {
    return aliases == null ? new ArrayList<Identifiable>() : aliases;
  }

  /**
    * Add an alias
    * @param alias an alias for this object
    */
  @Override public boolean addAlias(Identifiable alias) {
    if (aliases == null) {
      aliases = new ArrayList<Identifiable>();
    }
    return aliases.add(alias);
  }

  /**
    * Returns true if object is equals to this.
    * Test equality between codes (namespace + id), then between aliases.
    * @param object
    */
  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof Identifier) {
      Identifier other = (Identifier) object;
      if (getCode().equals(other.getCode())) {
        return true;
      }
      for (Identifiable id1 : getAliases()) {
        for (Identifiable id2 : other.getAliases()) {
          if (id1.getCode().equals(id2.getCode())) {
            return true;
          }
        }
      }
      return false;
    } else {
      return false;
    }
  }

  @Override public int hashCode() {
    int hash = 7;
    hash = 11 * hash + (this.authorityKey != null ? this.authorityKey.hashCode() : 0);
    hash = 11 * hash + (this.aliases != null ? this.aliases.hashCode() : 0);
    return hash;
  }

  /**
    * @return a String representation of this identifier.
    */
  @Override public String toString() {
    return "[" + authorityName + ":" + authorityKey + "] " + name;
  }
}