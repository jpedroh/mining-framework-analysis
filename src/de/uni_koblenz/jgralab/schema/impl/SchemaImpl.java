package de.uni_koblenz.jgralab.schema.impl;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.ProgressFunction;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.codegenerator.CodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.codegenerator.EdgeCodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.EnumCodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.GraphCodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.GraphFactoryGenerator;
import de.uni_koblenz.jgralab.codegenerator.RecordCodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.ReversedEdgeCodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.SchemaCodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.VertexCodeGenerator;
import de.uni_koblenz.jgralab.impl.ConsoleProgressFunction;
import de.uni_koblenz.jgralab.impl.generic.GenericGraphFactoryImpl;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.schema.CompositeDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.DoubleDomain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IntegerDomain;
import de.uni_koblenz.jgralab.schema.ListDomain;
import de.uni_koblenz.jgralab.schema.LongDomain;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.NamedElement;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.SetDomain;
import de.uni_koblenz.jgralab.schema.StringDomain;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.InvalidNameException;
import de.uni_koblenz.jgralab.schema.exception.SchemaClassAccessException;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.impl.compilation.ClassFileManager;
import de.uni_koblenz.jgralab.schema.impl.compilation.InMemoryJavaSourceFile;
import de.uni_koblenz.jgralab.schema.impl.compilation.SchemaClassManager;

/**
 * @author ist@uni-koblenz.de
 */
public class SchemaImpl implements Schema {
  private SchemaClassManager schemaClassManager = null;

  public SchemaClassManager getSchemaClassManager() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.schemaClassManager
=======
    schemaClassManager
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  static final Class<?>[] GRAPHCLASS_CREATE_SIGNATURE = { ImplementationType.class, String.class, int.class, int.class };

  /**
	 * This is the name of the package into which the implementation classes for
	 * this schema are generated. The impl package is child of the package for
	 * the Schema.
	 */
  public static final String IMPL_PACKAGE_NAME = "impl";

  /**
	 * This is the name of the package into which the implementation classes for
	 * this schema are generated. The impl package is child of the package for
	 * Schema.
	 */
  public static final String IMPLSTDPACKAGENAME = "impl.std";

  public static final String IMPLTRANSPACKAGENAME = "impl.trans";

  public static final String IMPLDATABASEPACKAGENAME = "impl.db";

  static final Class<?>[] VERTEX_CLASS_CREATE_SIGNATURE = { int.class };

  /**
	 * Toggles if the schema allows lowercase enumeration constants
	 */
  private boolean allowLowercaseEnumConstants = true;

  private PackageImpl defaultPackage;


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  protected CodeGeneratorConfiguration config;
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
	 * Maps from qualified name to the {@link Domain}.
	 */
  private Map<String, Domain> domains = new HashMap<String, Domain>();

  private DirectedAcyclicGraph<Domain> domainsDag = new DirectedAcyclicGraph<Domain>();

  private boolean finished = false;

  /**
	 * Holds a reference to the {@link GraphClass} of this schema (not the
	 * default graph class {@link GraphClass})
	 */
  private 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  GraphClass
=======
  GraphClassImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
   graphClass;

  /**
	 * The name of this schema without the package prefix.
	 */
  private String name;

  /**
	 * The package prefix of this schema.
	 */
  private String packagePrefix;

  /**
	 * Maps from simple names to a set of {@link NamedElement}s which have this
	 * simple name. Used for creation of unique names.
	 */
  private Map<String, AttributedElementClass<?, ?>> duplicateSimpleNames = new HashMap<String, AttributedElementClass<?, ?>>();

  /**
	 * Maps from qualified name to the {@link Package} with that qualified name.
	 */
  private Map<String, PackageImpl> packages = new TreeMap<String, Package, PackageImpl>();

  /**
	 * The qualified name of this schema, that is {@link #packagePrefix} DOT
	 * {@link #name}
	 */
  private String qualifiedName;

  /**
	 * A set of all qualified names known to this schema.
	 */
  private Map<String, NamedElement> namedElements = new TreeMap<String, NamedElement>();

  private BooleanDomain booleanDomain;

  private DoubleDomain doubleDomain;

  private IntegerDomain integerDomain;

  private LongDomain longDomain;

  private StringDomain stringDomain;

  private static final Pattern SCHEMA_NAME_PATTERN = Pattern.compile("^\\p{Upper}(\\p{Alnum}|[_])*\\p{Alnum}$");

  private static final Pattern PACKAGE_PREFIX_PATTERN = Pattern.compile("^\\p{Lower}\\w*(\\.\\p{Lower}\\w*)*$");

  /**
	 * Creates a new <code>Schema</code>.
	 *
	 * @param name
	 *            Name of schema.
	 * @param packagePrefix
	 *            Package prefix of schema.
	 */
  public SchemaImpl(String name, String packagePrefix) {
    if (!SCHEMA_NAME_PATTERN.matcher(name).matches()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.throwInvalidSchemaNameException();
=======
      throw new SchemaException("Invalid schema name \'" + name + "\'.\n" + "The name must not be empty.\n" + "The name must start with a capital letter.\n" + "Any following character must be alphanumeric and/or a \'_\' character.\n" + "The name must end with an alphanumeric character.");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    }
    if (!PACKAGE_PREFIX_PATTERN.matcher(packagePrefix).matches()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.throwInvalidPackagePrefixNameException();
=======
      throw new SchemaException("Invalid schema package prefix \'" + packagePrefix + "\'.\n" + "The packagePrefix must not be empty.\n" + "The package prefix must start with a small letter.\n" + "The first character after each \'.\' must be a small letter.\n" + "Following characters may be alphanumeric and/or \'_\' characters.\n" + "The last character before a \'.\' and the end of the line must be an alphanumeric character.");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    }
    this.name = name;
    this.packagePrefix = packagePrefix;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.qualifiedName = packagePrefix + "." + name
=======
    qualifiedName = packagePrefix + "." + name
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.schemaClassManager = SchemaClassManager.instance(this.qualifiedName)
=======
    schemaClassManager = SchemaClassManager.instance(qualifiedName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.defaultPackage = this.createDefaultPackage()
=======
    defaultPackage = PackageImpl.createDefaultPackage(this)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    this.booleanDomain = this.createBooleanDomain();
    this.doubleDomain = this.createDoubleDomain();
    this.integerDomain = this.createIntegerDomain();
    this.longDomain = this.createLongDomain();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.stringDomain = this.createStringDomain();
=======
    createBooleanDomain();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.defaultGraphClass = GraphClassImpl.createDefaultGraphClass(this);
=======
    createDoubleDomain();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.defaultVertexClass = VertexClassImpl.createDefaultVertexClass(this);
=======
    createIntegerDomain();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.defaultEdgeClass = EdgeClassImpl.createDefaultEdgeClass(this);
=======
    createLongDomain();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.config = this.createDefaultConfig();
=======
    createStringDomain();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
  }

  protected Package createDefaultPackage() {
    return PackageImpl.createDefaultPackage(this);
  }

  private void throwInvalidSchemaNameException() {
    throw new InvalidNameException("Invalid schema name \'" + this.name + "\'.\n" + "The name must not be empty.\n" + "The name must start with a capital letter.\n" + "Any following character must be alphanumeric and/or a \'_\' character.\n" + "The name must end with an alphanumeric character.");
  }

  private void throwInvalidPackagePrefixNameException() {
    throw new InvalidNameException("Invalid schema package prefix \'" + this.packagePrefix + "\'.\n" + "The packagePrefix must not be empty.\n" + "The package prefix must start with a small letter.\n" + "The first character after each \'.\' must be a small letter.\n" + "Following characters may be alphanumeric and/or \'_\' characters.\n" + "The last character before a \'.\' and the end of the line must be an alphanumeric character.");
  }

  private CodeGeneratorConfiguration createDefaultConfig() {
    CodeGeneratorConfiguration out = new CodeGeneratorConfiguration();
    if (java.lang.Package.getPackage(this.packagePrefix + ". " + IMPLSTDPACKAGENAME) == null) {
      out.setStandardSupport(false);
    }
    if (java.lang.Package.getPackage(this.packagePrefix + ". " + IMPLTRANSPACKAGENAME) != null) {
      out.setTransactionSupport(true);
    }
    if (java.lang.Package.getPackage(this.packagePrefix + ". " + IMPLDATABASEPACKAGENAME) != null) {
      out.setDatabaseSupport(true);
    }
    return out;
  }

  void addDomain(Domain dom) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    assert !this.domains.containsKey(dom.getQualifiedName()) : "There already is a Domain with the qualified name: " + dom.getQualifiedName() + " in the Schema!";
=======
    if (domains.containsKey(dom.getQualifiedName())) {
      throw new SchemaException("Duplicate Domain \'" + dom.getQualifiedName() + "\'");
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    domains.put(dom.getQualifiedName(), dom);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.domains.put(dom.getQualifiedName(), dom)
=======
    domainsDag.createNode(dom)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  void addPackage(PackageImpl pkg) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    assert !this.packages.containsKey(pkg.getQualifiedName()) : "There already is a Package with the qualified name \'" + pkg.getQualifiedName() + "\' in the Schema!";
=======
    if (packages.containsKey(pkg.getQualifiedName())) {
      throw new SchemaException("Duplicate Package \'" + pkg.getQualifiedName() + "\'");
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    packages.put(pkg.getQualifiedName(), pkg)
=======
    put(pkg.getQualifiedName(), pkg)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  void addNamedElement(NamedElement namedElement) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    assert !this.namedElements.containsKey(namedElement.getQualifiedName()) : "You are trying to add the NamedElement \'" + namedElement.getQualifiedName() + "\' to this Schema, but that does already exist!";
=======
    if (namedElements.containsKey(namedElement.getQualifiedName())) {
      throw new SchemaException("Duplicate NamedElement \'" + namedElement.getQualifiedName() + "\'");
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.namedElements.put(namedElement.getQualifiedName(), namedElement)
=======
    namedElements.put(namedElement.getQualifiedName(), namedElement)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    if (!(namedElement instanceof AttributedElementClass)) {
      return;
    }
    AttributedElementClass<?, ?> aec = (AttributedElementClass<?, ?>) namedElement;
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.duplicateSimpleNames.containsKey(aec.getSimpleName())
=======
    duplicateSimpleNames.containsKey(aec.getSimpleName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      AttributedElementClass<?, ?> other = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.duplicateSimpleNames.get(aec.getSimpleName())
=======
      duplicateSimpleNames.get(aec.getSimpleName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
      if (other != null) {
        ((NamedElementImpl) other).changeUniqueName();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.duplicateSimpleNames.put(aec.getSimpleName(), null)
=======
        duplicateSimpleNames.put(aec.getSimpleName(), null)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        ;
      }
      ((NamedElementImpl) aec).changeUniqueName();
    } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.duplicateSimpleNames.put(aec.getSimpleName(), aec)
=======
      duplicateSimpleNames.put(aec.getSimpleName(), aec)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
    }
  }

  @Override public NamedElement getNamedElement(String qualifiedName) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.namedElements.get(qualifiedName)
=======
    namedElements.get(qualifiedName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public boolean allowsLowercaseEnumConstants() {
    return this.allowLowercaseEnumConstants;
  }

  private Vector<InMemoryJavaSourceFile> createClasses(CodeGeneratorConfiguration config) {
    Vector<InMemoryJavaSourceFile> javaSources = new Vector<InMemoryJavaSourceFile>();
    GraphCodeGenerator graphCodeGenerator = new GraphCodeGenerator(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass
=======
    graphClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix
=======
    packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.name
=======
    name
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , config);
    javaSources.addAll(graphCodeGenerator.createJavaSources());
    for (VertexClass vertexClass : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass.getVertexClasses()
=======
    graphClass.getVertexClasses()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      if (vertexClass.isInternal()) {
        continue;
      }
      VertexCodeGenerator codeGen = new VertexCodeGenerator(vertexClass, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.packagePrefix
=======
      packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      , config);
      javaSources.addAll(codeGen.createJavaSources());
    }
    for (EdgeClass edgeClass : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass.getEdgeClasses()
=======
    graphClass.getEdgeClasses()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      if (edgeClass.isInternal()) {
        continue;
      }
      CodeGenerator codeGen = new EdgeCodeGenerator(edgeClass, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.packagePrefix
=======
      packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      , config);
      javaSources.addAll(codeGen.createJavaSources());
      if (!edgeClass.isAbstract()) {
        codeGen = new ReversedEdgeCodeGenerator(edgeClass, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.packagePrefix
=======
        packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        , config);
        javaSources.addAll(codeGen.createJavaSources());
      }
    }
    for (Domain domain : this.getRecordDomains()) {
      CodeGenerator rcode = new RecordCodeGenerator((RecordDomain) domain, this.packagePrefix, config);
      javaSources.addAll(rcode.createJavaSources());
    }
    for (Domain domain : this.getEnumDomains()) {
      CodeGenerator ecode = new EnumCodeGenerator((EnumDomain) domain, this.packagePrefix);
      javaSources.addAll(ecode.createJavaSources());
    }
    return javaSources;
  }

  @Override public void createJAR(CodeGeneratorConfiguration config, String jarFileName) throws IOException, GraphIOException {
    assertFinished();
    File tmpFile = File.createTempFile("jar-creation", "tmp");
    tmpFile.deleteOnExit();
    File tmpDir = new File(tmpFile.getParent());
    File schemaDir = new File(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    tmpDir + File.separator + this.getName()
=======
    tmpDir + File.separator + getName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    );
    if (!schemaDir.mkdir()) {
      System.err.println("Couldn\'t create " + schemaDir);
      return;
    }
    System.out.println("Committing schema classes to " + schemaDir);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.commit(schemaDir.getAbsolutePath(), config, new ConsoleProgressFunction("Committing"));
=======
    commit(schemaDir.getAbsolutePath(), config, new ConsoleProgressFunction("Committing"));
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.compileClasses(schemaDir);
=======
    compileClasses(schemaDir);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    Process proc = Runtime.getRuntime().exec("jar cf " + jarFileName + " -C " + schemaDir.getAbsolutePath() + " .");
    try {
      proc.waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.deleteRecursively(schemaDir);
=======
    deleteRecursively(schemaDir);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
  }

  private void deleteRecursively(File file) {
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.deleteRecursively(f);
=======
        deleteRecursively(f);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      }
      file.delete();
    } else {
      file.delete();
    }
  }

  private void compileClasses(File schemaDir) throws IOException {
    JavaCompiler c = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager = c.getStandardFileManager(null, null, null);
    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.getJavaFiles(schemaDir)
=======
    getJavaFiles(schemaDir)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    );
    c.getTask(null, fileManager, null, null, null, compilationUnits).call();
    fileManager.close();
  }

  private List<File> getJavaFiles(File schemaDir) {
    LinkedList<File> sources = new LinkedList<File>();
    for (File f : schemaDir.listFiles()) {
      if (f.isDirectory()) {
        sources.addAll(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.getJavaFiles(f)
=======
        getJavaFiles(f)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        );
      } else {
        if (f.getName().endsWith(".java")) {
          sources.add(f);
        } else {
          System.out.println("Skipping " + f + "...");
        }
      }
    }
    return sources;
  }

  @Override public Vector<InMemoryJavaSourceFile> commit(CodeGeneratorConfiguration config) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (!this.finished) {
      throw new SchemaException("Schema must be finish before committing is allowed. " + "Call finish() to finish the schema.");
    }
=======
    assertFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    Vector<InMemoryJavaSourceFile> javaSources = new Vector<InMemoryJavaSourceFile>();
    CodeGenerator schemaCodeGenerator = new SchemaCodeGenerator(this, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix
=======
    packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , config);
    javaSources.addAll(schemaCodeGenerator.createJavaSources());
    CodeGenerator factoryCodeGenerator = new GraphFactoryGenerator(this, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix
=======
    packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , config);
    javaSources.addAll(factoryCodeGenerator.createJavaSources());
    if (this.graphClass.getQualifiedName().equals("Graph")) {
      throw new SchemaException("The defined GraphClass must not be named Graph!");
    }
    javaSources.addAll(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.createClasses(config)
=======
    createClasses(config)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    );
    return javaSources;
  }

  private void createFiles(CodeGeneratorConfiguration config, String pathPrefix, ProgressFunction progressFunction, long schemaElements, long currentCount, long interval) throws GraphIOException {
    GraphCodeGenerator graphCodeGenerator = new GraphCodeGenerator(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass
=======
    graphClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix
=======
    packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.name
=======
    name
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , config);
    graphCodeGenerator.createFiles(pathPrefix);
    for (VertexClass vertexClass : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass.getVertexClasses()
=======
    graphClass.getVertexClasses()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      if (vertexClass.isInternal()) {
        continue;
      }
      VertexCodeGenerator codeGen = new VertexCodeGenerator(vertexClass, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.packagePrefix
=======
      packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      , config);
      codeGen.createFiles(pathPrefix);
      if (progressFunction != null) {
        schemaElements++;
        currentCount++;
        if (currentCount == interval) {
          progressFunction.progress(schemaElements);
          currentCount = 0;
        }
      }
    }
    for (EdgeClass edgeClass : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass.getEdgeClasses()
=======
    graphClass.getEdgeClasses()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      if (edgeClass.isInternal()) {
        continue;
      }
      CodeGenerator codeGen = new EdgeCodeGenerator(edgeClass, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.packagePrefix
=======
      packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      , config);
      codeGen.createFiles(pathPrefix);
      if (!edgeClass.isAbstract()) {
        codeGen = new ReversedEdgeCodeGenerator(edgeClass, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.packagePrefix
=======
        packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        , config);
        codeGen.createFiles(pathPrefix);
      }
      if (progressFunction != null) {
        schemaElements++;
        currentCount++;
        if (currentCount == interval) {
          progressFunction.progress(schemaElements);
          currentCount = 0;
        }
      }
    }
    for (Domain domain : this.getRecordDomains()) {
      CodeGenerator rcode = new RecordCodeGenerator((RecordDomain) domain, this.packagePrefix, config);
      rcode.createFiles(pathPrefix);
      if (progressFunction != null) {
        schemaElements++;
        currentCount++;
        if (currentCount == interval) {
          progressFunction.progress(schemaElements);
          currentCount = 0;
        }
      }
    }
    for (Domain domain : this.getEnumDomains()) {
      CodeGenerator ecode = new EnumCodeGenerator((EnumDomain) domain, this.packagePrefix);
      ecode.createFiles(pathPrefix);
    }
    if (progressFunction != null) {
      schemaElements++;
      currentCount++;
      if (currentCount == interval) {
        progressFunction.progress(schemaElements);
        currentCount = 0;
      }
    }
  }

  @Override public void commit(String pathPrefix, CodeGeneratorConfiguration config) throws GraphIOException {
    assertFinished();
    this.commit(pathPrefix, config, null);
  }

  @Override public void commit(String pathPrefix, CodeGeneratorConfiguration config, ProgressFunction progressFunction) throws GraphIOException {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (!this.finished) {
      throw new SchemaException("Schema must be finish before committing is allowed. " + "Call finish() to finish the schema.");
    }
=======
    assertFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    long schemaElements = 0, currentCount = 0, interval = 1;
    if (progressFunction != null) {
      int elements = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.getNumberOfElements()
=======
      getNumberOfElements()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
      if (config.hasTransactionSupport()) {
        elements *= 2;
      }
      progressFunction.init(elements);
      interval = progressFunction.getUpdateInterval();
    }
    if (!pathPrefix.endsWith(File.separator)) {
      pathPrefix += File.separator;
    }
    CodeGenerator schemaCodeGenerator = new SchemaCodeGenerator(this, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix
=======
    packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , config);
    schemaCodeGenerator.createFiles(pathPrefix);
    CodeGenerator factoryCodeGenerator = new GraphFactoryGenerator(this, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix
=======
    packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , config);
    factoryCodeGenerator.createFiles(pathPrefix);
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass.getQualifiedName().equals("Graph")
=======
    graphClass.getQualifiedName().equals("Graph")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      throw new SchemaException("The defined GraphClass must not be named Graph!");
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.createFiles(config, pathPrefix, progressFunction, schemaElements, currentCount, interval);
=======
    createFiles(config, pathPrefix, progressFunction, schemaElements, currentCount, interval);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    if (progressFunction != null) {
      progressFunction.finished();
    }
  }

  @Override public int compareTo(Schema other) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.qualifiedName.compareTo(other.getQualifiedName())
=======
    qualifiedName.compareTo(other.getQualifiedName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public void compile(CodeGeneratorConfiguration config) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.finish();
=======
    assertFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      throw new SchemaException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      "Cannot compile schema " + this.qualifiedName
=======
      "Cannot compile schema " + qualifiedName
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
       + ". Most probably you use a JRE instead of a JDK. " + "The JRE does not provide a compiler.");
    }
    StandardJavaFileManager jfm = compiler.getStandardFileManager(null, null, null);
    ClassFileManager manager = new ClassFileManager(this, jfm);
    Vector<InMemoryJavaSourceFile> javaSources = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.commit(config)
=======
    commit(config)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    compiler.getTask(null, manager, null, null, null, javaSources).call();
  }

  @Override public Attribute createAttribute(String name, Domain dom, AttributedElementClass<?, ?> aec, String defaultValueAsString) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    return new AttributeImpl(name, dom, aec, defaultValueAsString);
  }

  @Override public EnumDomain createEnumDomain(String qualifiedName) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.createEnumDomain(qualifiedName, new ArrayList<String>())
=======
    createEnumDomain(qualifiedName, new ArrayList<String>())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public EnumDomain createEnumDomain(String qualifiedName, List<String> enumComponents) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    String[] components = splitQualifiedName(qualifiedName);
    PackageImpl parent = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    (PackageImpl) this.createPackageWithParents(components[0])
=======
    createPackageWithParents(components[0])
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    String simpleName = components[1];
    EnumDomain ed = new EnumDomainImpl(simpleName, parent, enumComponents);
    return ed;
  }

  @Override public GraphClass createGraphClass(String simpleName) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass != null
=======
    graphClass != null
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      throw new SchemaException("Only one GraphClass (except DefaultGraphClass) is allowed in a Schema! \'" + 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.graphClass.getQualifiedName()
=======
      graphClass.getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
       + "\' is already there.");
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (simpleName.equals(GraphClass.DEFAULTGRAPHCLASS_NAME)) {
      throw new InvalidNameException("A GraphClass must not be named like the default GraphClass (" + GraphClass.DEFAULTGRAPHCLASS_NAME + ")");
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    if (simpleName.contains(".")) {
      throw new InvalidNameException("A GraphClass must always be in the default package!");
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    GraphClassImpl gc = new GraphClassImpl(simpleName, this);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    gc.addSuperClass(this.defaultGraphClass);
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    gc
=======
    new GraphClassImpl(simpleName, this)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  protected private BooleanDomain createBooleanDomain() {
    assertNotFinished();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.booleanDomain != null) {
      throw new SchemaException("The BooleanDomain for this Schema was already created!");
    }
=======
    if (booleanDomain == null) {
      booleanDomain = new BooleanDomainImpl(this);
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    this.booleanDomain = new BooleanDomainImpl(this);
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.booleanDomain
=======
    booleanDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  protected private DoubleDomain createDoubleDomain() {
    assertNotFinished();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.doubleDomain != null) {
      throw new SchemaException("The DoubleDomain for this Schema was already created!");
    }
=======
    if (doubleDomain == null) {
      doubleDomain = new DoubleDomainImpl(this);
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    this.doubleDomain = new DoubleDomainImpl(this);
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.doubleDomain
=======
    doubleDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  protected private IntegerDomain createIntegerDomain() {
    assertNotFinished();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.integerDomain != null) {
      throw new SchemaException("The IntegerDomain for this Schema was already created!");
    }
=======
    if (integerDomain == null) {
      integerDomain = new IntegerDomainImpl(this);
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    this.integerDomain = new IntegerDomainImpl(this);
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.integerDomain
=======
    integerDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  protected private LongDomain createLongDomain() {
    assertNotFinished();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.longDomain != null) {
      throw new SchemaException("The LongDomain for this Schema was already created!");
    }
=======
    if (longDomain == null) {
      longDomain = new LongDomainImpl(this);
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    this.longDomain = new LongDomainImpl(this);
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.longDomain
=======
    longDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  protected private StringDomain createStringDomain() {
    assertNotFinished();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.stringDomain != null) {
      throw new SchemaException("The StringDomain for this Schema was already created!");
    }
=======
    if (stringDomain == null) {
      stringDomain = new StringDomainImpl(this);
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    this.stringDomain = new StringDomainImpl(this);
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.stringDomain
=======
    stringDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public ListDomain createListDomain(Domain baseDomain) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    String qn = "List<" + baseDomain.getQualifiedName() + ">";
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.domains.containsKey(qn)
=======
    domains.containsKey(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      return (ListDomain) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.domains.get(qn)
=======
      domains.get(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
    }
    return new ListDomainImpl(this, baseDomain);
  }

  @Override public MapDomain createMapDomain(Domain keyDomain, Domain valueDomain) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    String qn = "Map<" + keyDomain.getQualifiedName() + ", " + valueDomain.getQualifiedName() + ">";
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.domains.containsKey(qn)
=======
    domains.containsKey(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      return (MapDomain) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.domains.get(qn)
=======
      domains.get(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
    }
    return new MapDomainImpl(this, keyDomain, valueDomain);
  }

  protected 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
   createPackage(String sn, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
   parentPkg) {
    assertNotFinished();
    return new PackageImpl(sn, parentPkg, this);
  }

  /**
	 * Creates a {@link Package} with given qualified name, or returns an
	 * existing package with this qualified name.
	 *
	 * @param qn
	 *            the qualified name of the package
	 * @return a new {@link Package} with the given qualified name, or an
	 *         existing package with this qualified name.
	 */

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  protected
=======
>>>>>>> Unknown file: This is a bug in JDime.
   PackageImpl createPackageWithParents(String qn) {
    assertNotFinished();
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packages.containsKey(qn)
=======
    packages.containsKey(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.packages.get(qn)
=======
      packages.get(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
    }
    String[] components = splitQualifiedName(qn);
    String parent = components[0];
    String pkgSimpleName = components[1];
    assert !pkgSimpleName.contains(".") : "The package simple name \'" + pkgSimpleName + "\' must not contain a dot!";
    PackageImpl currentParent = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.defaultPackage
=======
    defaultPackage
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    String currentPkgQName = "";
    if (!
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packages.containsKey(parent)
=======
    packages.containsKey(parent)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      for (String component : parent.split("\\.")) {
        if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        currentParent != this.defaultPackage
=======
        currentParent != defaultPackage
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        ) {
          currentPkgQName = currentParent.getQualifiedName() + "." + component;
        } else {
          currentPkgQName = component;
        }
        if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.packages.containsKey(currentPkgQName)
=======
        packages.containsKey(currentPkgQName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        ) {
          currentParent = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
          this.packages.get(currentPkgQName)
=======
          packages.get(currentPkgQName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
          ;
          continue;
        }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        currentParent = this.createPackage(component, currentParent)
=======
        currentParent = createPackage(component, currentParent)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        ;
      }
    } else {
      currentParent = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.packages.get(parent)
=======
      packages.get(parent)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
    }
    assert currentParent.getQualifiedName().equals(parent) : "Something went wrong when creating a package with parents: " + "parent should be \"" + parent + "\" but created was \"" + currentParent.getQualifiedName() + "\".";
    assert (currentParent.getQualifiedName().isEmpty() ? 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    currentParent == this.defaultPackage
=======
    currentParent == defaultPackage
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
     : true) : "The parent package of package \'" + pkgSimpleName + "\' is empty, but not the default package.";
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.createPackage(pkgSimpleName, currentParent)
=======
    createPackage(pkgSimpleName, currentParent)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  /**
	 * Given a qualified name like foo.bar.baz returns a string array with two
	 * components: the package prefix (foo.bar) and the simple name (baz).
	 *
	 * @param qualifiedName
	 *            a qualified name
	 * @return a string array with two components: the package prefix and the
	 *         simple name
	 */
  public static String[] splitQualifiedName(String qualifiedName) {
    int lastIndex = qualifiedName.lastIndexOf('.');
    String[] components = new String[2];
    if (lastIndex == -1) {
      components[0] = "";
      components[1] = qualifiedName;
    } else {
      components[0] = qualifiedName.substring(0, lastIndex);
      if ((components[0].length() >= 1) && (components[0].charAt(0) == '.')) {
        components[0] = components[0].substring(1);
      }
      components[1] = qualifiedName.substring(lastIndex + 1);
    }
    return components;
  }

  @Override public RecordDomain createRecordDomain(String qualifiedName) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.createRecordDomain(qualifiedName, null)
=======
    createRecordDomain(qualifiedName, null)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public RecordDomain createRecordDomain(String qualifiedName, Collection<RecordComponent> recordComponents) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    String[] components = splitQualifiedName(qualifiedName);
    PackageImpl parent = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    (PackageImpl) this.createPackageWithParents(components[0])
=======
    createPackageWithParents(components[0])
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    String simpleName = components[1];
    RecordDomain rd = new RecordDomainImpl(simpleName, parent, recordComponents);
    return rd;
  }

  @Override public SetDomain createSetDomain(Domain baseDomain) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.finished) {
      throw new SchemaException("No changes to finished schema!");
    }
=======
    assertNotFinished();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    String qn = "Set<" + baseDomain.getQualifiedName() + ">";
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.domains.containsKey(qn)
=======
    domains.containsKey(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      return (SetDomain) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.domains.get(qn)
=======
      domains.get(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
    }
    return new SetDomainImpl(this, baseDomain);
  }

  @Override public boolean equals(Object other) {
    if ((other == null) || !(other instanceof Schema)) {
      return false;
    }
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.qualifiedName.equals(((Schema) other).getQualifiedName())
=======
    qualifiedName.equals(((SchemaImpl) other).qualifiedName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public int hashCode() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.qualifiedName.hashCode()
=======
    qualifiedName.hashCode()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public <T extends AttributedElementClass<?, ?>> T getAttributedElementClass(String qualifiedName) {
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass == null
=======
    graphClass == null
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      return null;
    } else {
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.graphClass.getQualifiedName().equals(qualifiedName)
=======
      graphClass.getQualifiedName().equals(qualifiedName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ) {
        return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        (T) this.graphClass
=======
        (T) graphClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        ;
      } else {
        return (T) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.graphClass.getGraphElementClass(qualifiedName)
=======
        graphClass.getGraphElementClass(qualifiedName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        ;
      }
    }
  }

  @Override public List<CompositeDomain> getCompositeDomains() {
    ArrayList<CompositeDomain> topologicalOrderList = new ArrayList<CompositeDomain>();
    for (Domain dom : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.domainsDag.getNodesInTopologicalOrder()
=======
    domainsDag.getNodesInTopologicalOrder()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      if (dom instanceof CompositeDomain) {
        topologicalOrderList.add((CompositeDomain) dom);
      }
    }
    return topologicalOrderList;
  }

  private Method getCreateMethod(String className, String graphClassName, Class<?>[] signature, ImplementationType implementationType) {
    Class<? extends Graph> schemaClass = null;
    AttributedElementClass<?, ?> aec = null;
    try {
      schemaClass = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.getGraphClassImpl(implementationType)
=======
      getGraphClassImpl(implementationType)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
      if (className.equals(graphClassName)) {
        if (implementationType != ImplementationType.GENERIC) {
          return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
          this.getClass().getMethod("create" + graphClassName, signature)
=======
          getClass().getMethod("create" + graphClassName, signature)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
          ;
        } else {
          return schemaClass.getMethod("createGraph", signature);
        }
      } else {
        aec = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.graphClass.getVertexClass(className)
=======
        graphClass.getVertexClass(className)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        ;
        if (aec == null) {
          aec = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
          this.graphClass.getEdgeClass(className)
=======
          graphClass.getEdgeClass(className)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
          ;
          if (aec == null) {
            throw new SchemaClassAccessException("class " + className + " does not exist in schema");
          }
        }
        if (implementationType != ImplementationType.GENERIC) {
          return schemaClass.getMethod("create" + CodeGenerator.camelCase(aec.getUniqueName()), signature);
        } else {
          if (signature[0].equals(VertexClass.class)) {
            return schemaClass.getMethod("createVertex", signature);
          } else {
            return schemaClass.getMethod("createEdge", signature);
          }
        }
      }
    } catch (SecurityException e) {
      throw new SchemaClassAccessException("can\'t find create method in \'" + schemaClass.getName() + "\' for \'" + aec.getUniqueName() + "\'", e);
    } catch (NoSuchMethodException e) {
      throw new SchemaClassAccessException("can\'t find create method in \'" + schemaClass.getName() + "\' for \'" + aec.getUniqueName() + "\'", e);
    }
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  @Override public EdgeClass getDefaultEdgeClass() {
    return this.defaultEdgeClass;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.



<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  @Override public GraphClass getDefaultGraphClass() {
    return this.defaultGraphClass;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public Package getDefaultPackage() {
    return this.defaultPackage;
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  @Override public VertexClass getDefaultVertexClass() {
    return this.defaultVertexClass;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public Domain getDomain(String domainName) {
    return this.domains.get(domainName);
  }

  @Override public Map<String, Domain> getDomains() {
    return this.domains;
  }

  protected DirectedAcyclicGraph<Domain> getDomainsDag() {
    return this.domainsDag;
  }

  void addDomainDependency(Domain composite, Domain base) {
    domainsDag.createEdge(base, composite);
  }

  @Override public List<EdgeClass> getEdgeClasses() {
    List<EdgeClass> ec_top = new ArrayList<EdgeClass>();
    ec_top.add(this.defaultEdgeClass);
    for (EdgeClass ec : this.graphClass.getEdgeClasses()) {
      ec_top.add(ec);
    }
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    ec_top
=======
    graphClass.getEdgeClasses()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public Method getEdgeCreateMethod(String edgeClassName, ImplementationType implementationType) {
    AttributedElementClass<?, ?> aec = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.getAttributedElementClass(edgeClassName)
=======
    getAttributedElementClass(edgeClassName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    if ((aec == null) || !(aec instanceof EdgeClass)) {
      throw new SchemaException("There\'s no EdgeClass with qualified name " + edgeClassName + "!");
    }
    EdgeClass ec = (EdgeClass) aec;
    String methodName = "create" + CodeGenerator.camelCase(ec.getUniqueName());
    Class<?> schemaClass = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.getGraphClassImpl(implementationType)
=======
    getGraphClassImpl(implementationType)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    if (implementationType != ImplementationType.GENERIC) {
      for (Method m : schemaClass.getMethods()) {
        if (m.getName().equals(methodName) && (m.getParameterTypes().length == 3)) {
          return m;
        }
      }
    } else {
      try {
        return schemaClass.getMethod("createEdge", new Class[] { EdgeClass.class, int.class, Vertex.class, Vertex.class });
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      }
    }
    throw new SchemaClassAccessException("can\'t find create method \'" + methodName + "\' in \'" + schemaClass.getName() + "\' for \'" + ec.getUniqueName() + "\'");
  }

  @Override public List<EnumDomain> getEnumDomains() {
    ArrayList<EnumDomain> enumList = new ArrayList<EnumDomain>();
    for (Domain dl : this.domains.values()) {
      if (dl instanceof EnumDomain) {
        enumList.add((EnumDomain) dl);
      }
    }
    return enumList;
  }

  @Override public BooleanDomain getBooleanDomain() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.booleanDomain
=======
    booleanDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public DoubleDomain getDoubleDomain() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.doubleDomain
=======
    doubleDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public IntegerDomain getIntegerDomain() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.integerDomain
=======
    integerDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public LongDomain getLongDomain() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.longDomain
=======
    longDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public StringDomain getStringDomain() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.stringDomain
=======
    stringDomain
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public GraphClass getGraphClass() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass
=======
    graphClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  /**
	 *
	 * @param implementationType
	 * @return
	 */
  @SuppressWarnings(value = { "unchecked" }) private Class<? extends Graph> getGraphClassImpl(ImplementationType implementationType) {
    String implClassName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix + "."
=======
    packagePrefix + "."
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    switch (implementationType) {
      case STANDARD:
      implClassName += IMPLSTDPACKAGENAME;
      break;
      case TRANSACTION:
      implClassName += IMPLTRANSPACKAGENAME;
      break;
      case DATABASE:
      implClassName += IMPLDATABASEPACKAGENAME;
      case GENERIC:
      implClassName = "de.uni_koblenz.jgralab.impl.generic";
      break;
      default:
      throw new SchemaException("Implementation type " + implementationType + " not supported yet.");
    }
    Class<? extends Graph> schemaClass;
    if (implementationType != ImplementationType.GENERIC) {
      implClassName = implClassName + "." + 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.graphClass.getSimpleName()
=======
      graphClass.getSimpleName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
       + "Impl";
      try {
        schemaClass = (Class<? extends Graph>) Class.forName(implClassName, true, SchemaClassManager.instance(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
        this.qualifiedName
=======
        qualifiedName
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
        ));
      } catch (ClassNotFoundException e) {
        throw new SchemaClassAccessException("can\'t load implementation class \'" + implClassName + "\'", e);
      }
      return schemaClass;
    } else {
      implClassName += "." + "GenericGraphImpl";
      try {
        return (Class<? extends Graph>) Class.forName(implClassName);
      } catch (ClassNotFoundException e) {
        throw new SchemaClassAccessException("can\'t load implementation class \'" + implClassName + "\'", e);
      }
    }
  }

  @Override public String getName() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.name
=======
    name
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  /**
	 * only used internally
	 *
	 * @return number of graphelementclasses contained in graphclass
	 */
  private int getNumberOfElements() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass.getGraphElementClasses().size()
=======
    graphClass.getGraphElementClasses().size()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
     + 1;
  }

  @Override public Package getPackage(String packageName) {
    return this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    packages.get(packageName)
=======
    get(packageName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public String getPackagePrefix() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix
=======
    packagePrefix
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  @Override public Map<String, Package> getPackages() {
    return this.packages;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public String getQualifiedName() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.qualifiedName
=======
    qualifiedName
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public List<RecordDomain> getRecordDomains() {
    ArrayList<RecordDomain> recordList = new ArrayList<RecordDomain>();
    for (Domain dl : this.domains.values()) {
      if (dl instanceof RecordDomain) {
        recordList.add((RecordDomain) dl);
      }
    }
    return recordList;
  }

  @Override public List<VertexClass> getVertexClasses() {
    List<VertexClass> vc_top = new ArrayList<VertexClass>();
    vc_top.add(this.defaultVertexClass);
    for (VertexClass vc : this.graphClass.getVertexClasses()) {
      vc_top.add(vc);
    }
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    vc_top
=======
    graphClass.getVertexClasses()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public Method getVertexCreateMethod(String vertexClassName, ImplementationType implementationType) {
    if (implementationType != ImplementationType.GENERIC) {
      return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.getCreateMethod(vertexClassName, this.graphClass.getSimpleName(), VERTEX_CLASS_CREATE_SIGNATURE, implementationType)
=======
      getCreateMethod(vertexClassName, graphClass.getSimpleName(), VERTEX_CLASS_CREATE_SIGNATURE, implementationType)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
    } else {
      return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.getCreateMethod(vertexClassName, this.graphClass.getSimpleName(), new Class[] { VertexClass.class, int.class }, implementationType)
=======
      getCreateMethod(vertexClassName, graphClass.getSimpleName(), new Class[] { VertexClass.class, int.class }, implementationType)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ;
    }
  }

  @Override public boolean isValidEnumConstant(String name) {
    if (name.isEmpty()) {
      return false;
    }
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    !this.allowLowercaseEnumConstants
=======
    !allowLowercaseEnumConstants
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
     && !name.equals(name.toUpperCase())) {
      return false;
    }
    if (RESERVED_JAVA_WORDS.contains(name)) {
      return false;
    }
    if (!Character.isJavaIdentifierStart(name.charAt(0))) {
      return false;
    }
    for (char c : name.toCharArray()) {
      if (!Character.isJavaIdentifierPart(c)) {
        return false;
      }
    }
    return true;
  }

  @Override public boolean knows(String qn) {
    return (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.namedElements.containsKey(qn)
=======
    namedElements.containsKey(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
     || 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.getQualifiedName().equals(qn)
=======
    getQualifiedName().equals(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    );
  }

  @Override public void setAllowLowercaseEnumConstants(boolean allowLowercaseEnumConstants) {
    this.allowLowercaseEnumConstants = allowLowercaseEnumConstants;
  }

  void setGraphClass(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
  GraphClass
=======
  GraphClassImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
   gc) {
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass != null
=======
    graphClass != null
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ) {
      throw new SchemaException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      "There already is a GraphClass named: "
=======
      "A GraphClass named \'"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
       + 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      this.graphClass.getQualifiedName()
=======
      graphClass.getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
       + 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      "in the Schema!"
=======
      "\' already exists in this Schema!"
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      );
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.graphClass = gc
=======
    graphClass = gc
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  /**
	 * @return the textual representation of the schema with all graph classes,
	 *         their edge and vertex classes, all attributes and the whole
	 *         hierarchy of those classes
	 */
  public String getDescriptionString() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    "GraphClass of schema \'" + this.qualifiedName
=======
    "GraphClass of schema \'" + qualifiedName
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
     + "\':\n\n\n" + 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    ((GraphClassImpl) this.graphClass).getDescriptionString()
=======
    graphClass.getDescriptionString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public String toString() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.getQualifiedName()
=======
    getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public String toTGString() {
    String schemaDefinition = null;
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(byteOut);
    try {
      GraphIO.saveSchemaToStream(this, out);
      out.close();
      byteOut.close();
      schemaDefinition = new String(byteOut.toByteArray());
    } catch (GraphIOException e) {
      throw new 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      RuntimeException
=======
      SchemaException
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      (e);
    } catch (IOException e) {
      throw new 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      RuntimeException
=======
      SchemaException
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      (e);
    }
    return schemaDefinition;
  }

  @Override public String getFileName() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.qualifiedName.replace('.', File.separatorChar)
=======
    qualifiedName.replace('.', File.separatorChar)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public String getPathName() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.packagePrefix.replace('.', File.separatorChar)
=======
    packagePrefix.replace('.', File.separatorChar)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public GraphFactory createDefaultGraphFactory(ImplementationType implementationType) {
    assertFinished();
    if (implementationType != ImplementationType.GENERIC) {
      throw new 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
      IllegalArgumentException
=======
      SchemaException
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
      ("Base implementation can\'t create a GraphFactory for implementation type " + implementationType + ". Only GENERIC is supported.");
    }
    return new GenericGraphFactoryImpl(this);
  }

  @Override public Graph createGraph(ImplementationType implementationType) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.createGraph(implementationType, null, 100, 100)
=======
    createGraph(implementationType, null, 100, 100)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  @Override public Graph createGraph(ImplementationType implementationType, String id, int vMax, int eMax) {
    assertFinished();
    GraphFactory factory = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.createDefaultGraphFactory(implementationType)
=======
    createDefaultGraphFactory(implementationType)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
    return factory.createGraph(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.getGraphClass()
=======
    getGraphClass()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    , id, vMax, eMax);
  }

  protected void assertFinished() {
    if (!finished) {
      throw new SchemaException("Schema must be finished.");
    }
  }

  protected void assertNotFinished() {
    if (finished) {
      throw new SchemaException("No changes allowed in a finished Schema.");
    }
  }

  /**
	 * @return whether the schema is finished
	 */
  @Override public boolean isFinished() {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.finished
=======
    finished
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  /**
	 * Signals that the schema is finished. No more changes are allowed. To open
	 * the change mode call reopen
	 */
  @Override public void finish() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (this.finished) {
      return;
    }
=======
    if (finished) {
      return;
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    if (graphClass == null) {
      throw new SchemaException("Can\'t finish a schema without a GraphClass. Create a GraphClass first!");
    }
    domainsDag.finish();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    ((GraphClassImpl) this.graphClass).finish()
=======
    graphClass.finish()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    this.finished = true
=======
    finished = true
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java
    ;
  }

  /**
	 * Reopens the schema to allow changes. To finish the schema again, call
	 * finish
	 */
  @Override public void reopen() {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/left.java
    if (!this.finished) {
      return;
    }
=======
    throw new UnsupportedOperationException();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/SchemaImpl.java/right.java

    ((GraphClassImpl) this.graphClass).finish();
    this.finished = false;
  }

  @Override public void save(String filename) throws GraphIOException {
    GraphIO.saveSchemaToFile(this, filename);
  }

  @Override public void save(DataOutputStream out) throws GraphIOException {
    GraphIO.saveSchemaToStream(this, out);
  }
}