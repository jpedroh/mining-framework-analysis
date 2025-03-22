package de.uni_koblenz.jgralab.schema.codegenerator;

/**
 * This class keeps the configurations of the code generator and is passed to
 * all instances. It keeps and manages the following configuration entries:
 * 
 * - <code>standardSupport</code> toggles, if the classes for standard support
 * should be created, enabled by default.<br>
 * <br>
 * 
 * - <code>transactionSupport</code> toggles, if the classes for transaction
 * support should be created, enabled by default.<br>
 * <br>
 * 
 * - <code>typespecificMethodsSupport</code> toggles, if the typespecific
 * methods such as "getNextXYVertex" should be created, enabled by default.<br>
 * <br>
 * 
 * - <code>methodsForSubclasseSupport</code> toggles, if the methods with an
 * additional subtype-flag like "getNextXYVertex(boolean withSubclasses)" should
 * be created. Needs typeSpecifigMethodsSupport to be enabled. Disabled by
 * default.<br>
 * <br>
 * 
 */
public class CodeGeneratorConfiguration {
  public static final CodeGeneratorConfiguration MINIMAL = new CodeGeneratorConfiguration().withoutTypeSpecificMethodSupport();

  public static final CodeGeneratorConfiguration NORMAL = new CodeGeneratorConfiguration();

  public static final CodeGeneratorConfiguration WITH_DISKV2_SUPPORT = new CodeGeneratorConfiguration().withDiskV2Support();

  /**
	 * toggles, if the type-specific methods such as "getNextXYVertex" should be
	 * created
	 */
  private boolean typespecificMethodSupport = true;

  private boolean withDiskV2 = false;

  /**
	 * This constructor creates a default configuration:<br>
	 * <br>
	 * this.standardSupport = true <br>
	 * this.transactionSupport = false <br>
	 * this.typespecificMethodSupport = true <br>
	 * this.methodsForSubclassesSupport = false <br>
	 */
  public CodeGeneratorConfiguration() {
    typespecificMethodSupport = true;
  }

  public CodeGeneratorConfiguration withDiskV2Support() {
    this.withDiskV2 = true;
    return this;
  }

  public CodeGeneratorConfiguration withTypeSpecificMethodSupport() {
    typespecificMethodSupport = true;
    return this;
  }

  public CodeGeneratorConfiguration withoutTypeSpecificMethodSupport() {
    typespecificMethodSupport = false;
    return this;
  }

  /**
	 * This is a copy constructor.
	 * 
	 * @param other
	 *            A valid instance of {@link CodeGeneratorConfiguration} to copy
	 *            values from.
	 */
  public CodeGeneratorConfiguration(CodeGeneratorConfiguration other) {
    this.typespecificMethodSupport = other.typespecificMethodSupport;
  }

  public void setTypeSpecificMethodsSupport(boolean typespecificMethodSupport) {
    this.typespecificMethodSupport = typespecificMethodSupport;
  }

  public boolean hasTypeSpecificMethodsSupport() {
    return typespecificMethodSupport;
  }

  public boolean hasDiskV2Support() {
    return withDiskV2;
  }
}