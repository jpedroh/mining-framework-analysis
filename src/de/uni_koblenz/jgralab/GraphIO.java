package de.uni_koblenz.jgralab;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.graphmarker.BooleanGraphMarker;
import de.uni_koblenz.jgralab.impl.GraphBaseImpl;
import de.uni_koblenz.jgralab.impl.InternalGraph;
import de.uni_koblenz.jgralab.impl.db.GraphDatabase;
import de.uni_koblenz.jgralab.impl.db.GraphDatabaseException;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.NamedElement;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.impl.BasicDomainImpl;
import de.uni_koblenz.jgralab.schema.impl.ConstraintImpl;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;
import de.uni_koblenz.jgralab.schema.impl.compilation.SchemaClassManager;

/**
 * class for loading and storing schema and graphs in tg format
 * 
 * @author ist@uni-koblenz.de
 */
public class GraphIO {
  /**
	 * TG File Version this GraphIO recognizes.
	 */
  public static final int TGFILE_VERSION = 2;

  public static final String NULL_LITERAL = "n";

  public static final String TRUE_LITERAL = "t";

  public static final String FALSE_LITERAL = "f";

  public static final String TGRAPH_FILE_EXTENSION = ".tg";

  public static final String TGRAPH_COMPRESSED_FILE_EXTENSION = ".tg.gz";

  public static class TGFilenameFilter extends javax.swing.filechooser.FileFilter implements FilenameFilter {
    private static TGFilenameFilter instance;

    private TGFilenameFilter() {
    }

    public static TGFilenameFilter instance() {
      if (instance == null) {
        instance = new TGFilenameFilter();
      }
      return instance;
    }

    @Override public boolean accept(File dir, String name) {
      if (name.matches(".+\\.[Tt][Gg](\\.[Gg][Zz])?$")) {
        return true;
      }
      return false;
    }

    @Override public boolean accept(File f) {
      return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      f.isDirectory() || this.accept(f, f.getName())
=======
      f.isDirectory() || accept(f, f.getName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }

    @Override public String getDescription() {
      return "TG Files";
    }
  }

  protected static final int BUFFER_SIZE = 65536;

  protected InputStream TGIn;

  private DataOutputStream TGOut;

  protected Schema schema;

  /**
	 * Maps domain names to the respective Domains.
	 */
  private final Map<String, Domain> domains;

  /**
	 * Maps GraphElementClasses to their containing GraphClasses
	 */
  protected final Map<GraphElementClass<?, ?>, GraphClass> GECsearch;

  private int line;

  private int la;

  private String lookAhead;

  private boolean isUtfString;

  private boolean writeSpace;

  private String gcName;

  private final byte buffer[];

  private int bufferPos;

  private int bufferSize;

  private Vertex edgeIn[], edgeOut[];

  private int[] firstIncidence;

  private int[] nextIncidence;

  private int edgeOffset;

  /**
	 * Buffers the parsed data of enum domains prior to their creation in
	 * JGraLab.
	 */
  private final Set<EnumDomainData> enumDomainBuffer;

  /**
	 * Buffers the parsed data of record domains prior to their creation in
	 * JGraLab.
	 */
  private List<RecordDomainData> recordDomainBuffer;

  /**
	 * Buffers the parsed data of the graph class prior to its creation in
	 * JGraLab.
	 */
  private GraphClassData graphClass;

  /**
	 * Buffers the parsed data of vertex classes prior to their creation in
	 * JGraLab.
	 */
  private final Map<String, List<GraphElementClassData>> vertexClassBuffer;

  /**
	 * Buffers the parsed data of edge classes prior to their creation in
	 * JGraLab.
	 */
  protected final Map<String, List<GraphElementClassData>> edgeClassBuffer;

  private final Map<String, List<String>> commentData;

  private int putBackChar;

  private String currentPackageName;

  private ByteArrayOutputStream BAOut;

  private final HashMap<String, String> stringPool;

  private GraphFactory graphFactory;

  protected GraphIO() {
    this.domains = new TreeMap<String, Domain>();
    this.GECsearch = new HashMap<GraphElementClass<?, ?>, GraphClass>();
    this.buffer = new byte[BUFFER_SIZE];
    this.bufferPos = 0;
    this.enumDomainBuffer = new HashSet<EnumDomainData>();
    this.recordDomainBuffer = new ArrayList<RecordDomainData>();
    this.graphClass = null;
    this.vertexClassBuffer = new TreeMap<String, List<GraphElementClassData>>();
    this.edgeClassBuffer = new TreeMap<String, List<GraphElementClassData>>();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.commentData = new HashMap<String, List<String>>()
=======
    commentData = new HashMap<String, List<String>>()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.stringPool = new HashMap<String, String>()
=======
    stringPool = new HashMap<String, String>()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.putBackChar = -1
=======
    putBackChar = -1
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
  }

  public static Schema loadSchemaFromFile(String filename) throws GraphIOException {
    InputStream in = null;
    try {
      if (filename.toLowerCase().endsWith(".gz")) {
        in = new GZIPInputStream(new FileInputStream(filename), BUFFER_SIZE);
      } else {
        in = new BufferedInputStream(new FileInputStream(filename), BUFFER_SIZE);
      }
      return loadSchemaFromStream(in);
    } catch (IOException ex) {
      throw new GraphIOException("Exception while loading schema from " + filename, ex);
    } finally {
      close(in);
    }
  }

  public static Schema loadSchemaFromStream(InputStream in) throws GraphIOException {
    try {
      GraphIO io = new GraphIO();
      io.TGIn = in;
      io.tgfile();
      io.schema.finish();
      return io.schema;
    } catch (Exception e) {
      throw new GraphIOException("Exception while loading schema.", e);
    }
  }

  public static Schema loadSchemaFromDatabase(GraphDatabase graphDatabase, String packagePrefix, String schemaName) throws GraphIOException {
    String definition = graphDatabase.getSchemaDefinition(packagePrefix, schemaName);
    InputStream input = new ByteArrayInputStream(definition.getBytes());
    return loadSchemaFromStream(input);
  }

  public static void loadSchemaIntoGraphDatabase(String filePath, GraphDatabase graphDatabase) throws IOException, GraphIOException, SQLException {
    Schema schema = loadSchemaFromFile(filePath);
    graphDatabase.insertSchema(schema);
  }

  /**
	 * Saves the specified <code>schema</code> to the file named
	 * <code>filename</code>. When the <code>filename</code> ends with
	 * <code>.gz</code>, output will be GZIP compressed, otherwise uncompressed
	 * plain text.
	 * 
	 * @param schema
	 *            a schema
	 * @param filename
	 *            the name of the file
	 * @throws GraphIOException
	 *             if an IOException occurs
	 */
  public static void saveSchemaToFile(Schema schema, String filename) throws GraphIOException {
    DataOutputStream out = null;
    try {
      out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(filename))));
      saveSchemaToStream(schema, out);
    } catch (IOException ex) {
      throw new GraphIOException("Exception while saving schema to " + filename, ex);
    } finally {
      close(out);
    }
  }

  /**
	 * Saves the specified <code>schema</code> to the stream <code>out</code>.
	 * The stream is <em>not</em> closed.
	 * 
	 * @param schema
	 *            a schema
	 * @param out
	 *            a DataOutputStream
	 * @throws GraphIOException
	 *             if an IOException occurs
	 */
  public static void saveSchemaToStream(Schema schema, DataOutputStream out) throws GraphIOException {
    GraphIO io = new GraphIO();
    io.TGOut = out;
    try {
      io.saveHeader();
      io.saveSchema(schema);
      out.flush();
    } catch (IOException e) {
      throw new GraphException("Exception while saving schema", e);
    }
  }

  private void saveSchema(Schema s) throws IOException {
    this.schema = s;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.write("Schema");
=======
    write("Schema");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    this.space();
    this.writeIdentifier(this.schema.getQualifiedName());

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.write(";\n");
=======
    write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    GraphClass gc = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.schema.getGraphClass()
=======
    schema.getGraphClass()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.write("GraphClass");
=======
    write("GraphClass");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.space();
=======
    space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.writeIdentifier(gc.getSimpleName())
=======
    writeIdentifier(gc.getSimpleName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.writeAttributes(null, gc);
=======
    writeAttributes(null, gc);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.writeConstraints(gc);
=======
    writeConstraints(gc);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.write(";\n")
=======
    write(";\n")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.writeComments(gc, gc.getSimpleName());
=======
    writeComments(gc, gc.getSimpleName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    Queue<de.uni_koblenz.jgralab.schema.Package> worklist = new LinkedList<de.uni_koblenz.jgralab.schema.Package>();
    worklist.offer(s.getDefaultPackage());
    while (!worklist.isEmpty()) {
      Package pkg = worklist.poll();
      worklist.addAll(pkg.getSubPackages().values());
      if (!pkg.isDefaultPackage()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write("Package")
=======
        write("Package")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        this.space();
        this.writeIdentifier(pkg.getQualifiedName());

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(";\n")
=======
        write(";\n")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
      }
      for (Domain dom : pkg.getDomains().values()) {
        if (dom instanceof EnumDomain) {
          EnumDomain ed = (EnumDomain) dom;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write("EnumDomain")
=======
          write("EnumDomain")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
          this.space();
          this.writeIdentifier(ed.getSimpleName());

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(" (")
=======
          write(" (")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
          for (Iterator<String> eit = ed.getConsts().iterator(); eit.hasNext(); ) {
            this.space();
            this.writeIdentifier(eit.next());
            if (eit.hasNext()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.write(",")
=======
              write(",")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;
            }
          }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(" );\n");
=======
          write(" );\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.writeComments(ed, ed.getSimpleName())
=======
          writeComments(ed, ed.getSimpleName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        } else {
          if (dom instanceof RecordDomain) {
            RecordDomain rd = (RecordDomain) dom;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.write("RecordDomain")
=======
            write("RecordDomain")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
            this.space();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.writeIdentifier(rd.getSimpleName())
=======
            writeIdentifier(rd.getSimpleName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
            String delim = " ( ";
            for (RecordComponent rdc : rd.getComponents()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.write(delim)
=======
              write(delim)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;
              this.noSpace();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.writeIdentifier(rdc.getName())
=======
              writeIdentifier(rdc.getName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.write(": ")
=======
              write(": ")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.write(rdc.getDomain().getTGTypeName(pkg))
=======
              write(rdc.getDomain().getTGTypeName(pkg))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;
              delim = ", ";
            }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.write(" );\n");
=======
            write(" );\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.writeComments(rd, rd.getSimpleName())
=======
            writeComments(rd, rd.getSimpleName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
          }
        }
      }
      for (VertexClass vc : pkg.getVertexClasses().values()) {
        if (vc.isInternal()) {
          continue;
        }
        if (vc.isAbstract()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write("abstract ")
=======
          write("abstract ")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write("VertexClass");
=======
        write("VertexClass");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        this.space();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeIdentifier(vc.getSimpleName())
=======
>>>>>>> Unknown file: This is a bug in JDime.
        ;
        this.writeHierarchy(pkg, vc);
        this.writeAttributes(pkg, vc);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeConstraints(vc);
=======
        writeConstraints(vc);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(";\n");
=======
        write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeComments(vc, vc.getSimpleName())
=======
        writeComments(vc, vc.getSimpleName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
      }
      for (EdgeClass ec : pkg.getEdgeClasses().values()) {
        if (ec.isInternal()) {
          continue;
        }
        if (ec.isAbstract()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write("abstract ")
=======
          write("abstract ")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write("EdgeClass");
=======
        write("EdgeClass");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        this.space();
        this.writeIdentifier(ec.getSimpleName());
        this.writeHierarchy(pkg, ec);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(" from")
=======
        write(" from")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        this.space();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeIdentifier(ec.getFrom().getVertexClass().getQualifiedName(pkg))
=======
        writeIdentifier(ec.getFrom().getVertexClass().getQualifiedName(pkg))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(" (")
=======
        write(" (")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(ec.getFrom().getMin() + ",")
=======
        write(ec.getFrom().getMin() + ",")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        if (ec.getFrom().getMax() == Integer.MAX_VALUE) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write("*)")
=======
          write("*)")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(ec.getFrom().getMax() + ")")
=======
          write(ec.getFrom().getMax() + ")")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        }
        if (!ec.getFrom().getRolename().equals("")) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(" role")
=======
          write(" role")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
          this.space();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.writeIdentifier(ec.getFrom().getRolename())
=======
          writeIdentifier(ec.getFrom().getRolename())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
          String delim = " redefines";
          for (String redefinedRolename : ec.getFrom().getRedefinedRoles()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.write(delim)
=======
            write(delim)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
            delim = ",";
            this.space();
            this.writeIdentifier(redefinedRolename);
          }
        }
        switch (ec.getFrom().getAggregationKind()) {
          case NONE:
          break;
          case SHARED:

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(" aggregation shared");
=======
          write(" aggregation shared");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          break;
          case COMPOSITE:

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(" aggregation composite");
=======
          write(" aggregation composite");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          break;
        }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(" to")
=======
        write(" to")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        this.space();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeIdentifier(ec.getTo().getVertexClass().getQualifiedName(pkg))
=======
        writeIdentifier(ec.getTo().getVertexClass().getQualifiedName(pkg))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(" (")
=======
        write(" (")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(ec.getTo().getMin() + ",")
=======
        write(ec.getTo().getMin() + ",")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        if (ec.getTo().getMax() == Integer.MAX_VALUE) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write("*)")
=======
          write("*)")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(ec.getTo().getMax() + ")")
=======
          write(ec.getTo().getMax() + ")")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        }
        if (!ec.getTo().getRolename().equals("")) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(" role")
=======
          write(" role")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
          this.space();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.writeIdentifier(ec.getTo().getRolename())
=======
          writeIdentifier(ec.getTo().getRolename())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
          String delim = " redefines";
          for (String redefinedRolename : ec.getTo().getRedefinedRoles()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.write(delim)
=======
            write(delim)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
            delim = ",";
            this.space();
            this.writeIdentifier(redefinedRolename);
          }
        }
        switch (ec.getTo().getAggregationKind()) {
          case NONE:
          break;
          case SHARED:

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(" aggregation shared");
=======
          write(" aggregation shared");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          break;
          case COMPOSITE:

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(" aggregation composite");
=======
          write(" aggregation composite");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          break;
        }
        this.writeAttributes(pkg, ec);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeConstraints(ec);
=======
        writeConstraints(ec);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(";\n");
=======
        write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeComments(ec, ec.getSimpleName())
=======
        writeComments(ec, ec.getSimpleName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
      }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.writeComments(pkg, "." + pkg.getQualifiedName());
=======
      writeComments(pkg, "." + pkg.getQualifiedName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    }
  }

  private void writeComments(NamedElement elem, String name) throws IOException {
    if (!elem.getComments().isEmpty()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.write("Comment");
=======
      write("Comment");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.space();
=======
      space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.writeIdentifier(name);
=======
      writeIdentifier(name);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.space();
=======
      space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      for (String c : elem.getComments()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeUtfString(c);
=======
        writeUtfString(c);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.write(";\n");
=======
      write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    }
  }

  private void writeConstraints(AttributedElementClass<?, ?> aec) throws IOException {
    for (Constraint c : aec.getConstraints()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.writeSpace();
=======
      writeSpace();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.write("[");
=======
      write("[");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.noSpace();
=======
      noSpace();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.writeUtfString(c.getMessage());
=======
      writeUtfString(c.getMessage());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.writeUtfString(c.getPredicate());
=======
      writeUtfString(c.getPredicate());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      if (c.getOffendingElementsQuery() != null) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeUtfString(c.getOffendingElementsQuery());
=======
        writeUtfString(c.getOffendingElementsQuery());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.noSpace();
=======
      noSpace();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.write("]");
=======
      write("]");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.space();
=======
      space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    }
  }

  /**
	 * Saves the specified <code>graph</code> to the file named
	 * <code>filename</code>. When the <code>filename</code> ends with
	 * <code>.gz</code>, output will be GZIP compressed, otherwise uncompressed
	 * plain text. A {@link ProgressFunction} <code>pf</code> can be used to
	 * monitor progress.
	 * 
	 * @param graph
	 *            a graph
	 * @param filename
	 *            the name of the TG file to be written
	 * @param pf
	 *            a {@link ProgressFunction}, may be <code>null</code>
	 * @throws GraphIOException
	 *             if an IOException occurs
	 */
  public static void saveGraphToFile(Graph graph, String filename, ProgressFunction pf) throws GraphIOException {
    DataOutputStream out = null;
    try {
      if (filename.toLowerCase().endsWith(".gz")) {
        out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(filename), BUFFER_SIZE));
      } else {
        out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename), BUFFER_SIZE));
      }
      saveGraphToStream(graph, out, pf);
    } catch (IOException ex) {
      throw new GraphIOException("Exception while saving graph to " + filename, ex);
    } finally {
      close(out);
    }
  }

  /**
	 * Saves the marked <code>subGraph</code> to the file named
	 * <code>filename</code>. A {@link ProgressFunction} <code>pf</code> can be
	 * used to monitor progress. The stream is <em>not</em> closed. This method
	 * does <i>not</i> check if the subgraph marker is complete.
	 * 
	 * @param subGraph
	 *            a BooleanGraphMarker denoting the subgraph to be saved
	 * @param filename
	 *            a filename
	 * @param pf
	 *            a {@link ProgressFunction}, may be <code>null</code>
	 * @throws GraphIOException
	 *             if an IOException occurs
	 */
  public static void saveGraphToFile(BooleanGraphMarker subGraph, String filename, ProgressFunction pf) throws GraphIOException {
    DataOutputStream out = null;
    try {
      if (filename.toLowerCase().endsWith(".gz")) {
        out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(filename), BUFFER_SIZE));
      } else {
        out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename), BUFFER_SIZE));
      }
      saveGraphToStream(subGraph, out, pf);
    } catch (IOException e) {
      throw new GraphIOException("Exception while saving graph to " + filename, e);
    } finally {
      close(out);
    }
  }

  /**
	 * Saves the specified <code>graph</code> to the stream <code>out</code>. A
	 * {@link ProgressFunction} <code>pf</code> can be used to monitor progress.
	 * The stream is <em>not</em> closed.
	 * 
	 * @param graph
	 *            a graph
	 * @param out
	 *            a DataOutputStream
	 * @param pf
	 *            a {@link ProgressFunction}, may be <code>null</code>
	 * @throws GraphIOException
	 *             if an IOException occurs
	 */
  public static void saveGraphToStream(Graph graph, DataOutputStream out, ProgressFunction pf) throws GraphIOException {
    try {
      GraphIO io = new GraphIO();
      io.TGOut = out;
      io.saveGraph((InternalGraph) graph, pf, null);
      out.flush();
    } catch (IOException e) {
      throw new GraphIOException("Exception while saving graph", e);
    }
  }

  /**
	 * Saves the marked <code>subGraph</code> to the stream <code>out</code>. A
	 * {@link ProgressFunction} <code>pf</code> can be used to monitor progress.
	 * The stream is <em>not</em> closed. This method does <i>not</i> check if
	 * the subgraph marker is complete.
	 * 
	 * @param out
	 *            a DataOutputStream
	 * @param subGraph
	 *            a BooleanGraphMarker denoting the subgraph to be saved
	 * @param pf
	 *            a {@link ProgressFunction}, may be <code>null</code>
	 * @throws GraphIOException
	 *             if an IOException occurs
	 */
  public static void saveGraphToStream(BooleanGraphMarker subGraph, DataOutputStream out, ProgressFunction pf) throws GraphIOException {
    try {
      GraphIO io = new GraphIO();
      io.TGOut = out;
      io.saveGraph((InternalGraph) subGraph.getGraph(), pf, subGraph);
      out.flush();
    } catch (IOException e) {
      throw new GraphIOException("Exception while saving graph", e);
    }
  }

  private void saveGraph(InternalGraph graph, ProgressFunction pf, BooleanGraphMarker subGraph) throws IOException, GraphIOException {
    TraversalContext tc = graph.setTraversalContext(null);
    try {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.saveHeader();
=======
      saveHeader();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.schema = graph.getSchema()
=======
      schema = graph.getSchema()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.saveSchema(this.schema);
=======
      saveSchema(schema);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      long eId;
      long vId;
      long graphElements = 0, currentCount = 0, interval = 1;
      if (pf != null) {
        if (subGraph != null) {
          pf.init(subGraph.size());
        } else {
          pf.init(graph.getVCount() + graph.getECount());
        }
        interval = pf.getUpdateInterval();
      }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.space();
=======
      space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.write("Graph " + toUtfString(graph.getId()) + " " + graph.getGraphVersion());
=======
      write("Graph " + toUtfString(graph.getId()) + " " + graph.getGraphVersion());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.writeIdentifier(graph.getAttributedElementClass().getQualifiedName());
=======
      writeIdentifier(graph.getAttributedElementClass().getQualifiedName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      int vCount = graph.getVCount();
      int eCount = graph.getECount();
      if (subGraph != null) {
        vCount = 0;
        eCount = 0;
        for (AttributedElement<?, ?> ae : subGraph.getMarkedElements()) {
          if (ae instanceof Vertex) {
            vCount++;
          } else {
            if (ae instanceof Edge) {
              eCount++;
            }
          }
        }
      }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.write(" (" + graph.getMaxVCount() + " " + graph.getMaxECount() + " " + vCount + " " + eCount + ")");
=======
      write(" (" + graph.getMaxVCount() + " " + graph.getMaxECount() + " " + vCount + " " + eCount + ")");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.space();
=======
      space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      graph.writeAttributeValues(this);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.write(";\n");
=======
      write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      Package oldPackage = null;
      Vertex nextV = graph.getFirstVertex();
      while (nextV != null) {
        if ((subGraph != null) && !subGraph.isMarked(nextV)) {
          nextV = nextV.getNextVertex();
          continue;
        }
        vId = nextV.getId();
        AttributedElementClass<?, ?> aec = nextV.getAttributedElementClass();
        Package currentPackage = aec.getPackage();
        if (currentPackage != oldPackage) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write("Package");
=======
          write("Package");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.space();
=======
          space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.writeIdentifier(currentPackage.getQualifiedName());
=======
          writeIdentifier(currentPackage.getQualifiedName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(";\n");
=======
          write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          oldPackage = currentPackage;
        }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(Long.toString(vId));
=======
        write(Long.toString(vId));
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.space();
=======
        space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeIdentifier(aec.getSimpleName());
=======
        writeIdentifier(aec.getSimpleName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        Edge nextI = nextV.getFirstIncidence();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(" <");
=======
        write(" <");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.noSpace();
=======
        noSpace();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        while (nextI != null) {
          if ((subGraph != null) && !subGraph.isMarked(nextI)) {
            nextI = nextI.getNextIncidence();
            continue;
          }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.writeLong(nextI.getId());
=======
          writeLong(nextI.getId());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          nextI = nextI.getNextIncidence();
        }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(">");
=======
        write(">");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.space();
=======
        space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        nextV.writeAttributeValues(this);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(";\n");
=======
        write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        nextV = nextV.getNextVertex();
        if (pf != null) {
          graphElements++;
          currentCount++;
          if (currentCount == interval) {
            pf.progress(graphElements);
            currentCount = 0;
          }
        }
      }
      Edge nextE = graph.getFirstEdge();
      while (nextE != null) {
        if ((subGraph != null) && !subGraph.isMarked(nextE)) {
          nextE = nextE.getNextEdge();
          continue;
        }
        eId = nextE.getId();
        AttributedElementClass<?, ?> aec = nextE.getAttributedElementClass();
        Package currentPackage = aec.getPackage();
        if (currentPackage != oldPackage) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write("Package");
=======
          write("Package");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.space();
=======
          space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.writeIdentifier(currentPackage.getQualifiedName());
=======
          writeIdentifier(currentPackage.getQualifiedName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.write(";\n");
=======
          write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          oldPackage = currentPackage;
        }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(Long.toString(eId));
=======
        write(Long.toString(eId));
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.space();
=======
        space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeIdentifier(aec.getSimpleName());
=======
        writeIdentifier(aec.getSimpleName());
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.space();
=======
        space();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        nextE.writeAttributeValues(this);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(";\n");
=======
        write(";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        nextE = nextE.getNextEdge();
        if (pf != null) {
          graphElements++;
          currentCount++;
          if (currentCount == interval) {
            pf.progress(graphElements);
            currentCount = 0;
          }
        }
      }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.TGOut.flush()
=======
      TGOut.flush()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      if (pf != null) {
        pf.finished();
      }
    }  finally {
      graph.setTraversalContext(tc);
    }
  }

  private void saveHeader() throws IOException {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.write(JGraLab.getVersionInfo(true));
=======
    write(JGraLab.getVersionInfo(true));
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.write("TGraph " + TGFILE_VERSION + ";\n");
=======
    write("TGraph " + TGFILE_VERSION + ";\n");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
  }

  private void writeHierarchy(Package pkg, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
  AttributedElementClass
=======
  GraphElementClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
  <?, ?> aec) throws IOException {
    String delim = ":";
    for (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    AttributedElementClass
=======
    GraphElementClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    <?, ?> superClass : aec.getDirectSuperClasses()) {
      if (!superClass.isInternal()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.write(delim)
=======
        write(delim)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        this.space();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.writeIdentifier(superClass.getQualifiedName(pkg))
=======
        writeIdentifier(superClass.getQualifiedName(pkg))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        delim = ",";
      }
    }
  }

  private void writeAttributes(Package pkg, AttributedElementClass<?, ?> aec) throws IOException {
    List<Attribute> attributes = aec.getOwnAttributeList();
    if (attributes.isEmpty()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.write(" {");
=======
      return;
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    for (Iterator<Attribute> ait = aec.getOwnAttributeList().iterator(); ait.hasNext(); ) {
      Attribute a = ait.next();
      this.space();
      this.writeIdentifier(a.getName());
      this.write(": ");
      String domain = a.getDomain().getTGTypeName(pkg);
      this.write(domain);
      if ((a.getDefaultValueAsString() != null) && !a.getDefaultValueAsString().equals("n")) {
        this.write(" = ");
        this.writeUtfString(a.getDefaultValueAsString());
      }
      if (ait.hasNext()) {
        this.write(", ");
      } else {
        this.write(" }");
      }
    }
=======
    String delim = " {";
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    for (Attribute a : attributes) {
      write(delim);
      delim = ",";
      space();
      writeIdentifier(a.getName());
      write(": ");
      String domain = a.getDomain().getTGTypeName(pkg);
      write(domain);
      if ((a.getDefaultValueAsString() != null) && !a.getDefaultValueAsString().equals("n")) {
        write(" = ");
        writeUtfString(a.getDefaultValueAsString());
      }
    }
    write(" }");
  }

  public final void write(String s) throws IOException {
    this.TGOut.writeBytes(s);
  }

  public final void noSpace() {
    this.writeSpace = false;
  }

  public final void space() {
    this.writeSpace = true;
  }

  public final void writeSpace() throws IOException {
    if (this.writeSpace) {
      this.TGOut.writeBytes(" ");
    }
    this.writeSpace = true;
  }

  public final void writeBoolean(boolean b) throws IOException {
    this.writeSpace();
    this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    TGOut.writeBytes(b ? TRUE_LITERAL : FALSE_LITERAL)
=======
    writeBytes(b ? TRUE_LITERAL : FALSE_LITERAL)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
  }

  public final void writeInteger(int i) throws IOException {
    this.writeSpace();
    this.TGOut.writeBytes(Integer.toString(i));
  }

  public final void writeLong(long l) throws IOException {
    this.writeSpace();
    this.TGOut.writeBytes(Long.toString(l));
  }

  public final void writeDouble(double d) throws IOException {
    this.writeSpace();
    this.TGOut.writeBytes(Double.toString(d));
  }

  public final void writeUtfString(String s) throws IOException {
    this.writeSpace();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.TGOut.writeBytes(s == null ? NULL_LITERAL : toUtfString(s))
=======
    TGOut.writeBytes(s == null ? NULL_LITERAL : toUtfString(s))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
  }

  public final void writeIdentifier(String s) throws IOException {
    this.writeSpace();
    this.TGOut.writeBytes(s);
  }

  public static GraphIO createStringReader(String input, Schema schema) throws GraphIOException {
    GraphIO io = new GraphIO();
    io.TGIn = new ByteArrayInputStream(input.getBytes(Charset.forName("US-ASCII")));
    io.line = 1;
    io.schema = schema;
    io.la = io.read();
    io.match();
    return io;
  }

  public static GraphIO createStringWriter(Schema schema) {
    GraphIO io = new GraphIO();
    io.BAOut = new ByteArrayOutputStream();
    io.TGOut = new DataOutputStream(io.BAOut);
    io.schema = schema;
    return io;
  }

  public String getStringWriterResult() throws GraphIOException, IOException {
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.BAOut == null
=======
    BAOut == null
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      throw new GraphIOException("GraphIO did not write to a String.");
    }
    try {
      try {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.TGOut.flush()
=======
        TGOut.flush()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.BAOut.flush()
=======
        BAOut.flush()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        String result = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.BAOut.toString("US-ASCII")
=======
        BAOut.toString("US-ASCII")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        return result;
      }  finally {
        close(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.TGOut
=======
        TGOut
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        );
      }
    }  finally {
      close(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.BAOut
=======
      BAOut
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      );
    }
  }

  public static Graph loadGraphFromFile(String filename, ProgressFunction pf) throws GraphIOException {
    return loadGraphFromFile(filename, ImplementationType.STANDARD, pf);
  }

  public static Graph loadGraphFromFile(String filename, ImplementationType implementationType, ProgressFunction pf) throws GraphIOException {
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    (implementationType == null) || (implementationType == ImplementationType.DATABASE)
=======
    implementationType == null || implementationType == ImplementationType.DATABASE
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      throw new IllegalArgumentException("ImplementationType must be != null and != DATABASE");
    }
    FileInputStream fileStream = null;
    try {
      fileStream = new FileInputStream(filename);
      InputStream inputStream = null;
      try {
        if (filename.toLowerCase().endsWith(".gz")) {
          inputStream = new GZIPInputStream(fileStream, BUFFER_SIZE);
        } else {
          inputStream = new BufferedInputStream(fileStream, BUFFER_SIZE);
        }
        return loadGraphFromStream(inputStream, null, null, implementationType, pf);
      } catch (IOException ex) {
        throw new GraphIOException("Exception while loading graph from file " + filename, ex);
      } finally {
        close(inputStream);
      }
    } catch (IOException ex) {
      throw new GraphIOException("Exception while loading graph from file " + filename, ex);
    } finally {
      close(fileStream);
    }
  }

  public static <G extends Graph> G loadGraphFromFile(String filename, Schema schema, ImplementationType implementationType, ProgressFunction pf) throws GraphIOException {
    if (schema == null) {
      throw new IllegalArgumentException("Schema must be != null");
    }
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    (implementationType == null) || (implementationType == ImplementationType.DATABASE)
=======
    implementationType == null || implementationType == ImplementationType.DATABASE
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      throw new IllegalArgumentException("ImplementationType must be != null and != DATABASE");
    }
    GraphFactory factory = schema.createDefaultGraphFactory(implementationType);
    return loadGraphFromFile(filename, factory, pf);
  }

  public static <G extends Graph> G loadGraphFromFile(String filename, GraphFactory factory, ProgressFunction pf) throws GraphIOException {
    if (factory == null) {
      throw new IllegalArgumentException("GraphFactory must be != null");
    }
    FileInputStream fileStream = null;
    try {
      logger.finer("Loading graph " + filename);
      fileStream = new FileInputStream(filename);
      InputStream inputStream = null;
      try {
        if (filename.toLowerCase().endsWith(".gz")) {
          inputStream = new GZIPInputStream(fileStream, BUFFER_SIZE);
        } else {
          inputStream = new BufferedInputStream(fileStream, BUFFER_SIZE);
        }
        return loadGraphFromStream(inputStream, factory.getSchema(), factory, factory.getImplementationType(), pf);
      } catch (IOException ex) {
        throw new GraphIOException("Exception while loading graph from file " + filename, ex);
      } finally {
        close(inputStream);
      }
    } catch (IOException ex) {
      throw new GraphIOException("Exception while loading graph from file " + filename, ex);
    } finally {
      close(fileStream);
    }
  }

  public static <G extends Graph> G loadGraphFromDatabase(String id, GraphDatabase graphDatabase) throws GraphDatabaseException {
    if (graphDatabase != null) {
      return graphDatabase.getGraph(id);
    } else {
      throw new GraphDatabaseException("No graph database given.");
    }
  }

  protected private static void close(Closeable stream) throws GraphIOException {
    try {
      if (stream != null) {
        stream.close();
      }
    } catch (IOException ex) {
      throw new GraphIOException("Exception while closing stream.", ex);
    }
  }

  public static <G extends Graph> G loadGraphFromStream(InputStream in, Schema schema, GraphFactory graphFactory, ImplementationType implementationType, ProgressFunction pf) throws GraphIOException {
    try {
      GraphIO io = new GraphIO();
      io.TGIn = in;
      io.schema = schema;
      io.tgfile();
      if (implementationType != ImplementationType.GENERIC) {
        String schemaQName = io.schema.getQualifiedName();
        Class<?> schemaClass = null;
        try {
          schemaClass = Class.forName(schemaQName, true, SchemaClassManager.instance(schemaQName));
        } catch (ClassNotFoundException e) {
          io.schema.finish();
          io.schema.compile(CodeGeneratorConfiguration.MINIMAL);
          try {
            schemaClass = Class.forName(schemaQName, true, SchemaClassManager.instance(schemaQName));
          } catch (ClassNotFoundException e1) {
            throw new GraphIOException("Unable to load a graph which belongs to the schema because the Java-classes for this schema can not be created.", e1);
          }
        }
        Method instanceMethod = schemaClass.getMethod("instance", (Class<?>[]) null);
        io.schema = (Schema) instanceMethod.invoke(null, new Object[0]);
      }
      io.schema.finish();
      if (graphFactory == null) {
        graphFactory = io.schema.createDefaultGraphFactory(implementationType);
      }
      if (graphFactory.getSchema() != io.schema) {
        throw new GraphIOException("Incompatible in graph factory: Expected \'" + io.schema.getQualifiedName() + "\', found \'" + graphFactory.getSchema().getQualifiedName() + "\'.");
      }
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      (implementationType != null) && (graphFactory.getImplementationType() != implementationType)
=======
      implementationType != null && graphFactory.getImplementationType() != implementationType
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {
        throw new GraphIOException("Graph factory has wrong implementation type: Expected \'" + implementationType + "\', found \'" + graphFactory.getImplementationType() + "\'.");
      }
      io.graphFactory = graphFactory;
      @SuppressWarnings(value = { "unchecked" }) G loadedGraph = (G) io.graph(pf);
      return loadedGraph;
    } catch (GraphIOException e1) {
      throw e1;
    } catch (Exception e2) {
      throw new GraphIOException("Exception while loading graph.", e2);
    }
  }

  protected void tgfile() throws GraphIOException, SchemaException, IOException {
    this.line = 1;
    this.la = this.read();
    this.match();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.header();
=======
    header();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    this.schema();
    if (this.lookAhead.equals("") || this.lookAhead.equals("Graph")) {
      return;
    }
    throw new GraphIOException("Symbol \'" + this.lookAhead + "\' not recognized in line " + this.line, null);
  }

  /**
	 * Reads TG File header and checks if the file version can be processed.
	 * 
	 * @throws GraphIOException
	 *             if version number in file can not be processed
	 */
  private void header() throws GraphIOException {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match("TGraph");
=======
    match("TGraph");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    int version = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.matchInteger()
=======
    matchInteger()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    if (version != TGFILE_VERSION) {
      throw new GraphIOException("Can\'t read TGFile version " + version + ". Expected version " + TGFILE_VERSION);
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match(";");
=======
    match(";");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
  }

  /**
	 * Reads a Schema together with its Domains, GraphClasses and
	 * GraphElementClasses from a TG-file. Subsequently, the Schema is created.
	 * 
	 * @throws GraphIOException
	 */
  protected void schema() throws GraphIOException, SchemaException {
    this.currentPackageName = "";
    this.match("Schema");
    String[] qn = this.matchQualifiedName(true);
    if (qn[0].equals("")) {
      throw new GraphIOException("Invalid schema name \'" + this.lookAhead + "\', package prefix must not be empty in line " + this.line);
    }
    this.match(";");
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.schema != null
=======
    schema != null
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.schema.getQualifiedName().equals(qn[0] + "." + qn[1])
=======
      schema.getQualifiedName().equals(qn[0] + "." + qn[1])
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {
        String prev = "";
        while ((
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.lookAhead.length()
=======
        lookAhead.length()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
         > 0) && !(prev.equals(";") && 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.lookAhead.equals("Graph")
=======
        lookAhead.equals("Graph")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        )) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          prev = this.lookAhead
=======
          prev = lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.match();
=======
          match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        }
        return;
      } else {
        throw new GraphIOException("Trying to load a graph with wrong schema. Expected: " + 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.schema.getQualifiedName()
=======
        schema.getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
         + ", but found " + qn[0] + "." + qn[1]);
      }
    }
    this.schema = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.createSchema(qn[1], qn[0])
=======
    new SchemaImpl(qn[1], qn[0])
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    this.parseSchema();
    if (!(this.lookAhead.equals("") || this.lookAhead.equals("Graph"))) {
      throw new GraphIOException("Symbol \'" + this.lookAhead + "\' not recognized in line " + this.line, null);
    }
    this.checkFromToVertexClasses();
    this.sortRecordDomains();
    this.sortVertexClasses();
    this.sortEdgeClasses();
    this.domDef();
    this.completeGraphClass();
    this.buildHierarchy();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.processComments();
=======
    processComments();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
  }

  protected Schema createSchema(String name, String prefix) {
    return new SchemaImpl(name, prefix);
  }

  /**
	 * Adds comments collected during schema parsing to the annotated elements.
	 * 
	 * @throws GraphIOException
	 */
  private void processComments() throws GraphIOException {
    for (Entry<String, List<String>> e : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.commentData.entrySet()
=======
    commentData.entrySet()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      if (!
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.schema.knows(e.getKey())
=======
      schema.knows(e.getKey())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {
        throw new GraphIOException("Annotated element \'" + e.getKey() + "\' not found in schema " + 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.schema.getQualifiedName()
=======
        schema.getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        );
      }
      NamedElement el = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.schema.getNamedElement(e.getKey())
=======
      schema.getNamedElement(e.getKey())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      if ((el instanceof Domain) && !((el instanceof EnumDomain) || (el instanceof RecordDomain))) {
        throw new GraphIOException("Default domains can not have comments. Offending domain is \'" + e.getKey() + "\'");
      }
      for (String comment : e.getValue()) {
        el.addComment(comment);
      }
    }
  }

  /**
	 * Creates the Domains contained in a Schema.
	 * 
	 * @return A Map of the Domain names to the concrete Domain objects.
	 * @throws GraphIOException
	 */
  private Map<String, Domain> domDef() throws GraphIOException, SchemaException {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.enumDomains()
=======
>>>>>>> Unknown file: This is a bug in JDime.
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.recordDomains()
=======
>>>>>>> Unknown file: This is a bug in JDime.
    ;
    return this.domains;
  }

  /**
	 * Reads an EnumDomain, i.e. its name along with the enum constants.
	 * 
	 * @throws GraphIOException
	 */
  private void parseEnumDomain() throws GraphIOException {
    this.match("EnumDomain");
    String[] qn = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.matchQualifiedName(true)
=======
    matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    enumDomainBuffer.add(new EnumDomainData(qn[0], qn[1], this.parseEnumConstants()))
=======
    add(new EnumDomainData(qn[0], qn[1], parseEnumConstants()))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    this.match(";");
  }

  /**
	 * Creates all EnumDomains whose data is stored in {@link enumDomainBuffer}
	 */
  private void enumDomains() {
    Domain domain;
    for (EnumDomainData enumDomainData : this.enumDomainBuffer) {
      String qName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.toQNameString(enumDomainData.packageName, enumDomainData.simpleName)
=======
      toQNameString(enumDomainData.packageName, enumDomainData.simpleName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      domain = this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      schema.createEnumDomain(qName, enumDomainData.enumConstants)
=======
      createEnumDomain(qName, enumDomainData.enumConstants)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      domains.put(qName, domain)
=======
      put(qName, domain)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }
  }

  /**
	 * Read a RecordDomain, i.e. its name along with the components.
	 * 
	 * @throws GraphIOException
	 */
  private void parseRecordDomain() throws GraphIOException {
    this.match("RecordDomain");
    String[] qn = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.matchQualifiedName(true)
=======
    matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    recordDomainBuffer.add(new RecordDomainData(qn[0], qn[1], this.parseRecordComponents()))
=======
    add(new RecordDomainData(qn[0], qn[1], parseRecordComponents()))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    this.match(";");
  }

  /**
	 * Creates all RecordDomains whose data is stored in
	 * {@link recordDomainBuffer} @
	 */
  private void recordDomains() throws GraphIOException, SchemaException {
    Domain domain;
    for (RecordDomainData recordDomainData : this.recordDomainBuffer) {
      String qName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.toQNameString(recordDomainData.packageName, recordDomainData.simpleName)
=======
      toQNameString(recordDomainData.packageName, recordDomainData.simpleName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      domain = this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      schema.createRecordDomain(qName, this.getComponents(recordDomainData.components))
=======
      createRecordDomain(qName, getComponents(recordDomainData.components))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      domains.put(qName, domain)
=======
      put(qName, domain)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }
  }

  private List<RecordComponent> getComponents(List<ComponentData> componentsData) throws GraphIOException {
    List<RecordComponent> result = new ArrayList<RecordComponent>(componentsData.size());
    for (ComponentData ad : componentsData) {
      RecordComponent c = new RecordComponent(ad.name, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.attrDomain(ad.domainDescription)
=======
      attrDomain(ad.domainDescription)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      );
      result.add(c);
    }
    return result;
  }

  /**
	 * Reads Schema's Domains and GraphClasses with contained
	 * GraphElementClasses from TG-file.
	 * 
	 * @throws GraphIOException
	 */
  private void parseSchema() throws GraphIOException, SchemaException {
    while (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("Comment")
=======
    lookAhead.equals("Comment")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.parseComment();
=======
      parseComment();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    }
    String currentGraphClassName = this.parseGraphClass();
    while (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("Package")
=======
    lookAhead.equals("Package")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     || this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    lookAhead.equals("RecordDomain")
=======
    equals("RecordDomain")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     || this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    lookAhead.equals("EnumDomain")
=======
    equals("EnumDomain")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     || this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    lookAhead.equals("abstract")
=======
    equals("abstract")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     || this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    lookAhead.equals("VertexClass")
=======
    equals("VertexClass")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     || this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    lookAhead.equals("EdgeClass")
=======
    equals("EdgeClass")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     || this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    lookAhead.equals("Comment")
=======
    equals("Comment")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      if (this.lookAhead.equals("Package")) {
        this.parsePackage();
      } else {
        if (this.lookAhead.equals("RecordDomain")) {
          this.parseRecordDomain();
        } else {
          if (this.lookAhead.equals("EnumDomain")) {
            this.parseEnumDomain();
          } else {
            if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.lookAhead.equals("Comment")
=======
            lookAhead.equals("Comment")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.parseComment();
=======
              parseComment();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.parseGraphElementClass(currentGraphClassName);
=======
              parseGraphElementClass(currentGraphClassName);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            }
          }
        }
      }
    }
  }

  private void parseComment() throws GraphIOException {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match("Comment");
=======
    match("Comment");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    String qName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.toQNameString(this.matchQualifiedName())
=======
    toQNameString(matchQualifiedName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    List<String> comments = new ArrayList<String>();
    comments.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.matchUtfString()
=======
    matchUtfString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    );
    while (!
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals(";")
=======
    lookAhead.equals(";")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      comments.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.matchUtfString()
=======
      matchUtfString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      );
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match(";");
=======
    match(";");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.commentData.containsKey(qName)
=======
    commentData.containsKey(qName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.commentData.get(qName).addAll(comments)
=======
      commentData.get(qName).addAll(comments)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.commentData.put(qName, comments)
=======
      commentData.put(qName, comments)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }
  }

  private void parsePackage() throws GraphIOException {
    this.match("Package");
    this.currentPackageName = "";
    if (this.lookAhead.equals(";")) {
      this.currentPackageName = "";
    } else {
      String[] qn = this.matchQualifiedName(false);
      String qualifiedName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.toQNameString(qn)
=======
      toQNameString(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      if (!isValidPackageName(qn[1])) {
        throw new GraphIOException("Invalid package name \'" + qualifiedName + "\' in line " + this.line);
      }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.currentPackageName
=======
>>>>>>> Unknown file: This is a bug in JDime.
       = qualifiedName;
    }
    this.match(";");
  }

  /**
	 * Creates the GraphClass contained in the Schema along with its
	 * GraphElementClasses.
	 * 
	 * @throws GraphIOException
	 * @throws SchemaException
	 */
  private void completeGraphClass() throws GraphIOException, SchemaException {
    GraphClass currentGraphClass = this.createGraphClass(this.graphClass);
    for (GraphElementClassData currentGraphElementClassData : this.vertexClassBuffer.get(this.graphClass.name)) {
      this.createVertexClass(currentGraphElementClassData, currentGraphClass);
    }
    for (GraphElementClassData currentGraphElementClassData : this.edgeClassBuffer.get(this.graphClass.name)) {
      this.createEdgeClass(currentGraphElementClassData, currentGraphClass);
    }
  }

  /**
	 * Reads a GraphClass from a TG-file.
	 * 
	 * @return The name of the read GraphClass.
	 * @throws GraphIOException
	 * @throws SchemaException
	 */
  private String parseGraphClass() throws GraphIOException, SchemaException {
    this.match("GraphClass");
    this.graphClass = new GraphClassData();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.graphClass.name = this.matchSimpleName(true)
=======
    graphClass.name = matchSimpleName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    if (this.lookAhead.equals("{")) {
      this.graphClass.attributes = this.parseAttributes();
    }
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("[")
=======
    lookAhead.equals("[")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.graphClass.constraints = this.parseConstraints()
=======
      graphClass.constraints = parseConstraints()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }
    this.match(";");
    this.vertexClassBuffer.put(this.graphClass.name, new ArrayList<GraphElementClassData>());
    this.edgeClassBuffer.put(this.graphClass.name, new ArrayList<GraphElementClassData>());
    return this.graphClass.name;
  }

  /**
	 * Creates a GraphClass based on the given GraphClassData.
	 * 
	 * @param gcData
	 *            The GraphClassData used to create the GraphClass.
	 * @return The created GraphClass.
	 * @throws GraphIOException
	 * @throws SchemaException
	 */
  private GraphClass createGraphClass(GraphClassData gcData) throws GraphIOException, SchemaException {
    GraphClass gc = this.schema.createGraphClass(gcData.name);
    gc.setAbstract(gcData.isAbstract);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.addAttributes(gcData.attributes, gc)
=======
    addAttributes(gcData.attributes, gc)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    for (Constraint constraint : gcData.constraints) {
      gc.addConstraint(constraint);
    }
    return gc;
  }

  /**
	 * Reads the direct superclasses of a GraphClass or a GraphElementClass from
	 * the TG-file.
	 * 
	 * @return A list of the direct super classes.
	 * @throws GraphIOException
	 */
  private List<String> parseHierarchy() throws GraphIOException {
    List<String> hierarchy = new LinkedList<String>();
    this.match(":");
    String[] qn = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.matchQualifiedName(true)
=======
    matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    hierarchy.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.toQNameString(qn)
=======
    toQNameString(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    );
    while (this.lookAhead.equals(",")) {
      this.match();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      qn = this.matchQualifiedName(true)
=======
      qn = matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      hierarchy.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.toQNameString(qn)
=======
      toQNameString(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      );
    }
    return hierarchy;
  }

  private List<AttributeData> parseAttributes() throws GraphIOException {
    List<AttributeData> attributesData = new ArrayList<AttributeData>();
    Set<String> names = new TreeSet<String>();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match("{");
=======
    match("{");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    AttributeData ad = new AttributeData();
    ad.name = this.matchSimpleName(false);
    this.match(":");

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    ad.domainDescription = this.parseAttrDomain()
=======
    ad.domainDescription = parseAttrDomain()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("=")
=======
    lookAhead.equals("=")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.match();
=======
      match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      ad.defaultValue = this.matchUtfString()
=======
      ad.defaultValue = matchUtfString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }
    attributesData.add(ad);
    names.add(ad.name);
    while (this.lookAhead.equals(",")) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.match(",");
=======
      match(",");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      ad = new AttributeData();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      ad.name = this.matchSimpleName(false)
=======
      ad.name = matchSimpleName(false)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.match(":")
=======
      match(":")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      ad.domainDescription = this.parseAttrDomain()
=======
      ad.domainDescription = parseAttrDomain()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.equals("=")
=======
      lookAhead.equals("=")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.match();
=======
        match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        ad.defaultValue = this.matchUtfString()
=======
        ad.defaultValue = matchUtfString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
      }
      if (names.contains(ad.name)) {
        throw new GraphIOException("Duplicate attribute name \'" + ad.name + "\' in line " + this.line);
      }
      attributesData.add(ad);
      names.add(ad.name);
    }
    this.match("}");
    return attributesData;
  }

  protected private void addAttributes(List<AttributeData> attributesData, AttributedElementClass<?, ?> aec) throws GraphIOException {
    for (AttributeData ad : attributesData) {
      aec.addAttribute(ad.name, attrDomain(ad.domainDescription), 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.schema.createAttribute(ad.name, this.attrDomain(ad.domainDescription), aec, ad.defaultValue)
=======
      ad.defaultValue
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      );
    }
  }

  private List<String> parseAttrDomain() throws GraphIOException {
    List<String> result = new ArrayList<String>();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.parseAttrDomain(result);
=======
    parseAttrDomain(result);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    return result;
  }

  /**
	 * Reads an Attribute's domain from the TG-file and stores it in the list
	 * given as argument.
	 * 
	 * @param attrDomain
	 *            The list to which an attribute's domain shall be added.
	 * @throws GraphIOException
	 */
  private void parseAttrDomain(List<String> attrDomain) throws GraphIOException {
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.matches("[.]?List")
=======
    lookAhead.matches("[.]?List")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      this.match();
      this.match("<");
      attrDomain.add("List<");
      this.parseAttrDomain(attrDomain);
      this.match(">");
    } else {
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.matches("[.]?Set")
=======
      lookAhead.matches("[.]?Set")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {
        this.match();
        this.match("<");
        attrDomain.add("Set<");
        this.parseAttrDomain(attrDomain);
        this.match(">");
      } else {
        if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.lookAhead.matches("[.]?Map")
=======
        lookAhead.matches("[.]?Map")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.match();
=======
          match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.match("<");
=======
          match("<");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          attrDomain.add("Map<");

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.parseAttrDomain(attrDomain);
=======
          parseAttrDomain(attrDomain);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.match(",");
=======
          match(",");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.parseAttrDomain(attrDomain);
=======
          parseAttrDomain(attrDomain);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.match(">");
=======
          match(">");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          if (this.isBasicDomainName(this.lookAhead)) {
            attrDomain.add(this.lookAhead);
            this.match();
          } else {
            String[] qn = this.matchQualifiedName(true);
            attrDomain.add(this.toQNameString(qn));
          }
=======
          if (isBasicDomainName(lookAhead)) {
            attrDomain.add(lookAhead);
            match();
          } else {
            String[] qn = matchQualifiedName(true);
            attrDomain.add(toQNameString(qn));
          }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        }
      }
    }
  }

  private boolean isBasicDomainName(String s) {
    return BasicDomainImpl.isBasicDomain(s.startsWith(".") ? s.substring(1) : s);
  }

  /**
	 * Creates a Domain corresponding to a list of domain names representing a,
	 * probably composite, domain.
	 * 
	 * @param domainNames
	 *            The list containing the names of, probably composite, domains.
	 * @return The created Domain.
	 * @throws GraphIOException
	 */
  private Domain attrDomain(List<String> domainNames) throws GraphIOException {
    Iterator<String> it = domainNames.iterator();
    String domainName;
    while (it.hasNext()) {
      domainName = it.next();
      it.remove();
      if (domainName.equals("List<")) {
        try {
          return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.schema.createListDomain(this.attrDomain(domainNames))
=======
          schema.createListDomain(attrDomain(domainNames))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        } catch (SchemaException e) {
          throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          "Can\'t create list domain in line " + this.line
=======
          "Can\'t create list domain in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          , e);
        }
      } else {
        if (domainName.equals("Set<")) {
          try {
            return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.schema.createSetDomain(this.attrDomain(domainNames))
=======
            schema.createSetDomain(attrDomain(domainNames))
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
          } catch (SchemaException e) {
            throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            "Can\'t create set domain in line " + this.line
=======
            "Can\'t create set domain in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            , e);
          }
        } else {
          if (domainName.equals("Map<")) {
            try {
              Domain keyDomain = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.attrDomain(domainNames)
=======
              attrDomain(domainNames)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;
              Domain valueDomain = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.attrDomain(domainNames)
=======
              attrDomain(domainNames)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;
              if (keyDomain == null) {
                throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
                "Can\'t create map domain, because no key domain was given in line " + this.line
=======
                "Can\'t create map domain, because no key domain was given in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
                );
              }
              MapDomain result = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.schema.createMapDomain(keyDomain, valueDomain)
=======
              schema.createMapDomain(keyDomain, valueDomain)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;
              return result;
            } catch (SchemaException e) {
              throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              "Can\'t create map domain in line " + this.line
=======
              "Can\'t create map domain in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              , e);
            }
          } else {
            Domain result = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.schema.getDomain(domainName)
=======
            schema.getDomain(domainName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
            if (result == null) {
              throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              "Undefined domain \'" + domainName + "\' in line " + this.line
=======
              "Undefined domain \'" + domainName + "\' in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              );
            }
            return result;
          }
        }
      }
    }
    throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    "Couldn\'t create domain for \'" + domainNames + "\' in line " + this.line
=======
    "Couldn\'t create domain for \'" + domainNames + "\' in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    );
  }

  public final String matchEnumConstant() throws GraphIOException {
    if (this.schema.isValidEnumConstant(this.lookAhead) || this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    lookAhead.equals(NULL_LITERAL)
=======
    equals(NULL_LITERAL)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      return this.matchAndNext();
    }
    throw new GraphIOException("Invalid enumeration constant \'" + this.lookAhead + "\' in line " + this.line);
  }

  /**
	 * Reads the a GraphElementClass of the GraphClass indicated by the given
	 * name.
	 * 
	 * @throws GraphIOException
	 */
  private void parseGraphElementClass(String gcName) throws GraphIOException, SchemaException {
    GraphElementClassData graphElementClassData = new GraphElementClassData();
    if (this.lookAhead.equals("abstract")) {
      this.match();
      graphElementClassData.isAbstract = true;
    }
    if (this.lookAhead.equals("VertexClass")) {
      this.match("VertexClass");
      String[] qn = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.matchQualifiedName(true)
=======
      matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      graphElementClassData.packageName = qn[0];
      graphElementClassData.simpleName = qn[1];
      if (this.lookAhead.equals(":")) {
        graphElementClassData.directSuperClasses = this.parseHierarchy();
      }
      this.vertexClassBuffer.get(gcName).add(graphElementClassData);
    } else {
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.equals("EdgeClass")
=======
      lookAhead.equals("EdgeClass")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.match()
=======
        match()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        String[] qn = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.matchQualifiedName(true)
=======
        matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        graphElementClassData.packageName = qn[0];
        graphElementClassData.simpleName = qn[1];
        if (this.lookAhead.equals(":")) {
          graphElementClassData.directSuperClasses = this.parseHierarchy();
        }
        this.match("from");
        String[] fqn = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.matchQualifiedName(true)
=======
        matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        graphElementClassData.fromVertexClassName = this.toQNameString(fqn)
=======
        graphElementClassData.fromVertexClassName = toQNameString(fqn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        graphElementClassData.fromMultiplicity = this.parseMultiplicity();
        graphElementClassData.fromRoleName = this.parseRoleName();
        graphElementClassData.redefinedFromRoles = this.parseRolenameRedefinitions();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        graphElementClassData.fromAggregation = this.parseAggregation()
=======
        graphElementClassData.fromAggregation = parseAggregation()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        this.match("to");
        String[] tqn = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.matchQualifiedName(true)
=======
        matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        graphElementClassData.toVertexClassName = this.toQNameString(tqn)
=======
        graphElementClassData.toVertexClassName = toQNameString(tqn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        graphElementClassData.toMultiplicity = this.parseMultiplicity();
        graphElementClassData.toRoleName = this.parseRoleName();
        graphElementClassData.redefinedToRoles = this.parseRolenameRedefinitions();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        graphElementClassData.toAggregation = this.parseAggregation()
=======
        graphElementClassData.toAggregation = parseAggregation()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        this.edgeClassBuffer.get(gcName).add(graphElementClassData);
      } else {
        throw new SchemaException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        "Undefined keyword: " + this.lookAhead
=======
        "Undefined keyword: " + lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
         + " at position ");
      }
    }
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("{")
=======
    lookAhead.equals("{")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      graphElementClassData.attributes = this.parseAttributes()
=======
      graphElementClassData.attributes = parseAttributes()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("[")
=======
    lookAhead.equals("[")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      graphElementClassData.constraints = this.parseConstraints()
=======
      graphElementClassData.constraints = parseConstraints()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match(";");
=======
    match(";");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
  }

  private Set<Constraint> parseConstraints() throws GraphIOException {
    HashSet<Constraint> constraints = new HashSet<Constraint>(1);
    do {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.match("[");
=======
      match("[");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      String msg = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.matchUtfString()
=======
      matchUtfString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      String pred = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.matchUtfString()
=======
      matchUtfString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      String greql = null;
      if (!
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.equals("]")
=======
      lookAhead.equals("]")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        greql = this.matchUtfString()
=======
        greql = matchUtfString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
      }
      constraints.add(new ConstraintImpl(msg, pred, greql));

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.match("]");
=======
      match("]");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    } while(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("[")
=======
    lookAhead.equals("[")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    );
    return constraints;
  }

  private VertexClass createVertexClass(GraphElementClassData vcd, GraphClass gc) throws GraphIOException, SchemaException {
    VertexClass vc = gc.createVertexClass(vcd.getQualifiedName());
    vc.setAbstract(vcd.isAbstract);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.addAttributes(vcd.attributes, vc);
=======
    addAttributes(vcd.attributes, vc);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    for (Constraint constraint : vcd.constraints) {
      vc.addConstraint(constraint);
    }
    this.GECsearch.put(vc, gc);
    return vc;
  }

  protected EdgeClass createEdgeClass(GraphElementClassData ecd, GraphClass gc) throws GraphIOException, SchemaException {
    EdgeClass ec = gc.createEdgeClass(ecd.getQualifiedName(), gc.getVertexClass(ecd.fromVertexClassName), ecd.fromMultiplicity[0], ecd.fromMultiplicity[1], ecd.fromRoleName, ecd.fromAggregation, gc.getVertexClass(ecd.toVertexClassName), ecd.toMultiplicity[0], ecd.toMultiplicity[1], ecd.toRoleName, ecd.toAggregation);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.addAttributes(ecd.attributes, ec)
=======
    addAttributes(ecd.attributes, ec)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    for (Constraint constraint : ecd.constraints) {
      ec.addConstraint(constraint);
    }
    ec.setAbstract(ecd.isAbstract);
    this.GECsearch.put(ec, gc);
    return ec;
  }

  /**
	 * Reads a multiplicity of an EdgeClass.
	 * 
	 * @return An array with two elements. The first element represents the
	 *         multiplicity's lower bound. The second element represents the
	 *         upper bound.
	 * @throws GraphIOException
	 */
  private int[] parseMultiplicity() throws GraphIOException {
    int[] multis = new int[2];
    this.match("(");
    int min = this.matchInteger();
    if (min < 0) {
      throw new GraphIOException("Minimum multiplicity \'" + min + "\' must be >=0 in line " + this.line);
    }
    this.match(",");
    int max;
    if (this.lookAhead.equals("*")) {
      max = Integer.MAX_VALUE;
      this.match();
    } else {
      max = this.matchInteger();
      if (max < min) {
        throw new GraphIOException("Maximum multiplicity \'" + max + "\' must be * or >=" + min + " in line " + this.line);
      }
    }
    this.match(")");
    multis[0] = min;
    multis[1] = max;
    return multis;
  }

  /**
	 * Reads a role name of an EdgeClass.
	 * 
	 * @return A role name.
	 * @throws GraphIOException
	 */
  private String parseRoleName() throws GraphIOException {
    if (this.lookAhead.equals("role")) {
      this.match();
      String result = this.matchSimpleName(false);
      return result;
    }
    return "";
  }

  /**
	 * Reads the redefinition of a rolename of an EdgeClass
	 * 
	 * @return A Set<String> of redefined rolenames or <code>null</code> if no
	 *         rolenames were redefined
	 * @throw GraphIOException
	 */
  private Set<String> parseRolenameRedefinitions() throws GraphIOException {
    if (!this.lookAhead.equals("redefines")) {
      return null;
    }
    this.match();
    Set<String> result = new HashSet<String>();
    String redefinedName = this.matchSimpleName(false);
    result.add(redefinedName);
    while (this.lookAhead.equals(",")) {
      this.match();
      redefinedName = this.matchSimpleName(false);
      result.add(redefinedName);
    }
    return result;
  }

  private AggregationKind parseAggregation() throws GraphIOException {
    if (!
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("aggregation")
=======
    lookAhead.equals("aggregation")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      return AggregationKind.NONE;
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match();
=======
    match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("none")
=======
    lookAhead.equals("none")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.match();
=======
      match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      return AggregationKind.NONE;
    } else {
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.equals("shared")
=======
      lookAhead.equals("shared")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.match();
=======
        match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

        return AggregationKind.SHARED;
      } else {
        if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.lookAhead.equals("composite")
=======
        lookAhead.equals("composite")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.match();
=======
          match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

          return AggregationKind.COMPOSITE;
        } else {
          throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          "Invalid aggregation: expected \'none\', \'shared\', or \'composite\', but found \'" + this.lookAhead + "\' in line " + this.line
=======
          "Invalid aggregation: expected \'none\', \'shared\', or \'composite\', but found \'" + lookAhead + "\' in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          );
        }
      }
    }
  }

  private static boolean isValidPackageName(String s) {
    if ((s == null) || (s.length() == 0)) {
      return false;
    }
    char[] chars = s.toCharArray();
    if (!Character.isLetter(chars[0]) || !Character.isLowerCase(chars[0]) || (chars[0] > 127)) {
      return false;
    }
    for (int i = 1; i < chars.length; i++) {
      if (!(Character.isLowerCase(chars[i]) || Character.isDigit(chars[i]) || (chars[i] == '_')) || (chars[i] > 127)) {
        return false;
      }
    }
    return true;
  }

  private List<ComponentData> parseRecordComponents() throws GraphIOException {
    List<ComponentData> componentsData = new ArrayList<ComponentData>();
    Set<String> names = new TreeSet<String>();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match("(");
=======
    match("(");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    ComponentData cd = new ComponentData();
    cd.name = this.matchSimpleName(false);
    this.match(":");

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    cd.domainDescription = this.parseAttrDomain()
=======
    cd.domainDescription = parseAttrDomain()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    componentsData.add(cd);
    names.add(cd.name);
    while (this.lookAhead.equals(",")) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.match(",");
=======
      match(",");
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

      cd = new ComponentData();
      cd.name = this.matchSimpleName(false);
      this.match(":");

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      cd.domainDescription = this.parseAttrDomain()
=======
      cd.domainDescription = parseAttrDomain()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      if (names.contains(cd.name)) {
        throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        "Duplicate record component name \'" + cd.name + "\' in line " + this.line
=======
        "Duplicate record component name \'" + cd.name + "\' in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        );
      }
      componentsData.add(cd);
      names.add(cd.name);
    }
    this.match(")");
    return componentsData;
  }

  /**
	 * Reads the constants of an EnumDomain. Duplicate constant names are
	 * rejected.
	 * 
	 * @return A list of String containing the constants.
	 * @throws GraphIOException
	 *             if duplicate constant names are read.
	 */
  private List<String> parseEnumConstants() throws GraphIOException {
    this.match("(");
    List<String> enums = new ArrayList<String>();
    enums.add(this.matchEnumConstant());
    while (this.lookAhead.equals(",")) {
      this.match();
      String s = this.matchEnumConstant();
      if (enums.contains(s)) {
        throw new GraphIOException("Duplicate enumeration constant name \'" + this.lookAhead + "\' in line " + this.line);
      }
      enums.add(s);
    }
    this.match(")");
    return enums;
  }

  private void buildVertexClassHierarchy() throws GraphIOException, SchemaException {
    AttributedElementClass<?, ?> aec;
    VertexClass superClass;
    for (Entry<String, List<GraphElementClassData>> gcElements : this.vertexClassBuffer.entrySet()) {
      for (GraphElementClassData vData : gcElements.getValue()) {
        aec = this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        schema.getAttributedElementClass(vData.getQualifiedName())
=======
        getAttributedElementClass(vData.getQualifiedName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        if (aec == null) {
          throw new GraphIOException("Undefined AttributedElementClass \'" + vData.getQualifiedName() + "\'");
        }
        if (aec instanceof VertexClass) {
          for (String superClassName : vData.directSuperClasses) {
            superClass = (VertexClass) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.GECsearch.get(aec).getGraphElementClass(superClassName)
=======
            GECsearch.get(aec).getGraphElementClass(superClassName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
            if (superClass == null) {
              throw new GraphIOException("Undefined VertexClass \'" + superClassName + "\'");
            }
            ((VertexClass) aec).addSuperClass(superClass);
          }
        }
      }
    }
  }

  private void buildEdgeClassHierarchy() throws GraphIOException, SchemaException {
    AttributedElementClass<?, ?> aec;
    EdgeClass superClass;
    for (Entry<String, List<GraphElementClassData>> gcElements : this.edgeClassBuffer.entrySet()) {
      for (GraphElementClassData eData : gcElements.getValue()) {
        aec = this.
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        schema.getAttributedElementClass(eData.getQualifiedName())
=======
        getAttributedElementClass(eData.getQualifiedName())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        if (aec == null) {
          throw new GraphIOException("Undefined AttributedElementClass \'" + eData.getQualifiedName() + "\'");
        }
        if (!(aec instanceof EdgeClass)) {
          throw new GraphIOException("Expected EdgeClass \'" + eData.getQualifiedName() + "\', but it\'s a " + aec.getSchemaClass().getSimpleName());
        }
        EdgeClass ec = (EdgeClass) aec;
        for (String superClassName : eData.directSuperClasses) {
          superClass = (EdgeClass) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.GECsearch.get(aec).getGraphElementClass(superClassName)
=======
          GECsearch.get(aec).getGraphElementClass(superClassName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
          if (superClass == null) {
            throw new GraphIOException("Undefined EdgeClass \'" + superClassName + "\'");
          }
          ec.addSuperClass(superClass);
        }
        ec.getFrom().addRedefinedRoles(eData.redefinedFromRoles);
        ec.getTo().addRedefinedRoles(eData.redefinedToRoles);
      }
    }
  }

  private void buildHierarchy() throws GraphIOException, SchemaException {
    this.buildVertexClassHierarchy();
    this.buildEdgeClassHierarchy();
  }

  private final String nextToken() throws GraphIOException {
    StringBuilder out = new StringBuilder();
    this.isUtfString = false;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.skipWs();
=======
    skipWs();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.la == '\"'
=======
    la == '\"'
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.readUtfString(out);
=======
      readUtfString(out);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.isUtfString = true
=======
      isUtfString = true
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    } else {
      if (isSeparator(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.la
=======
      la
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      )) {
        out.append(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        (char) this.la
=======
        (char) la
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        );

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.la = this.read()
=======
        la = read()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
      } else {
        if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.la != -1
=======
        la != -1
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ) {
          do {
            out.append(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            (char) this.la
=======
            (char) la
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            );

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
            this.la = this.read()
=======
            la = read()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
            ;
          } while(!isWs(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.la
=======
          la
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ) && !isSeparator(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.la
=======
          la
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ) && (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.la != -1
=======
          la != -1
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ));
        }
      }
    }
    return out.toString();
  }

  private final int read() throws GraphIOException {
    try {
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.putBackChar >= 0
=======
      putBackChar >= 0
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {
        int result = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.putBackChar
=======
        putBackChar
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.putBackChar = -1
=======
        putBackChar = -1
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        return result;
      }
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.bufferPos < this.bufferSize
=======
      bufferPos < bufferSize
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {
        return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.buffer[this.bufferPos++]
=======
        buffer[bufferPos++]
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
      } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.bufferSize = this.TGIn.read(this.buffer)
=======
        bufferSize = TGIn.read(buffer)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.bufferSize != -1
=======
        bufferSize != -1
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.bufferPos = 0
=======
          bufferPos = 0
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
          return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.buffer[this.bufferPos++]
=======
          buffer[bufferPos++]
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ;
        } else {
          return -1;
        }
      }
    } catch (IOException e) {
      throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      "Error on reading bytes from file, line " + this.line
=======
      "Error on reading bytes from file, line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       + ", last char read was " + (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.la >= 0
=======
      la >= 0
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       ? "\'" + 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      (char) this.la
=======
      (char) la
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       + "\'" : "end of file"), e);
    }
  }

  private final void readUtfString(StringBuilder out) throws GraphIOException {
    int startLine = this.line;
    this.la = this.read();
    LOOP:
    while ((
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.la != -1
=======
    la != -1
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) && (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.la != '\"'
=======
    la != '\"'
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    )) {
      if ((
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.la < 32
=======
      la < 32
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) || (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.la > 127
=======
      la > 127
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      )) {
        throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        "Invalid character \'" + (char) this.la + "\' in string in line " + this.line
=======
        "Invalid character \'" + (char) la + "\' in string in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        );
      }
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.la == '\\'
=======
      la == '\\'
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.la = this.read()
=======
        la = read()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
        if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.la == -1
=======
        la == -1
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ) {
          break LOOP;
        }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        switch (this.la) {
          case '\\':
          this.la = '\\';
          break;
          case '\"':
          this.la = '\"';
          break;
          case 'n':
          this.la = '\n';
          break;
          case 'r':
          this.la = '\r';
          break;
          case 't':
          this.la = '\t';
          break;
          case 'u':
          this.la = this.read();
          if (this.la == -1) {
            break LOOP;
          }
          String unicode = "" + (char) this.la;
          this.la = this.read();
          if (this.la == -1) {
            break LOOP;
          }
          unicode += (char) this.la;
          this.la = this.read();
          if (this.la == -1) {
            break LOOP;
          }
          unicode += (char) this.la;
          this.la = this.read();
          if (this.la == -1) {
            break LOOP;
          }
          unicode += (char) this.la;
          try {
            this.la = Integer.parseInt(unicode, 16);
          } catch (NumberFormatException e) {
            throw new GraphIOException("Invalid unicode escape sequence \'\\u" + unicode + "\' in line " + this.line);
          }
          break;
          default:
          throw new GraphIOException("Invalid escape sequence in string in line " + this.line);
        }
=======
        switch (la) {
          case '\\':
          la = '\\';
          break;
          case '\"':
          la = '\"';
          break;
          case 'n':
          la = '\n';
          break;
          case 'r':
          la = '\r';
          break;
          case 't':
          la = '\t';
          break;
          case 'u':
          la = read();
          if (la == -1) {
            break LOOP;
          }
          String unicode = "" + (char) la;
          la = read();
          if (la == -1) {
            break LOOP;
          }
          unicode += (char) la;
          la = read();
          if (la == -1) {
            break LOOP;
          }
          unicode += (char) la;
          la = read();
          if (la == -1) {
            break LOOP;
          }
          unicode += (char) la;
          try {
            la = Integer.parseInt(unicode, 16);
          } catch (NumberFormatException e) {
            throw new GraphIOException("Invalid unicode escape sequence \'\\u" + unicode + "\' in line " + line);
          }
          break;
          default:
          throw new GraphIOException("Invalid escape sequence in string in line " + line);
        }
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      }
      out.append(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      (char) this.la
=======
      (char) la
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      );

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.la = this.read()
=======
      la = read()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }
    if (this.la == -1) {
      throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      "Unterminated string starting in line " + startLine + ".  lookAhead = \'" + this.lookAhead
=======
      "Unterminated string starting in line " + startLine + ".  lookAhead = \'" + lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       + "\'");
    }
    this.la = this.read();
  }

  private final static boolean isWs(int c) {
    return (c == ' ') || (c == '\n') || (c == '\t') || (c == '\r');
  }

  private final static boolean isSeparator(int c) {
    return (c == ';') || (c == '<') || (c == '>') || (c == '(') || (c == ')') || (c == '{') || (c == '}') || (c == ':') || (c == '[') || (c == ']') || (c == ',') || (c == '=');
  }

  private final void skipWs() throws GraphIOException {
    do {
      while (isWs(this.la)) {
        if (this.la == '\n') {
          ++this.line;
        }
        this.la = this.read();
      }
      if (this.la == '/') {
        this.la = this.read();
        if ((
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.la >= 0
=======
        la >= 0
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ) && (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.la == '/'
=======
        la == '/'
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        )) {
          while ((
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.la >= 0
=======
          la >= 0
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          ) && (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
          this.la != '\n'
=======
          la != '\n'
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
          )) {
            this.la = this.read();
          }
        } else {
          this.putback(this.la);
        }
      }
    } while(isWs(this.la));
  }

  private final void putback(int ch) {
    this.putBackChar = ch;
  }

  private final String matchAndNext() throws GraphIOException {
    String result = this.lookAhead;
    this.match();
    return result;
  }

  public final boolean isNextToken(String token) {
    return this.lookAhead.equals(token);
  }

  public final void match() throws GraphIOException {
    this.lookAhead = this.nextToken();
  }

  public final void match(String s) throws GraphIOException {
    if (this.lookAhead.equals(s)) {
      this.lookAhead = this.nextToken();
    } else {
      throw new GraphIOException("Expected \'" + s + "\' but found " + (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.equals("")
=======
      lookAhead.equals("")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       ? "end of file" : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      "\'" + this.lookAhead
=======
      "\'" + lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       + "\'") + " in line " + this.line, null);
    }
  }

  public final int matchInteger() throws GraphIOException {
    try {
      int result = Integer.parseInt(this.lookAhead);
      this.match();
      return result;
    } catch (NumberFormatException e) {
      throw new GraphIOException("Expected int number but found " + (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.equals("")
=======
      lookAhead.equals("")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       ? "end of file" : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      "\'" + this.lookAhead
=======
      "\'" + lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       + "\'") + " in line " + this.line, e);
    }
  }

  public final long matchLong() throws GraphIOException {
    try {
      long result = Long.parseLong(this.lookAhead);
      this.match();
      return result;
    } catch (NumberFormatException e) {
      throw new GraphIOException("Expected long number but found " + (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.equals("")
=======
      lookAhead.equals("")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       ? "end of file" : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      "\'" + this.lookAhead
=======
      "\'" + lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       + "\'") + " in line " + this.line, e);
    }
  }

  /**
	 * Parses an identifier, checks it for validity and returns it.
	 * 
	 * @param isUpperCase
	 *            If true, the identifier must begin with an uppercase character
	 * @return the parsed identifier
	 * @throws GraphIOException
	 */
  public final String matchSimpleName(boolean isUpperCase) throws GraphIOException {
    String s = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead
=======
    lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    boolean ok = isValidIdentifier(s) && ((isUpperCase && Character.isUpperCase(s.charAt(0))) || (!isUpperCase && Character.isLowerCase(s.charAt(0))));
    if (!ok) {
      throw new GraphIOException("Invalid simple name \'" + this.lookAhead + "\' in line " + this.line);
    }
    this.match();
    return s;
  }

  /**
	 * Parses an identifier, checks it for validity and returns it.
	 * 
	 * @param isUpperCase
	 *            If true, the identifier must begin with an uppercase character
	 * @return An array of the form {parentPackage, simpleName}
	 * @throws GraphIOException
	 */
  public final String[] matchQualifiedName(boolean isUpperCase) throws GraphIOException {
    String c = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.indexOf('.') >= 0 ? this.lookAhead : this.toQNameString(this.currentPackageName, this.lookAhead)
=======
    lookAhead.indexOf('.') >= 0 ? lookAhead : toQNameString(currentPackageName, lookAhead)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    String[] result = SchemaImpl.splitQualifiedName(c);
    boolean ok = true;
    if (result[0].length() > 0) {
      String[] parts = result[0].split("\\.");
      ok = ((parts.length == 1) && (parts[0].length() == 0)) || isValidPackageName(parts[0]);
      for (int i = 1; (i < parts.length) && ok; i++) {
        ok = ok && isValidPackageName(parts[i]);
      }
    }
    ok = ok && isValidIdentifier(result[1]) && ((isUpperCase && Character.isUpperCase(result[1].charAt(0))) || (!isUpperCase && Character.isLowerCase(result[1].charAt(0))));
    if (!ok) {
      throw new GraphIOException("Invalid qualified name \'" + this.lookAhead + "\' in line " + this.line);
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match()
=======
>>>>>>> Unknown file: This is a bug in JDime.
    ;
    return result;
  }

  public final String[] matchQualifiedName() throws GraphIOException {
    String c = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.indexOf('.') >= 0 ? this.lookAhead : this.toQNameString(this.currentPackageName, this.lookAhead)
=======
    lookAhead.indexOf('.') >= 0 ? lookAhead : toQNameString(currentPackageName, lookAhead)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    String[] result = SchemaImpl.splitQualifiedName(c);
    boolean ok = true;
    if (result[0].length() > 0) {
      String[] parts = result[0].split("\\.");
      ok = ((parts.length == 1) && (parts[0].length() == 0)) || isValidPackageName(parts[0]);
      for (int i = 1; (i < parts.length) && ok; i++) {
        ok = ok && isValidPackageName(parts[i]);
      }
    }
    ok = ok && isValidIdentifier(result[1]);
    if (!ok) {
      throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      "Invalid qualified name \'" + this.lookAhead + "\' in line " + this.line
=======
      "Invalid qualified name \'" + lookAhead + "\' in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      );
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.match();
=======
    match();
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java

    return result;
  }

  /**
	 * @param qn
	 * @return a string representation of a qualified name specified as array
	 *         (like returned by @{#matchQualifiedName}).
	 */
  private final String toQNameString(String[] qn) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.toQNameString(qn[0], qn[1])
=======
    toQNameString(qn[0], qn[1])
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
  }

  /**
	 * @param pn
	 *            package name
	 * @param sn
	 *            simple name
	 * @return a string representation of a qualified name specified as package
	 *         name and simple name.
	 */
  private final String toQNameString(String pn, String sn) {
    if ((pn == null) || pn.isEmpty()) {
      return sn;
    }
    return pn + "." + sn;
  }

  public final String matchUtfString() throws GraphIOException {
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    !this.isUtfString
=======
    !isUtfString
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     && 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals(NULL_LITERAL)
=======
    lookAhead.equals(NULL_LITERAL)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      this.match();
      return null;
    }
    if (this.isUtfString) {
      String result = this.lookAhead;
      this.match();
      String s = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.stringPool.get(result)
=======
      stringPool.get(result)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
      if (s == null) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.stringPool.put(result, result)
=======
        stringPool.put(result, result)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
        ;
      } else {
        result = s;
      }
      return result;
    }
    throw new GraphIOException("Expected a string constant but found " + (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals("")
=======
    lookAhead.equals("")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     ? "end of file" : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    "\'" + this.lookAhead
=======
    "\'" + lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     + "\'") + " in line " + this.line);
  }

  public final boolean matchBoolean() throws GraphIOException {
    if (!this.lookAhead.equals("t") && !this.lookAhead.equals("f")) {
      throw new GraphIOException("Expected a boolean constant (\'f\' or \'t\') but found " + (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.lookAhead.equals("")
=======
      lookAhead.equals("")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       ? "end of file" : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      "\'" + this.lookAhead
=======
      "\'" + lookAhead
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       + "\'") + " in line " + this.line);
    }
    boolean result = this.lookAhead.equals("t");
    this.match();
    return result;
  }

  private GraphBaseImpl graph(ProgressFunction pf) throws GraphIOException {
    this.currentPackageName = "";
    this.match("Graph");
    String graphId = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.matchUtfString()
=======
    matchUtfString()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    long graphVersion = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.matchLong()
=======
    matchLong()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.gcName = this.matchAndNext()
=======
    gcName = matchAndNext()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    assert !
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.gcName.contains(".")
=======
    gcName.contains(".")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
     && isValidIdentifier(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.gcName
=======
    gcName
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) : "illegal characters in graph class \'" + this.gcName + "\'";
    if (!
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.schema.getGraphClass().getQualifiedName().equals(this.gcName)
=======
    schema.getGraphClass().getQualifiedName().equals(gcName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {
      throw new GraphIOException("Graph Class " + this.gcName + "does not exist in " + this.schema.getQualifiedName());
    }
    this.match("(");
    int maxV = this.matchInteger();
    int maxE = this.matchInteger();
    int vCount = this.matchInteger();
    int eCount = this.matchInteger();
    this.match(")");
    if (vCount > maxV) {
      throw new GraphIOException("Number of vertices in graph (" + vCount + ") exceeds maximum number of vertices (" + maxV + ")");
    }
    if (eCount > maxE) {
      throw new GraphIOException("Number of edges in graph (" + eCount + ") exceeds maximum number of edges (" + maxE + ")");
    }
    this.edgeIn = new Vertex[maxE + 1];
    this.edgeOut = new Vertex[maxE + 1];

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.firstIncidence = new int[maxV + 1]
=======
    firstIncidence = new int[maxV + 1]
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.nextIncidence = new int[(2 * maxE) + 1]
=======
    nextIncidence = new int[(2 * maxE) + 1]
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    this.edgeOffset = maxE;
    long graphElements = 0, currentCount = 0, interval = 1;
    if (pf != null) {
      pf.init(vCount + eCount);
      interval = pf.getUpdateInterval();
    }
    GraphBaseImpl graph = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.graphFactory.createGraph(this.schema.getGraphClass(), graphId, maxV, maxE)
=======
    graphFactory.createGraph(schema.getGraphClass(), graphId, maxV, maxE)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    graph.setLoading(true);
    graph.readAttributeValues(this);
    this.match(";");
    int vNo = 1;
    while (vNo <= vCount) {
      if (this.lookAhead.equals("Package")) {
        this.parsePackage();
      } else {
        this.vertexDesc(graph);
        if (pf != null) {
          graphElements++;
          currentCount++;
          if (currentCount == interval) {
            pf.progress(graphElements);
            currentCount = 0;
          }
        }
        ++vNo;
      }
    }
    int eNo = 1;
    while (eNo <= eCount) {
      if (this.lookAhead.equals("Package")) {
        this.parsePackage();
      } else {
        this.edgeDesc(graph);
        if (pf != null) {
          graphElements++;
          currentCount++;
          if (currentCount == interval) {
            pf.progress(graphElements);
            currentCount = 0;
          }
        }
        ++eNo;
      }
    }
    graph.setGraphVersion(graphVersion);
    if (pf != null) {
      pf.finished();
    }
    graph.internalLoadingCompleted(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.firstIncidence
=======
    firstIncidence
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.nextIncidence
=======
    nextIncidence
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    );

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.firstIncidence = null
=======
    firstIncidence = null
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.nextIncidence = null
=======
    nextIncidence = null
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    graph.setLoading(false);
    graph.loadingCompleted();
    return graph;
  }

  public final double matchDouble() throws GraphIOException {
    try {
      double result = Double.parseDouble(this.lookAhead);
      this.match();
      return result;
    } catch (NumberFormatException e) {
      throw new GraphIOException(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      "expected a double value but found \'" + this.lookAhead + "\' in line " + this.line
=======
      "expected a double value but found \'" + lookAhead + "\' in line " + line
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      , e);
    }
  }

  private void vertexDesc(Graph graph) throws GraphIOException {
    int vId = this.vId();
    String vcName = this.className();
    VertexClass vc = (VertexClass) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.schema.getAttributedElementClass(vcName)
=======
    schema.getAttributedElementClass(vcName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    Vertex vertex = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.graphFactory.createVertex(vc, vId, graph)
=======
    graphFactory.createVertex(vc, vId, graph)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    this.parseIncidentEdges(vertex);
    vertex.readAttributeValues(this);
    this.match(";");
  }

  private void edgeDesc(Graph graph) throws GraphIOException {
    int eId = this.eId();
    String ecName = this.className();
    EdgeClass ec = (EdgeClass) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.schema.getAttributedElementClass(ecName)
=======
    schema.getAttributedElementClass(ecName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    Edge edge = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.graphFactory.createEdge(ec, eId, graph, this.edgeOut[eId], this.edgeIn[eId])
=======
    graphFactory.createEdge(ec, eId, graph, edgeOut[eId], edgeIn[eId])
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    edge.readAttributeValues(this);
    this.match(";");
  }

  private int eId() throws GraphIOException {
    int eId = this.matchInteger();
    if (eId == 0) {
      throw new GraphIOException("Invalid edge id " + eId + ".");
    }
    return eId;
  }

  private String className() throws GraphIOException {
    String[] qn = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.matchQualifiedName(true)
=======
    matchQualifiedName(true)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.toQNameString(qn)
=======
    toQNameString(qn)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ;
  }

  private int vId() throws GraphIOException {
    int vId = this.matchInteger();
    if (vId <= 0) {
      throw new GraphIOException("Invalid vertex id " + vId + ".");
    } else {
      return vId;
    }
  }

  private void parseIncidentEdges(Vertex v) throws GraphIOException {
    int eId = 0;
    int prevId = 0;
    int vId = v.getId();
    this.match("<");
    if (!
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
    this.lookAhead.equals(">")
=======
    lookAhead.equals(">")
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      eId = this.eId()
=======
      eId = eId()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.firstIncidence[vId]
=======
      firstIncidence[vId]
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       = eId;
      if (eId < 0) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.edgeIn[-eId]
=======
        edgeIn[-eId]
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
         = v;
      } else {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
        this.edgeOut[eId]
=======
        edgeOut[eId]
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
         = v;
      }
    }
    while (!this.lookAhead.equals(">")) {
      prevId = eId;
      eId = this.eId();

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      this.nextIncidence[this.edgeOffset + prevId]
=======
      nextIncidence[edgeOffset + prevId]
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
       = eId;
      if (eId < 0) {
        this.edgeIn[-eId] = v;
      } else {
        this.edgeOut[eId] = v;
      }
    }
    this.match();
  }

  /**
	 * Converts a String value with arbitrary characters to a quoted string
	 * value containing only ASCII characters and escaped unicode sequences as
	 * required by the TG file format.
	 * 
	 * @param value
	 *            a string
	 * @return a quoted string suitable for storage in TG files.
	 */
  public static String toUtfString(String value) {
    if (value == null) {
      return "";
    }
    StringBuilder out = new StringBuilder("\"");
    CharBuffer cb = CharBuffer.wrap(value);
    char c;
    while (cb.hasRemaining()) {
      c = cb.get();
      switch (c) {
        case '\"':
        out.append("\\\"");
        break;
        case '\n':
        out.append("\\n");
        break;
        case '\r':
        out.append("\\r");
        break;
        case '\\':
        out.append("\\\\");
        break;
        case '\t':
        out.append("\\t");
        break;
        default:
        if ((c >= 32) && (c <= 127)) {
          out.append(c);
        } else {
          out.append("\\u");
          String s = Integer.toHexString(c);
          switch (s.length()) {
            case 1:
            out.append("000");
            break;
            case 2:
            out.append("00");
            break;
            case 3:
            out.append("0");
            break;
          }
          out.append(s);
        }
      }
    }
    out.append("\"");
    return out.toString();
  }

  private static boolean isValidIdentifier(String s) {
    if ((s == null) || (s.length() == 0)) {
      return false;
    }
    char[] chars = s.toCharArray();
    if (!Character.isLetter(chars[0]) || (chars[0] > 127)) {
      return false;
    }
    for (int i = 1; i < chars.length; i++) {
      if (!(Character.isLetter(chars[i]) || Character.isDigit(chars[i]) || (chars[i] == '_')) || (chars[i] > 127)) {
        return false;
      }
    }
    return true;
  }

  private void sortRecordDomains() throws GraphIOException {
    List<RecordDomainData> orderedRdList = new ArrayList<RecordDomainData>();
    boolean componentDomsInOrderedList = true;
    RecordDomainData rd;
    boolean definedRdName;
    while (!this.recordDomainBuffer.isEmpty()) {
      for (Iterator<RecordDomainData> rdit = this.recordDomainBuffer.iterator(); rdit.hasNext(); ) {
        rd = rdit.next();
        componentDomsInOrderedList = true;
        for (ComponentData comp : rd.components) {
          for (String componentDomain : comp.domainDescription) {
            if (componentDomain.equals("String") || componentDomain.equals("Integer") || componentDomain.equals("Boolean") || componentDomain.equals("Long") || componentDomain.equals("Double") || componentDomain.equals("Set<") || componentDomain.equals("List<") || componentDomain.equals("Map<")) {
              continue;
            }
            componentDomsInOrderedList = false;
            for (RecordDomainData orderedRd : orderedRdList) {
              String qName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.toQNameString(orderedRd.packageName, orderedRd.simpleName)
=======
              toQNameString(orderedRd.packageName, orderedRd.simpleName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;
              if (componentDomain.equals(qName)) {
                componentDomsInOrderedList = true;
                break;
              }
            }
            for (EnumDomainData ed : this.enumDomainBuffer) {
              String qName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
              this.toQNameString(ed.packageName, ed.simpleName)
=======
              toQNameString(ed.packageName, ed.simpleName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
              ;
              if (componentDomain.equals(qName)) {
                componentDomsInOrderedList = true;
                break;
              }
            }
            if (!componentDomsInOrderedList) {
              definedRdName = false;
              for (RecordDomainData rd2 : this.recordDomainBuffer) {
                String qName = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
                this.toQNameString(rd2.packageName, rd2.simpleName)
=======
                toQNameString(rd2.packageName, rd2.simpleName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
                ;
                if (qName.equals(componentDomain)) {
                  definedRdName = true;
                  break;
                }
              }
              if (!definedRdName) {
                throw new GraphIOException("Domain " + componentDomain + " does not exist");
              }
              break;
            }
          }
          if (!componentDomsInOrderedList) {
            break;
          }
        }
        if (componentDomsInOrderedList) {
          orderedRdList.add(rd);
          rdit.remove();
        }
      }
    }
    this.recordDomainBuffer = orderedRdList;
  }

  private void sortVertexClasses() throws GraphIOException {
    List<GraphElementClassData> orderedVcList, unorderedVcList;
    Set<String> orderedVcNames = new TreeSet<String>();
    GraphElementClassData vc;
    boolean definedVcName;
    unorderedVcList = this.vertexClassBuffer.get(this.graphClass.name);
    orderedVcList = new ArrayList<GraphElementClassData>();
    while (!unorderedVcList.isEmpty()) {
      for (Iterator<GraphElementClassData> vcit = unorderedVcList.iterator(); vcit.hasNext(); ) {
        vc = vcit.next();
        if (orderedVcNames.containsAll(vc.directSuperClasses)) {
          orderedVcNames.add(vc.getQualifiedName());
          orderedVcList.add(vc);
          vcit.remove();
        } else {
          for (String superClass : vc.directSuperClasses) {
            if (orderedVcNames.contains(superClass)) {
              continue;
            }
            definedVcName = false;
            for (GraphElementClassData vc2 : unorderedVcList) {
              if (vc2.getQualifiedName().equals(superClass)) {
                definedVcName = true;
                break;
              }
            }
            if (!definedVcName) {
              throw new GraphIOException("VertexClass " + superClass + " does not exist");
            }
          }
        }
      }
    }
    this.vertexClassBuffer.put(this.graphClass.name, orderedVcList);
  }

  private void sortEdgeClasses() throws GraphIOException {
    List<GraphElementClassData> orderedEcList, unorderedEcList;
    Set<String> orderedEcNames = new TreeSet<String>();
    GraphElementClassData ec;
    boolean definedEcName;
    unorderedEcList = this.edgeClassBuffer.get(this.graphClass.name);
    orderedEcList = new ArrayList<GraphElementClassData>();
    while (!unorderedEcList.isEmpty()) {
      for (Iterator<GraphElementClassData> ecit = unorderedEcList.iterator(); ecit.hasNext(); ) {
        ec = ecit.next();
        if (orderedEcNames.containsAll(ec.directSuperClasses)) {
          orderedEcNames.add(ec.getQualifiedName());
          orderedEcList.add(ec);
          ecit.remove();
        } else {
          for (String superClass : ec.directSuperClasses) {
            if (orderedEcNames.contains(superClass)) {
              continue;
            }
            definedEcName = false;
            for (GraphElementClassData ec2 : unorderedEcList) {
              if (ec2.getQualifiedName().equals(superClass)) {
                definedEcName = true;
                break;
              }
            }
            if (!definedEcName) {
              throw new GraphIOException("EdgeClass " + superClass + " does not exist");
            }
          }
        }
      }
    }
    this.edgeClassBuffer.put(this.graphClass.name, orderedEcList);
  }

  /**
	 * checks if from- and to-VertexClasses given in EdgeClass definitions exist
	 */
  private void checkFromToVertexClasses() throws GraphIOException {
    boolean existingFromVertexClass;
    boolean existingToVertexClass;
    for (Entry<String, List<GraphElementClassData>> graphClassEdge : this.edgeClassBuffer.entrySet()) {
      for (GraphElementClassData ec : graphClassEdge.getValue()) {
        existingFromVertexClass = false;
        existingToVertexClass = false;
        for (Entry<String, List<GraphElementClassData>> graphClassVertex : this.vertexClassBuffer.entrySet()) {
          for (GraphElementClassData vc : graphClassVertex.getValue()) {
            if (ec.fromVertexClassName.equals(vc.getQualifiedName()) || ec.fromVertexClassName.equals("Vertex")) {
              existingFromVertexClass = true;
            }
            if (ec.toVertexClassName.equals(vc.getQualifiedName()) || ec.toVertexClassName.equals("Vertex")) {
              existingToVertexClass = true;
            }
            if (existingFromVertexClass && existingToVertexClass) {
              break;
            }
          }
          if (existingFromVertexClass && existingToVertexClass) {
            break;
          }
        }
        if (!existingFromVertexClass) {
          throw new GraphIOException("FromVertexClass " + ec.fromVertexClassName + " at EdgeClass " + ec.getQualifiedName() + " + does not exist");
        }
        if (!existingToVertexClass) {
          throw new GraphIOException("ToVertexClass " + ec.toVertexClassName + " at EdgeClass " + ec.getQualifiedName() + " does not exist");
        }
      }
    }
  }

  private static class EnumDomainData {
    String simpleName;

    String packageName;

    List<String> enumConstants;

    EnumDomainData(String packageName, String simpleName, List<String> enumConstants) {
      this.packageName = packageName;
      this.simpleName = simpleName;
      this.enumConstants = enumConstants;
    }
  }

  private static class RecordDomainData {
    String simpleName;

    String packageName;

    List<ComponentData> components;

    RecordDomainData(String packageName, String simpleName, List<ComponentData> components) {
      this.packageName = packageName;
      this.simpleName = simpleName;
      this.components = components;
    }
  }

  private static class ComponentData {
    String name;

    List<String> domainDescription;
  }

  private static class AttributeData {
    String name;

    List<String> domainDescription;

    String defaultValue;
  }

  private static class GraphClassData {
    Set<Constraint> constraints = new HashSet<Constraint>(1);

    String name;

    boolean isAbstract = false;

    List<AttributeData> attributes = new ArrayList<AttributeData>();
  }

  protected class GraphElementClassData {
    protected String simpleName;

    protected String packageName;

    public String getQualifiedName() {
      return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/left.java
      GraphIO.this.toQNameString(this.packageName, this.simpleName)
=======
      toQNameString(packageName, simpleName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/GraphIO.java/right.java
      ;
    }

    public boolean isAbstract = false;

    public List<String> directSuperClasses = new LinkedList<String>();

    public String fromVertexClassName;

    public int[] fromMultiplicity = { 1, Integer.MAX_VALUE };

    public String fromRoleName = "";

    protected Set<String> redefinedFromRoles = null;

    public AggregationKind fromAggregation;

    public String toVertexClassName;

    public int[] toMultiplicity = { 1, Integer.MAX_VALUE };

    public String toRoleName = "";

    protected Set<String> redefinedToRoles = null;

    public AggregationKind toAggregation;

    public List<AttributeData> attributes = new ArrayList<AttributeData>();

    public Set<Constraint> constraints = new HashSet<Constraint>(1);
  }
}