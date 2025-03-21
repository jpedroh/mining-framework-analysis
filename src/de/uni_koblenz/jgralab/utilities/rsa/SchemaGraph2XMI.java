package de.uni_koblenz.jgralab.utilities.rsa;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import de.uni_koblenz.ist.utilities.option_handler.OptionHandler;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.grumlschema.GrumlSchema;
import de.uni_koblenz.jgralab.grumlschema.SchemaGraph;
import de.uni_koblenz.jgralab.grumlschema.domains.BooleanDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.CollectionDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.Domain;
import de.uni_koblenz.jgralab.grumlschema.domains.DoubleDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.EnumDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.HasRecordDomainComponent;
import de.uni_koblenz.jgralab.grumlschema.domains.IntegerDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.LongDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.MapDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.RecordDomain;
import de.uni_koblenz.jgralab.grumlschema.domains.StringDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.AggregationKind;
import de.uni_koblenz.jgralab.grumlschema.structure.Annotates;
import de.uni_koblenz.jgralab.grumlschema.structure.Attribute;
import de.uni_koblenz.jgralab.grumlschema.structure.AttributedElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.Comment;
import de.uni_koblenz.jgralab.grumlschema.structure.Constraint;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsDomain;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsGraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.ContainsSubPackage;
import de.uni_koblenz.jgralab.grumlschema.structure.EdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.EndsAt;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphClass;
import de.uni_koblenz.jgralab.grumlschema.structure.GraphElementClass;
import de.uni_koblenz.jgralab.grumlschema.structure.HasAttribute;
import de.uni_koblenz.jgralab.grumlschema.structure.HasConstraint;
import de.uni_koblenz.jgralab.grumlschema.structure.IncidenceClass;
import de.uni_koblenz.jgralab.grumlschema.structure.NamedElement;
import de.uni_koblenz.jgralab.grumlschema.structure.Package;
import de.uni_koblenz.jgralab.grumlschema.structure.Redefines;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesEdgeClass;
import de.uni_koblenz.jgralab.grumlschema.structure.SpecializesVertexClass;
import de.uni_koblenz.jgralab.grumlschema.structure.VertexClass;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.Schema2SchemaGraph;

/**
 * SchemaGraph2XMI is a utility that converts a SchemaGraph or a TG schema file
 * into a XMI file which can be imported by IBM (tm) Rational Software Architect
 * (tm). The converter the StAX writer to create the xmi. As intermediate
 * format, a grUML schema graph is created from the TG schema file.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SchemaGraph2XMI {
  /**
	 * This {@link TreeSet} stores all domains which have to have an entry in
	 * the package PrimitiveTypes, because they are no default attribute types
	 * of UML and have no explicitly defined UML element in the XMI. Those
	 * domains are the LongDomain, the DoubleDomain, all CollectionDomains and
	 * all MapDomains.
	 */
  private final TreeSet<Domain> typesToBeDeclaredAtTheEnd = new TreeSet<Domain>();

  /**
	 * If set to <code>true</code>, the EdgeClasses are created as associations
	 * which are bidirectional navigable.
	 */
  private boolean isBidirectional = false;

  /**
	 * Processes an TG-file as schema or a schema in a grUML graph to a XMI
	 * file. For all command line options see
	 * {@link Rsa2Tg#processCommandLineOptions(String[])}.
	 * 
	 * @param args
	 *            {@link String} array of command line options.
	 * @throws GraphIOException
	 */
  public static void main(String[] args) throws GraphIOException {
    System.out.println("SchemaGraph to XMI");
    System.out.println("==================");
    SchemaGraph2XMI s = new SchemaGraph2XMI();
    CommandLine cli = processCommandLineOptions(args);
    assert cli != null : "No CommandLine object has been generated!";
    s.isBidirectional = cli.hasOption("b");
    String outputName = cli.getOptionValue("o");
    try {
      if (cli.hasOption("ig")) {
        s.process(GrumlSchema.instance().loadSchemaGraph(cli.getOptionValue("ig")), outputName);
      } else {
        s.process(new Schema2SchemaGraph().convert2SchemaGraph(GraphIO.loadSchemaFromFile(cli.getOptionValue("i"))), outputName);
      }
    } catch (Exception e) {
      System.err.println("An Exception occured while processing " + (cli.hasOption("i") ? cli.getOptionValue("i") : cli.getOptionValue("ig")) + ".");
      System.err.println(e.getMessage());
      e.printStackTrace();
      e.printStackTrace();
    }
    System.out.println("Fini.");
  }

  /**
	 * Processes all command line parameters and returns a {@link CommandLine}
	 * object, which holds all values included in the given {@link String}
	 * array.
	 * 
	 * @param args
	 *            {@link CommandLine} parameters.
	 * @return {@link CommandLine} object, which holds all necessary values.
	 */
  public static CommandLine processCommandLineOptions(String[] args) {
    String toolString = "java " + SchemaGraph2XMI.class.getName();
    String versionString = JGraLab.getInfo(false);
    OptionHandler oh = new OptionHandler(toolString, versionString);
    Option output = new Option("o", "output", true, "(required): the output xmi file name");
    output.setRequired(true);
    output.setArgName("file");
    oh.addOption(output);
    Option schemaGraph = new Option("ig", "inputSchemaGraph", true, "(required or -i):if set, the schemaGraph is converted into a xmi.");
    schemaGraph.setRequired(false);
    schemaGraph.setArgs(0);
    output.setArgName("file");
    oh.addOption(schemaGraph);
    Option schema = new Option("i", "inputSchema", true, "(required or -ig): TG-file of the schema which should be converted into a xmi.");
    schema.setRequired(false);
    schema.setArgName("file");
    oh.addOption(schema);
    OptionGroup input = new OptionGroup();
    input.addOption(schemaGraph);
    input.addOption(schema);
    input.setRequired(true);
    oh.addOptionGroup(input);
    Option bidirectional = new Option("b", "bidirectional", false, "(optional): If set the EdgeClasses are created as bidirectional associations.");
    bidirectional.setRequired(false);
    oh.addOption(bidirectional);
    return oh.parse(args);
  }

  /**
	 * Converts the {@link SchemaGraph} to the XMI file <code>xmiName</code>.
	 * 
	 * @param schemaGraph
	 *            {@link SchemaGraph} the schema graph to be converted.
	 * @param xmiName
	 *            {@link String} the path of the xmi file to be created.
	 * @throws XMLStreamException
	 * @throws IOException
	 */
  public void process(SchemaGraph schemaGraph, String xmiName) throws XMLStreamException, IOException {
    createXMI(xmiName, schemaGraph);
  }

  /**
	 * This method creates the XMI file. Created content is:<br/>
	 * <code>&lt;?xml version="{@link XMIConstants4SchemaGraph2XMI#XML_VERSION}" encoding="{@link XMIConstants4SchemaGraph2XMI#XML_ENCODING}"?&gt;<br/>
	 * &lt;!-- content created by {@link SchemaGraph2XMI#createRootElement(XMLStreamWriter, SchemaGraph)} --&gt;
	 * </code>
	 * 
	 * @param xmiName
	 *            {@link String} the path of the XMI file
	 * @param schemaGraph
	 *            {@link SchemaGraph} to be converted into an XMI
	 * @throws XMLStreamException
	 * @throws IOException
	 */
  private void createXMI(String xmiName, SchemaGraph schemaGraph) throws XMLStreamException, IOException {
    Writer out = null;
    XMLStreamWriter writer = null;
    try {
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmiName), "UTF-8"));
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      factory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
      writer = factory.createXMLStreamWriter(out);
      writer.writeStartDocument(XMIConstants4SchemaGraph2XMI.XML_ENCODING, XMIConstants4SchemaGraph2XMI.XML_VERSION);
      createRootElement(writer, schemaGraph);
      writer.writeEndDocument();
      writer.flush();
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      }  finally {
        if (out != null) {
          out.close();
        }
      }
    }
  }

  /**
	 * Creates the root <code>XMI</code> tag of the whole document. It calls
	 * {@link SchemaGraph2XMI#createModelElement(XMLStreamWriter, SchemaGraph)}
	 * to create its content.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param schemaGraph
	 *            {@link SchemaGraph} to be converted into an XMI
	 * @throws XMLStreamException
	 */
  private void createRootElement(XMLStreamWriter writer, SchemaGraph schemaGraph) throws XMLStreamException {
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.NAMESPACE_PREFIX_XMI, XMIConstants4SchemaGraph2XMI.XMI_TAG_XMI, XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_VERSION, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_VERSION_VALUE);
    writer.setPrefix(XMIConstants4SchemaGraph2XMI.NAMESPACE_PREFIX_XSI, XMIConstants4SchemaGraph2XMI.NAMESPACE_XSI);
    writer.setPrefix(XMIConstants4SchemaGraph2XMI.NAMESPACE_PREFIX_EECORE, XMIConstants4SchemaGraph2XMI.NAMESPACE_EECORE);
    writer.setPrefix(XMIConstants4SchemaGraph2XMI.NAMESPACE_PREFIX_ECORE, XMIConstants4SchemaGraph2XMI.NAMESPACE_ECORE);
    writer.setPrefix(XMIConstants4SchemaGraph2XMI.NAMESPACE_PREFIX_UML, XMIConstants4SchemaGraph2XMI.NAMESPACE_UML);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XSI, XMIConstants4SchemaGraph2XMI.XSI_ATTRIBUTE_SCHEMALOCATION, XMIConstants4SchemaGraph2XMI.SCHEMALOCATION);
    createModelElement(writer, schemaGraph);
    writer.writeEndElement();
  }

  /**
	 * Creates the <code>Model</code> tag and its content consisting of
	 * <ul>
	 * <li>the {@link GraphClass},</li>
	 * <li>the defaultPackage and its content,</li>
	 * <li>the representation of the domains in
	 * {@link SchemaGraph2XMI#typesToBeDeclaredAtTheEnd} and</li>
	 * <li>and the profile application</li>
	 * </ul>
	 * by calling the corresponding methods.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param schemaGraph
	 *            {@link SchemaGraph} to be converted into an XMI
	 * @throws XMLStreamException
	 */
  private void createModelElement(XMLStreamWriter writer, SchemaGraph schemaGraph) throws XMLStreamException {
    de.uni_koblenz.jgralab.grumlschema.structure.Schema schema = schemaGraph.getFirstSchema();
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.NAMESPACE_UML, XMIConstants4SchemaGraph2XMI.UML_TAG_MODEL);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, schema.get_packagePrefix() + "." + schema.get_name());
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, schema.get_packagePrefix() + "." + schema.get_name());
    createAttributedElementClass(writer, schemaGraph.getFirstGraphClass());
    createPackage(writer, (Package) schemaGraph.getFirstSchema().getFirstContainsDefaultPackageIncidence().getThat());
    if (!typesToBeDeclaredAtTheEnd.isEmpty()) {
      createTypes(writer);
    }
    createProfileApplication(writer);
    writer.writeEndElement();
  }

  /**
	 * This method creates the representation of:
	 * <ul>
	 * <li>the {@link Package} <code>pack</code>, if it is not the
	 * defaultPackage,</li>
	 * <li>{@link Comment}s which are attached to <code>pack</code>,</li>
	 * <li>{@link EnumDomain}s,</li>
	 * <li>{@link RecordDomain}s,</li>
	 * <li>{@link GraphElementClass}es and</li>
	 * <li>{@link Package}s</li>
	 * </ul>
	 * contained in <code>pack</code>. The creation of other {@link Domain}s
	 * except {@link EnumDomain}s and {@link RecordDomain}s are explained at
	 * {@link SchemaGraph2XMI#createAttribute(XMLStreamWriter, String, String, Domain, String)}
	 * .
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param pack
	 *            {@link Package} the current {@link Package}
	 * @throws XMLStreamException
	 */
  private void createPackage(XMLStreamWriter writer, Package pack) throws XMLStreamException {
    boolean packageTagHasToBeClosed = false;
    if (!isPackageEmpty(pack)) {
      if (!pack.get_qualifiedName().equals(de.uni_koblenz.jgralab.schema.Package.DEFAULTPACKAGE_NAME)) {
        packageTagHasToBeClosed = pack.getFirstAnnotatesIncidence() != null || pack.getFirstContainsDomainIncidence() != null || pack.getFirstContainsGraphElementClassIncidence() != null || pack.getFirstContainsSubPackageIncidence(EdgeDirection.OUT) != null;
        if (packageTagHasToBeClosed) {
          writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_PACKAGEDELEMENT);
        } else {
          writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_PACKAGEDELEMENT);
        }
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_TYPE_VALUE_PACKAGE);
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, pack.get_qualifiedName());
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, extractSimpleName(pack.get_qualifiedName()));
      }
      createComments(writer, pack);
      for (ContainsDomain cd : pack.getContainsDomainIncidences()) {
        Domain domain = (Domain) cd.getThat();
        if (!domain.get_qualifiedName().equals(de.uni_koblenz.jgralab.schema.BooleanDomain.BOOLEANDOMAIN_NAME) && !domain.get_qualifiedName().equals(de.uni_koblenz.jgralab.schema.DoubleDomain.DOUBLEDOMAIN_NAME) && !domain.get_qualifiedName().equals(de.uni_koblenz.jgralab.schema.IntegerDomain.INTDOMAIN_NAME) && !domain.get_qualifiedName().equals(de.uni_koblenz.jgralab.schema.LongDomain.LONGDOMAIN_NAME) && !domain.get_qualifiedName().equals(de.uni_koblenz.jgralab.schema.StringDomain.STRINGDOMAIN_NAME)) {
          if (domain instanceof EnumDomain) {
            createEnum(writer, (EnumDomain) domain);
          } else {
            if (domain instanceof RecordDomain) {
              createRecordDomain(writer, (RecordDomain) domain);
            }
          }
        }
      }
      for (ContainsGraphElementClass cgec : pack.getContainsGraphElementClassIncidences()) {
        GraphElementClass gec = (GraphElementClass) cgec.getThat();
        createAttributedElementClass(writer, gec);
      }
      for (ContainsSubPackage csp : pack.getContainsSubPackageIncidences(EdgeDirection.OUT)) {
        createPackage(writer, (Package) csp.getThat());
      }
      if (packageTagHasToBeClosed) {
        writer.writeEndElement();
      }
    }
  }

  /**
	 * Returns <code>true</code> if the {@link Package} <code>pack</code> does
	 * not contain a {@link Comment}, a {@link Domain}, a
	 * {@link GraphElementClass} and no nonempty {@link Package}. Otherwise
	 * <code>false</code> is returned.
	 * 
	 * @param pack
	 *            {@link Package} the current {@link Package}
	 * @return boolean
	 */
  private boolean isPackageEmpty(Package pack) {
    boolean isPackageEmpty = pack.getFirstAnnotatesIncidence() == null && pack.getFirstContainsDomainIncidence() == null && pack.getFirstContainsGraphElementClassIncidence() == null;
    if (!isPackageEmpty) {
      return false;
    } else {
      for (ContainsSubPackage csp : pack.getContainsSubPackageIncidences(EdgeDirection.OUT)) {
        if (!isPackageEmpty((Package) csp.getThat())) {
          return false;
        }
      }
      return true;
    }
  }

  /**
	 * Creates the representation of the {@link RecordDomain}
	 * <code>recordDomain</code>. This representation consists of an UML class
	 * with stereotype <code>&lt;&lt;record&gt;&gt;</code>. The components of
	 * <code>recordDomain</code> are represented as attributes of the generated
	 * UML class. Further more all {@link Comment}s attached to
	 * <code>recordDomain</code> are created.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param recordDomain
	 *            {@link RecordDomain} the current {@link RecordDomain}
	 * @throws XMLStreamException
	 */
  private void createRecordDomain(XMLStreamWriter writer, RecordDomain recordDomain) throws XMLStreamException {
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_PACKAGEDELEMENT);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_TYPE_VALUE_CLASS);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, recordDomain.get_qualifiedName());
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, extractSimpleName(recordDomain.get_qualifiedName()));
    createExtension(writer, recordDomain, "record");
    createComments(writer, recordDomain);
    for (HasRecordDomainComponent hrdc : recordDomain.getHasRecordDomainComponentIncidences(EdgeDirection.OUT)) {
      createAttribute(writer, hrdc.get_name(), null, (Domain) hrdc.getThat(), recordDomain.get_qualifiedName() + "_" + hrdc.get_name());
    }
    writer.writeEndElement();
  }

  /**
	 * Creates the representation of the {@link EnumDomain}
	 * <code>EnumDomain</code>. This representation is a UML enumeration with
	 * stereotype <code>&lt;&lt;record&gt;&gt;</code>. The constants of
	 * <code>enumDomain</code> are represented as constants of the generated UML
	 * enumeration. Further more all {@link Comment}s attached to
	 * <code>enumDomain</code> are created.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param enumDomain
	 *            {@link EnumDomain} the current {@link EnumDomain}
	 * @throws XMLStreamException
	 */
  private void createEnum(XMLStreamWriter writer, EnumDomain enumDomain) throws XMLStreamException {
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_PACKAGEDELEMENT);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_TYPE_VALUE_ENUMERATION);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, enumDomain.get_qualifiedName());
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, extractSimpleName(enumDomain.get_qualifiedName()));
    createComments(writer, enumDomain);
    for (String enumConst : enumDomain.get_enumConstants()) {
      writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_OWNEDLITERAL);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.OWNEDLITERAL_TYPE_VALUE);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, enumDomain.get_qualifiedName() + "_" + enumConst);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, enumConst);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.OWNEDLITERAL_ATTRIBUTE_CLASSIFIER, enumDomain.get_qualifiedName());
    }
    writer.writeEndElement();
  }

  /**
	 * Creates the profileApplication tag at the end of the model tag.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @throws XMLStreamException
	 */
  private void createProfileApplication(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_PROFILEAPPLICATION);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.PROFILEAPPLICATION_TYPE_VALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, XMIConstants4SchemaGraph2XMI.TAG_PROFILEAPPLICATION + System.currentTimeMillis());
    createExtension(writer, null, null);
    writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_APPLIEDPROFILE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.APPLIEDPROFILE_TYPE_VALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_HREF, XMIConstants4SchemaGraph2XMI.APPLIEDPROFILE_HREF_VALUE);
    writer.writeEndElement();
  }

  /**
	 * Defines all attributes of type {@link DoubleDomain}, {@link LongDomain},
	 * {@link MapDomain} or {@link CollectionDomain} which are used in the
	 * {@link SchemaGraph} i.e. contained in
	 * {@link SchemaGraph2XMI#typesToBeDeclaredAtTheEnd}. These definitions are
	 * created in a new UML package, called <code>PrimitiveTypes</code>. This is
	 * necessary because those {@link Domain}s are not UML primitive types and
	 * are not represented by an own <code>packagedElement</code> in the XMI
	 * file.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @throws XMLStreamException
	 */
  private void createTypes(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_PACKAGEDELEMENT);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_TYPE_VALUE_PACKAGE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, XMIConstants4SchemaGraph2XMI.PACKAGE_PRIMITIVETYPES_NAME);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, XMIConstants4SchemaGraph2XMI.PACKAGE_PRIMITIVETYPES_NAME);
    for (Domain domain : typesToBeDeclaredAtTheEnd) {
      writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_PACKAGEDELEMENT);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_PRIMITIVETYPE);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, domain.get_qualifiedName().replaceAll("\\s", "").replaceAll("<", "_").replaceAll(">", "_"));
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, extractSimpleName(domain.get_qualifiedName()));
    }
    writer.writeEndElement();
  }

  /**
	 * Creates the representation of the {@link AttributedElementClass}
	 * <code>aeclass</code>.<br />
	 * A {@link GraphClass} is represented as a UML class with stereotype
	 * <code>&lt;&lt;graphclass&gt;&gt;</code>.<br />
	 * A {@link VertexClass} is represented as a UML class.<br />
	 * An {@link EdgeClass} is represented as a UML association, if it has no
	 * attributes and otherwise as a UML associationClass.<br />
	 * Furthermore the attribute abstract is generated, if the
	 * {@link VertexClass} or {@link EdgeClass} is abstract. {@link Comment}s,
	 * {@link Constraint}s, generalization and {@link Attribute}s are
	 * represented as well.<br />
	 * The {@link IncidenceClass} representations are created if needed. To get
	 * more information if it is needed, have a look at
	 * {@link SchemaGraph2XMI#createIncidences(XMLStreamWriter, EdgeClass)} and
	 * {@link SchemaGraph2XMI#createIncidences(XMLStreamWriter, VertexClass)}.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param aeclass
	 *            {@link AttributedElementClass} the current
	 *            {@link AttributedElementClass}
	 * @throws XMLStreamException
	 */
  private void createAttributedElementClass(XMLStreamWriter writer, AttributedElementClass aeclass) throws XMLStreamException {
    boolean isEmptyGraphElementClass = aeclass.getFirstAnnotatesIncidence() == null && aeclass.getFirstHasAttributeIncidence() == null && aeclass.getFirstHasConstraintIncidence() == null && (aeclass instanceof GraphElementClass && !((GraphElementClass) aeclass).is_abstract()) && (aeclass instanceof VertexClass && ((VertexClass) aeclass).getFirstSpecializesVertexClassIncidence(EdgeDirection.OUT) == null && !hasChildIncidence((VertexClass) aeclass) || aeclass instanceof EdgeClass && ((EdgeClass) aeclass).getFirstSpecializesEdgeClassIncidence(EdgeDirection.OUT) == null && isBidirectional);
    if (isEmptyGraphElementClass) {
      writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_PACKAGEDELEMENT);
    } else {
      writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_PACKAGEDELEMENT);
    }
    if (aeclass instanceof EdgeClass) {
      if (aeclass.getFirstHasAttributeIncidence() == null) {
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_TYPE_VALUE_ASSOCIATION);
      } else {
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_TYPE_VALUE_ASSOCIATIONCLASS);
      }
    } else {
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_TYPE_VALUE_CLASS);
    }
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, aeclass.get_qualifiedName());
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, extractSimpleName(aeclass.get_qualifiedName()));
    if (aeclass instanceof EdgeClass) {
      EdgeClass ec = (EdgeClass) aeclass;
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_ATTRIBUTE_MEMBEREND, ((VertexClass) ((IncidenceClass) ec.getFirstComesFromIncidence().getThat()).getFirstEndsAtIncidence().getThat()).get_qualifiedName() + "_incidence_" + ec.get_qualifiedName() + "_from " + ((VertexClass) ((IncidenceClass) ec.getFirstGoesToIncidence().getThat()).getFirstEndsAtIncidence().getThat()).get_qualifiedName() + "_incidence_" + ec.get_qualifiedName() + "_to");
    }
    if (aeclass instanceof GraphElementClass && ((GraphElementClass) aeclass).is_abstract()) {
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_ATTRIBUTE_ISABSTRACT, XMIConstants4SchemaGraph2XMI.ATTRIBUTE_VALUE_TRUE);
      createExtension(writer, aeclass, "abstract");
    }
    if (aeclass instanceof GraphClass) {
      createExtension(writer, aeclass, "graphclass");
    }
    createComments(writer, aeclass);
    createConstraints(writer, aeclass);
    if (aeclass instanceof VertexClass) {
      for (SpecializesVertexClass svc : ((VertexClass) aeclass).getSpecializesVertexClassIncidences(EdgeDirection.OUT)) {
        createGeneralization(writer, "generalization_" + aeclass.get_qualifiedName(), ((VertexClass) svc.getThat()).get_qualifiedName());
      }
    } else {
      if (aeclass instanceof EdgeClass) {
        for (SpecializesEdgeClass svc : ((EdgeClass) aeclass).getSpecializesEdgeClassIncidences(EdgeDirection.OUT)) {
          createGeneralization(writer, "generalization_" + aeclass.get_qualifiedName(), ((EdgeClass) svc.getThat()).get_qualifiedName());
        }
      }
    }
    createAttributes(writer, aeclass);
    if (aeclass instanceof VertexClass) {
      createIncidences(writer, (VertexClass) aeclass);
    } else {
      if (aeclass instanceof EdgeClass) {
        createIncidences(writer, (EdgeClass) aeclass);
      }
    }
    if (!isEmptyGraphElementClass) {
      writer.writeEndElement();
    }
  }

  /**
	 * Returns <code>true</code>, iff there has to be created an incidence child
	 * in the XMI file for <code>vertexClass</code>. Otherwise
	 * <code>false</code> is returned.
	 * 
	 * @param vertexClass
	 *            {@link VertexClass} to be checked
	 * @return boolean
	 */
  private boolean hasChildIncidence(VertexClass vertexClass) {
    if (vertexClass.getFirstEndsAtIncidence() == null) {
      return false;
    }
    for (EndsAt ea : vertexClass.getEndsAtIncidences()) {
      IncidenceClass ic = (IncidenceClass) ea.getThat();
      if (hasToBeCreatedAtVertex(ic)) {
        return true;
      }
    }
    return false;
  }

  /**
	 * Returns <code>true</code>, iff the <code>incidence</code> has to be
	 * created at the {@link VertexClass} VC, to which <code>incidence</code> is
	 * connected via an {@link EndsAt} edge. This is the case if
	 * <ul>
	 * <li>{@link SchemaGraph2XMI#isBidirectional} is set to <code>true</code>,</li>
	 * <li>{@link SchemaGraph2XMI#isReverted} is set to <code>false</code> and
	 * VC is the alpha {@link VertexClass} of the {@link EdgeClass} to which
	 * <code>incidence</code> belongs or</li>
	 * <li>{@link SchemaGraph2XMI#isReverted} is set to <code>true</code> and VC
	 * is the omega {@link VertexClass} of the {@link EdgeClass} to which
	 * <code>incidence</code> belongs.</li>
	 * </ul>
	 * Otherwise <code>false</code> is returned.
	 * 
	 * @param incidence
	 *            {@link IncidenceClass} to be checked
	 * @return boolean
	 */
  private boolean hasToBeCreatedAtVertex(IncidenceClass incidence) {
    boolean isAlphaVertexClass = incidence.getFirstComesFromIncidence() != null;
    if (isBidirectional) {
      return true;
    } else {
      if (isAlphaVertexClass) {
        return true;
      }
    }
    return false;
  }

  /**
	 * Creates the representation for the alpha and omega {@link IncidenceClass}
	 * of <code>edgeClass</code> if it wasn't already created at the
	 * corresponding {@link VertexClass} (e.g. if
	 * {@link SchemaGraph2XMI#hasToBeCreatedAtVertex(IncidenceClass)} returns
	 * <code>false</code>).
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param edgeClass
	 *            {@link EdgeClass} of which the {@link IncidenceClass}es have
	 *            to be represented.
	 * @throws XMLStreamException
	 */
  private void createIncidences(XMLStreamWriter writer, EdgeClass edgeClass) throws XMLStreamException {
    IncidenceClass alphaIncidence = (IncidenceClass) edgeClass.getFirstComesFromIncidence().getThat();
    IncidenceClass omegaIncidence = (IncidenceClass) edgeClass.getFirstGoesToIncidence().getThat();
    VertexClass alphaVertex = (VertexClass) alphaIncidence.getFirstEndsAtIncidence().getThat();
    VertexClass omegaVertex = (VertexClass) omegaIncidence.getFirstEndsAtIncidence().getThat();
    if (!hasToBeCreatedAtVertex(alphaIncidence)) {
      createIncidence(writer, omegaIncidence, edgeClass, alphaIncidence, omegaVertex, alphaVertex.get_qualifiedName(), true);
    }
    if (!hasToBeCreatedAtVertex(omegaIncidence)) {
      createIncidence(writer, alphaIncidence, edgeClass, omegaIncidence, alphaVertex, omegaVertex.get_qualifiedName(), true);
    }
  }

  /**
	 * Creates the representation of all {@link IncidenceClass}es which are
	 * connected to <code>vertexClass</code> if
	 * {@link SchemaGraph2XMI#hasToBeCreatedAtVertex(IncidenceClass)} returns
	 * <code>true</code>.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param vertexClass
	 *            {@link VertexClass} of which the connected
	 *            {@link IncidenceClass}es are represented
	 * @throws XMLStreamException
	 */
  private void createIncidences(XMLStreamWriter writer, VertexClass vertexClass) throws XMLStreamException {
    for (EndsAt ea : vertexClass.getEndsAtIncidences()) {
      IncidenceClass incidence = (IncidenceClass) ea.getThat();
      if (hasToBeCreatedAtVertex(incidence)) {
        boolean isVertexClassAlphaVertex = incidence.getFirstComesFromIncidence() != null;
        EdgeClass edgeClass = (EdgeClass) (isVertexClassAlphaVertex ? incidence.getFirstComesFromIncidence() : incidence.getFirstGoesToIncidence()).getThat();
        IncidenceClass otherIncidence = (IncidenceClass) (isVertexClassAlphaVertex ? edgeClass.getFirstGoesToIncidence() : edgeClass.getFirstComesFromIncidence()).getThat();
        VertexClass connectedVertexClass = (VertexClass) otherIncidence.getFirstEndsAtIncidence().getThat();
        createIncidence(writer, otherIncidence, edgeClass, incidence, connectedVertexClass, vertexClass.get_qualifiedName(), false);
      }
    }
  }

  /**
	 * Creates the representation of the {@link IncidenceClass} which contains
	 * the information for the {@link VertexClass} which is specified by
	 * <code>qualifiedNameOfVertexClass</code> e.g. <code>otherIncidence</code>.<br/>
	 * The relevant information are: <ui> <li>the rolename,</li> <li>the
	 * redefines information,</li> <li>the min value,</li> <li>the max value and
	 * </li> <li>composition or shared information.</li> </ui> The default
	 * rolename for associations is
	 * <code>qualifiedNameOfVertexClass_edgeClass.getQualifiedName()</code> and
	 * <code>""</code> for associationClasses.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param otherIncidence
	 *            {@link IncidenceClass} which contains the information for the
	 *            incidence corresponding to the {@link VertexClass} of
	 *            <code>qualifiedNameOfVertexClass</code> in the xmi file.
	 * @param connectedVertexClass
	 *            {@link VertexClass} at the other end of <code>edgeClass</code>
	 * @param incidence
	 *            {@link IncidenceClass} connected to the {@link VertexClass}
	 *            specified by <code>qualifiedNameOfVertexClass</code>
	 * @param edgeClass
	 *            {@link EdgeClass} which connects the {@link VertexClass}
	 *            specified by <code>qualifiedNameOfVertexClass</code> with
	 *            <code>connectedVertexClass</code>
	 * @param qualifiedNameOfVertexClass
	 *            {@link String} the qualified name of the {@link VertexClass}
	 *            to which <code>incidence</code> is connected to.
	 * @param createOwnedEnd
	 *            boolean if set to <code>true</code>, the tag ownedEnd is used
	 *            instead of ownedAttribute (ownedEnd has to be created, if you
	 *            create an incidence at an association.)
	 * @throws XMLStreamException
	 */
  private void createIncidence(XMLStreamWriter writer, IncidenceClass otherIncidence, EdgeClass edgeClass, IncidenceClass incidence, VertexClass connectedVertexClass, String qualifiedNameOfVertexClass, boolean createOwnedEnd) throws XMLStreamException {
    String incidenceId = qualifiedNameOfVertexClass + "_incidence_" + edgeClass.get_qualifiedName() + (incidence.getFirstComesFromIncidence() != null ? "_from" : "_to");
    int i = 0;
    for (Redefines red : otherIncidence.getRedefinesIncidences(EdgeDirection.OUT)) {
      createConstraint(writer, "redefines " + ((IncidenceClass) red.getThat()).get_roleName(), qualifiedNameOfVertexClass + "_redefines" + i + "_" + edgeClass.get_qualifiedName(), incidenceId);
    }
    writer.writeStartElement(createOwnedEnd ? XMIConstants4SchemaGraph2XMI.TAG_OWNEDEND : XMIConstants4SchemaGraph2XMI.TAG_OWNEDATTRIBUTE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_TYPE_VALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, incidenceId);
    if (otherIncidence.get_roleName() != null && !otherIncidence.get_roleName().isEmpty()) {
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, otherIncidence.get_roleName());
    } else {
      if (isRoleNameNecessary(edgeClass, connectedVertexClass.get_qualifiedName())) {
        String newRoleName = createNewUniqueRoleName(connectedVertexClass);
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, newRoleName);
        otherIncidence.set_roleName(newRoleName);
      }
    }
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_ATTRIBUTE_VISIBILITY, XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_VISIBILITY_VALUE_PRIVATE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_ATTRIBUTE_TYPE, connectedVertexClass.get_qualifiedName());
    if (otherIncidence.get_aggregation() == AggregationKind.SHARED) {
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_ATTRIBUTE_AGGREGATION, XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_ATTRIBUTE_AGGREGATION_VALUE_SHARED);
    } else {
      if (otherIncidence.get_aggregation() == AggregationKind.COMPOSITE) {
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_ATTRIBUTE_AGGREGATION, XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_ATTRIBUTE_AGGREGATION_VALUE_COMPOSITE);
      }
    }
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.PACKAGEDELEMENT_ATTRIBUTE_ASSOCIATION, edgeClass.get_qualifiedName());
    writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_UPPERVALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_LITERALUNLIMITEDNATURAL);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, incidenceId + "_uppervalue");
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_VALUE, otherIncidence.get_max() == Integer.MAX_VALUE ? "*" : Integer.toString(otherIncidence.get_max()));
    writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_LOWERVALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_LITERALINTEGER);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, incidenceId + "_lowervalue");
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_VALUE, incidence.get_min() == Integer.MAX_VALUE ? "*" : Integer.toString(otherIncidence.get_min()));
    writer.writeEndElement();
  }

  /**
	 * Find a new rolename which lets the new {@link Schema} still be
	 * consistent.
	 * 
	 * @param connectedVertexClass
	 *            {@link VertexClass} for which the new rolename should be
	 *            created
	 * @return String the new rolename
	 */
  private String createNewUniqueRoleName(VertexClass connectedVertexClass) {
    String baseRolename = connectedVertexClass.get_qualifiedName().replaceAll(Pattern.quote("."), "_");
    if (Character.isUpperCase(baseRolename.charAt(0))) {
      baseRolename = Character.toLowerCase(baseRolename.charAt(0)) + baseRolename.substring(1);
    }
    HashMap<String, Object> boundVars = new HashMap<String, Object>();
    boundVars.put("start", connectedVertexClass);
    int counter = 0;
    return baseRolename + (counter == 1 ? "" : counter);
  }

  /**
	 * Checks if the there have to be a rolename. If the {@link EdgeClass}
	 * <code>edgeClass</code> is bidirectional navigable both rolenames have to
	 * exist. If it is only navigable in one direction the navigable
	 * {@link IncidenceClass} has to have an existing rolename. The other not.
	 * 
	 * @param edgeClass
	 *            {@link EdgeClass}
	 * @param qualifiedName
	 *            {@link String} the qualified name of the {@link VertexClass}
	 *            for which the existing of a rolename should be checked
	 * @return boolean
	 */
  private boolean isRoleNameNecessary(EdgeClass edgeClass, String qualifiedName) {
    if (isBidirectional) {
      return true;
    } else {
      if (((VertexClass) ((IncidenceClass) edgeClass.getFirstGoesToIncidence().getThat()).getFirstEndsAtIncidence().getThat()).get_qualifiedName().equals(qualifiedName)) {
        return true;
      } else {
        return false;
      }
    }
  }

  private void createGeneralization(XMLStreamWriter writer, String id, String idOfSpecializedClass) throws XMLStreamException {
    writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_GENERALIZATION);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.GENERALIZATION_TYPE_VALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, id);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.GENERALIZATION_ATTRIBUTE_GENERAL, idOfSpecializedClass);
  }

  /**
	 * This method creates an <code>extension</code> tag. It is used in profile
	 * application and to create stereotypes of the form
	 * <code>&lt;&lt;keyValue&gt;&gt;</code>. In the first case a
	 * <code>references</code> child is created in the other a
	 * <code>details</code> child. To create the first one <code>nelement</code>
	 * has to be <code>null</code>.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param nelement
	 *            {@link NamedElement} if null <code>references</code> is
	 *            created otherwise the stereotype <code>keyValue</code>.
	 * @param keyValue
	 *            {@link String} the stereotype to be created
	 * @throws XMLStreamException
	 */
  private void createExtension(XMLStreamWriter writer, NamedElement nelement, String keyValue) throws XMLStreamException {
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_TAG_EXTENSION);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_EXTENDER, XMIConstants4SchemaGraph2XMI.NAMESPACE_ECORE);
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_EANNOTATIONS);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.EANNOTATIONS_TYPE_VALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, nelement != null ? nelement.get_qualifiedName() + "_" + XMIConstants4SchemaGraph2XMI.TAG_EANNOTATIONS : XMIConstants4SchemaGraph2XMI.TAG_EANNOTATIONS + System.currentTimeMillis());
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.EANNOTATIONS_ATTRIBUTE_SOURCE, XMIConstants4SchemaGraph2XMI.EANNOTATIONS_ATTRIBUTE_SOURCE_VALUE);
    if (nelement != null) {
      writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_DETAILS);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.DETAILS_ATTRIBUTE_TYPE_VALUE);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, nelement.get_qualifiedName() + "_" + XMIConstants4SchemaGraph2XMI.TAG_DETAILS);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.DETAILS_ATTRIBUTE_KEY, keyValue);
    } else {
      writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_REFERENCES);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.REFERENCES_TYPE_VALUE);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_HREF, XMIConstants4SchemaGraph2XMI.REFERENCES_HREF_VALUE);
    }
    writer.writeEndElement();
    writer.writeEndElement();
  }

  /**
	 * Creates the representation of all {@link Attribute}s of
	 * <code>aeclass</code>.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param aeclass
	 *            {@link AttributedElementClass} of which all {@link Attribute}s
	 *            are created
	 * @throws XMLStreamException
	 */
  private void createAttributes(XMLStreamWriter writer, AttributedElementClass aeclass) throws XMLStreamException {
    for (HasAttribute hasAttribute : aeclass.getHasAttributeIncidences()) {
      Attribute attribute = (Attribute) hasAttribute.getThat();
      createAttribute(writer, attribute.get_name(), attribute.get_defaultValue(), (Domain) attribute.getFirstHasDomainIncidence().getThat(), aeclass.get_qualifiedName() + "_" + ((Attribute) hasAttribute.getThat()).get_name());
    }
  }

  /**
	 * Creates the representation of an {@link Attribute}. The represented
	 * information are the name, the default value and the type. <br/>
	 * If the {@link Attribute} is of type LongDomain, a DoubleDomain, a
	 * CollectionDomain or a MapDomain the type is stored in
	 * {@link SchemaGraph2XMI#typesToBeDeclaredAtTheEnd} because there has to be
	 * created a representation in the primitive types package.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param attributeName
	 *            {@link String} the name of the current {@link Attribute}
	 * @param defaultValue
	 *            {@link String} the default value of the current
	 *            {@link Attribute}
	 * @param domain
	 *            {@link Domain} of the current {@link Attribute}
	 * @param id
	 *            {@link String} the id of the tag which represents the current
	 *            {@link Attribute}
	 * @throws XMLStreamException
	 */
  private void createAttribute(XMLStreamWriter writer, String attributeName, String defaultValue, Domain domain, String id) throws XMLStreamException {
    boolean hasDefaultValue = defaultValue != null && !defaultValue.isEmpty();
    if (!hasDefaultValue && !(domain instanceof BooleanDomain || domain instanceof IntegerDomain || domain instanceof StringDomain)) {
      writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_OWNEDATTRIBUTE);
    } else {
      writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_OWNEDATTRIBUTE);
    }
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_TYPE_VALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, id);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, attributeName);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_ATTRIBUTE_VISIBILITY, XMIConstants4SchemaGraph2XMI.OWNEDATTRIBUTE_VISIBILITY_VALUE_PRIVATE);
    if (domain instanceof BooleanDomain || domain instanceof IntegerDomain || domain instanceof StringDomain) {
      createType(writer, domain);
    } else {
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, domain.get_qualifiedName().replaceAll("\\s", "").replaceAll("<", "_").replaceAll(">", "_"));
    }
    if (hasDefaultValue) {
      createDefaultValue(writer, defaultValue, id, domain);
    }
    if (hasDefaultValue || domain instanceof BooleanDomain || domain instanceof IntegerDomain || domain instanceof StringDomain) {
      writer.writeEndElement();
    }
    if (domain instanceof LongDomain || domain instanceof DoubleDomain || domain instanceof CollectionDomain || domain instanceof MapDomain) {
      typesToBeDeclaredAtTheEnd.add(domain);
    }
  }

  /**
	 * Creates the default values.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param defaultValue
	 *            {@link String} the default value of the current
	 *            {@link Attribute}
	 * @param id
	 *            {@link String} the id of the current {@link Attribute}
	 * @param domain
	 *            {@link Domain} the type of the current {@link Attribute}
	 * @throws XMLStreamException
	 */
  private void createDefaultValue(XMLStreamWriter writer, String defaultValue, String id, Domain domain) throws XMLStreamException {
    if (domain instanceof LongDomain || domain instanceof EnumDomain) {
      writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_DEFAULTVALUE);
    } else {
      writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_DEFAULTVALUE);
    }
    if (domain instanceof BooleanDomain) {
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_LITERALBOOLEAN);
    } else {
      if (domain instanceof IntegerDomain || domain instanceof LongDomain) {
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_LITERALINTEGER);
      } else {
        if (domain instanceof EnumDomain) {
          writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_INSTANCEVALUE);
        } else {
          writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_OPAQUEEXPRESSION);
        }
      }
    }
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, id + "_defaultValue");
    if (domain instanceof BooleanDomain || domain instanceof IntegerDomain || domain instanceof LongDomain || domain instanceof EnumDomain) {
      if (domain instanceof BooleanDomain) {
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_VALUE, defaultValue.equals("t") ? "true" : "false");
      } else {
        if (domain instanceof EnumDomain) {
          writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_NAME, defaultValue);
          writer.writeAttribute(XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, domain.get_qualifiedName());
          writer.writeAttribute(XMIConstants4SchemaGraph2XMI.DEFAULTVALUE_ATTRIBUTE_INSTANCE, domain.get_qualifiedName() + "_" + defaultValue);
        } else {
          writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_VALUE, defaultValue);
        }
      }
      if (domain instanceof BooleanDomain || domain instanceof IntegerDomain) {
        createType(writer, domain);
      }
    } else {
      if (domain instanceof StringDomain) {
        createType(writer, domain);
      } else {
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, domain.get_qualifiedName().replaceAll("\\s", "").replaceAll("<", "_").replaceAll(">", "_"));
      }
      writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_BODY);
      writer.writeCharacters(defaultValue);
      writer.writeEndElement();
    }
    if (!(domain instanceof LongDomain || domain instanceof EnumDomain)) {
      writer.writeEndElement();
    }
  }

  /**
	 * Creates the <code>type</code> tag used for attributes and default values.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param domain
	 *            {@link Domain} the type of the current {@link Attribute}
	 * @throws XMLStreamException
	 */
  private void createType(XMLStreamWriter writer, Domain domain) throws XMLStreamException {
    writer.writeEmptyElement(XMIConstants4SchemaGraph2XMI.TAG_TYPE);
    if (domain instanceof BooleanDomain) {
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_PRIMITIVETYPE);
      writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_HREF, XMIConstants4SchemaGraph2XMI.TYPE_HREF_VALUE_BOOLEAN);
    } else {
      if (domain instanceof IntegerDomain) {
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_PRIMITIVETYPE);
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_HREF, XMIConstants4SchemaGraph2XMI.TYPE_HREF_VALUE_INTEGER);
      } else {
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_PRIMITIVETYPE);
        writer.writeAttribute(XMIConstants4SchemaGraph2XMI.ATTRIBUTE_HREF, XMIConstants4SchemaGraph2XMI.TYPE_HREF_VALUE_STRING);
      }
    }
  }

  /**
	 * Creates the representation of all {@link Constraint}s attached to
	 * <code>aeclass</code>.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param aeclass
	 *            {@link AttributedElementClass} of which the {@link Constraint}
	 *            s should be created
	 * @throws XMLStreamException
	 */
  private void createConstraints(XMLStreamWriter writer, AttributedElementClass aeclass) throws XMLStreamException {
    int uniqueNumber = 0;
    for (HasConstraint hasConstraint : aeclass.getHasConstraintIncidences()) {
      createConstraint(writer, (Constraint) hasConstraint.getThat(), aeclass.get_qualifiedName() + "_" + XMIConstants4SchemaGraph2XMI.TAG_OWNEDRULE + uniqueNumber++, aeclass.get_qualifiedName());
    }
  }

  /**
	 * Creates the representation of <code>constraint</code>.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param constraint
	 *            {@link Constraint} to be represented
	 * @param id
	 *            {@link String} the id of the created <code>ownedRule</code>
	 *            tag, which represents <code>constraint</code>
	 * @param constrainedElement
	 *            {@link String} qualified name of the
	 *            {@link AttributedElementClass} which is constrained
	 * @throws XMLStreamException
	 */
  private void createConstraint(XMLStreamWriter writer, Constraint constraint, String id, String constrainedElement) throws XMLStreamException {
    createConstraint(writer, "\"" + constraint.get_message() + "\" \"" + constraint.get_predicateQuery() + "\" \"" + constraint.get_offendingElementsQuery() + "\"", id, constrainedElement);
  }

  /**
	 * Creates an constraint with the content <code>constraintContent</code>.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param constraintContent
	 *            {@link String} the content of the constraint which will be
	 *            created
	 * @param id
	 *            {@link String} the id of the created <code>ownedRule</code>
	 *            tag, which represents <code>constraint</code>
	 * @param constrainedElement
	 *            {@link String} qualified name of the
	 *            {@link AttributedElementClass} which is constrained
	 * @throws XMLStreamException
	 */
  private void createConstraint(XMLStreamWriter writer, String constraintContent, String id, String constrainedElement) throws XMLStreamException {
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_OWNEDRULE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.OWNEDRULE_TYPE_VALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, id);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.OWNEDRULE_ATTRIBUTE_CONSTRAINEDELEMENT, constrainedElement);
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_SPECIFICATION);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.TYPE_VALUE_OPAQUEEXPRESSION);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, id + "_" + XMIConstants4SchemaGraph2XMI.TAG_SPECIFICATION);
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_LANGUAGE);
    writer.writeEndElement();
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_BODY);
    writer.writeCharacters(constraintContent);
    writer.writeEndElement();
    writer.writeEndElement();
    writer.writeEndElement();
  }

  /**
	 * Creates the representation of all {@link Comment}s attached to
	 * <code>nelement</code>.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param nelement
	 *            {@link NamedElement} of which the {@link Comment}s should be
	 *            created
	 * @throws XMLStreamException
	 */
  private void createComments(XMLStreamWriter writer, NamedElement nelement) throws XMLStreamException {
    int uniqueNumber = 0;
    for (Annotates annotates : nelement.getAnnotatesIncidences()) {
      createComment(writer, (Comment) annotates.getThat(), nelement.get_qualifiedName() + "_" + XMIConstants4SchemaGraph2XMI.TAG_OWNEDCOMMENT + uniqueNumber++, nelement.get_qualifiedName());
    }
  }

  /**
	 * Creates the representation of one comment.
	 * 
	 * @param writer
	 *            {@link XMLStreamWriter} of the current XMI file
	 * @param comment
	 *            {@link Comment} which should be represented
	 * @param id
	 *            {@link String} the id of the created <code>ownedComment</code>
	 *            tag, which represents <code>comment</code>
	 * @param annotatedElement
	 *            {@link String} qualified name of the {@link NamedElement}
	 *            which is commented
	 * @throws XMLStreamException
	 */
  private void createComment(XMLStreamWriter writer, Comment comment, String id, String annotatedElement) throws XMLStreamException {
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_OWNEDCOMMENT);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_TYPE, XMIConstants4SchemaGraph2XMI.OWNEDCOMMENT_TYPE_VALUE);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.NAMESPACE_XMI, XMIConstants4SchemaGraph2XMI.XMI_ATTRIBUTE_ID, id);
    writer.writeAttribute(XMIConstants4SchemaGraph2XMI.OWNEDCOMMENT_ATTRIBUTE_ANNOTATEDELEMENT, annotatedElement);
    writer.writeStartElement(XMIConstants4SchemaGraph2XMI.TAG_BODY);
    writer.writeCharacters(XMIConstants4SchemaGraph2XMI.COMMENT_START + comment.get_text().replaceAll(Pattern.quote("\n"), XMIConstants4SchemaGraph2XMI.COMMENT_NEWLINE) + XMIConstants4SchemaGraph2XMI.COMMENT_END);
    writer.writeEndElement();
    writer.writeEndElement();
  }

  /**
	 * Extracts the simple name out of <code>qualifiedName</code>.
	 * 
	 * @param qualifiedName
	 *            {@link String} the qualified name of which the simple name
	 *            should be extracted
	 * @return {@link String} the extracted simple name
	 */
  private String extractSimpleName(String qualifiedName) {
    int lastIndexOfDot = qualifiedName.lastIndexOf('.');
    if (lastIndexOfDot >= 0) {
      return qualifiedName.substring(lastIndexOfDot + 1);
    } else {
      return qualifiedName;
    }
  }
}