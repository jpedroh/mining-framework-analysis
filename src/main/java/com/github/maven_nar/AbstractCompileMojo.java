package com.github.maven_nar;
import java.util.Collections;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.tools.ant.Project;

/**
 * @author Mark Donszelmann
 */
public abstract class AbstractCompileMojo extends AbstractDependencyMojo {
  /**
   * C++ Compiler
   */
  @Parameter private Cpp cpp;

  /**
   * C Compiler
   */
  @Parameter private C c;

  /**
   * Fortran Compiler
   */
  @Parameter private Fortran fortran;

  /**
   * Resource Compiler
   */
  @Parameter private Resource resource;

  /**
     * Assembler Compiler
     *
     */
  @Parameter(defaultValue = "") private 
<<<<<<< /usr/src/app/output/maven-nar/nar-maven-plugin/f094671078becaaf84107cb3859f4a41a66be2af/src/main/java/com/github/maven_nar/AbstractCompileMojo.java/left.java
  String
=======
  Assembler
>>>>>>> /usr/src/app/output/maven-nar/nar-maven-plugin/f094671078becaaf84107cb3859f4a41a66be2af/src/main/java/com/github/maven_nar/AbstractCompileMojo.java/right.java
   
<<<<<<< /usr/src/app/output/maven-nar/nar-maven-plugin/f094671078becaaf84107cb3859f4a41a66be2af/src/main/java/com/github/maven_nar/AbstractCompileMojo.java/left.java
  fortifyID
=======
  assembler
>>>>>>> /usr/src/app/output/maven-nar/nar-maven-plugin/f094671078becaaf84107cb3859f4a41a66be2af/src/main/java/com/github/maven_nar/AbstractCompileMojo.java/right.java
  ;

  /**
   * IDL Compiler
   */
  @Parameter private IDL idl;

  /**
   * Message Compiler
   */
  @Parameter private Message message;

  /**
   * By default NAR compile will attempt to compile using all known compilers
   * against files in the directories specified by convention.
   * This allows configuration to a reduced set, you will have to specify each
   * compiler to use in the configuration.
   */
  @Parameter(defaultValue = "false") protected boolean onlySpecifiedCompilers;

  /**
   * Do we log commands that is executed to produce the end-result?
   * Conception was to allow eclipse to sniff out include-paths from compile.
   */
  @Parameter protected int commandLogLevel = Project.MSG_VERBOSE;

  /**
   * Maximum number of Cores/CPU's to use. 0 means unlimited.
   */
  @Parameter private int maxCores = 0;

  /**
   * Fail on compilation/linking error.
   */
  @Parameter(defaultValue = "true", required = true) private boolean failOnError;

  /**
   * Sets the type of runtime library, possible values "dynamic", "static".
   */
  @Parameter(defaultValue = "dynamic", required = true) private String runtime;

  /**
   * Set use of libtool. If set to true, the "libtool " will be prepended to the
   * command line for compatible
   * processors.
   */
  @Parameter(defaultValue = "false", required = true) private boolean libtool;

  /**
   * Forces project to specify all it's dependencies and not inherit transitive  
   * dependencies.
   @since 3.5.3
   */
  @Parameter(defaultValue = "false") protected boolean directDepsOnly;

  /**
   * List of tests to create
   */
  @Parameter private List tests;

  /**
   * Java info for includes and linking
   */
  @Parameter private Java java;

  /**
   * Flag to cpptasks to indicate whether linker options should be decorated or
   * not
   */
  @Parameter protected boolean decorateLinkerOptions;

  private List dependencyLibOrder;

  private Project antProject;

  protected final boolean failOnError(final AOL aol) throws MojoExecutionException {
    return getNarInfo().getProperty(aol, "failOnError", this.failOnError);
  }

  protected final Project getAntProject() {
    if (this.antProject == null) {
      this.antProject = new Project();
      this.antProject.setName("NARProject");
      this.antProject.addBuildListener(new NarLogger(getLog()));
    }
    return this.antProject;
  }

  protected final C getC() {
    if (this.c == null && !this.onlySpecifiedCompilers) {
      setC(new C());
    }
    return this.c;
  }

  protected final Cpp getCpp() {
    if (this.cpp == null && !this.onlySpecifiedCompilers) {
      setCpp(new Cpp());
    }
    return this.cpp;
  }

  protected final List getDependencyLibOrder() {
    return this.dependencyLibOrder;
  }

  protected final Fortran getFortran() {
    if (this.fortran == null && !this.onlySpecifiedCompilers) {
      setFortran(new Fortran());
    }
    return this.fortran;
  }

  protected final Assembler getAssembler() {
    if (assembler == null) {
      assembler = new Assembler();
    }
    assembler.setAbstractCompileMojo(this);
    return assembler;
  }

  protected final IDL getIdl() {
    if (this.idl == null && !this.onlySpecifiedCompilers) {
      setIdl(new IDL());
    }
    return this.idl;
  }

  protected final Java getJava() {
    if (this.java == null) {
      this.java = new Java();
    }
    this.java.setAbstractCompileMojo(this);
    return this.java;
  }

  protected final int getMaxCores(final AOL aol) throws MojoExecutionException {
    return getNarInfo().getProperty(aol, "maxCores", this.maxCores);
  }

  /**
   * Get value of the directDepsOnly flag.
   * @return {@code true} if directDepsOnly is true, {@code false} otherwise.
   * @since 3.5.3
   */
  protected boolean getDirectDepsOnly() {
    return this.directDepsOnly;
  }

  protected final Message getMessage() {
    if (this.message == null && !this.onlySpecifiedCompilers) {
      setMessage(new Message());
    }
    return this.message;
  }

  protected final String getOutput(final AOL aol, final String type) throws MojoExecutionException {
    return getNarInfo().getOutput(aol, getOutput(!Library.EXECUTABLE.equals(type)));
  }

  protected final Resource getResource() {
    if (this.resource == null && !this.onlySpecifiedCompilers) {
      setResource(new Resource());
    }
    return this.resource;
  }

  protected final String getRuntime(final AOL aol) throws MojoExecutionException {
    return getNarInfo().getProperty(aol, "runtime", this.runtime);
  }

  protected final List getTests() {
    if (this.tests == null) {
      this.tests = Collections.emptyList();
    }
    return this.tests;
  }

  public void setC(final C c) {
    this.c = c;
    c.setAbstractCompileMojo(this);
  }

  public void setCpp(final Cpp cpp) {
    this.cpp = cpp;
    cpp.setAbstractCompileMojo(this);
  }

  protected final String getfortifyID() {
    return this.fortifyID;
  }

  public final void setDependencyLibOrder(final List order) {
    this.dependencyLibOrder = order;
  }

  public void setFortran(final Fortran fortran) {
    this.fortran = fortran;
    fortran.setAbstractCompileMojo(this);
  }

  public void setIdl(final IDL idl) {
    this.idl = idl;
    idl.setAbstractCompileMojo(this);
  }

  public void setMessage(final Message message) {
    this.message = message;
    message.setAbstractCompileMojo(this);
  }

  public void setResource(final Resource resource) {
    this.resource = resource;
    resource.setAbstractCompileMojo(this);
  }

  protected final boolean useLibtool(final AOL aol) throws MojoExecutionException {
    return getNarInfo().getProperty(aol, "libtool", this.libtool);
  }
}