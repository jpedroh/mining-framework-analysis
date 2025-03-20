package com.github.maven_nar;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Sets up a test to create
 * 
 * @author Mark Donszelmann
 */
public class Test implements Executable {
  /**
     * Name of the test to create
     * 
     * @required
     * @parameter default-value=""
     */
  @Parameter(required = true) private String name = null;

  /**
     * Type of linking used for this test Possible choices are: "shared" or "static". Defaults to "shared".
     * 
     * @parameter default-value=""
     */
  @Parameter(defaultValue = "shared") private String link = Library.SHARED;

  /**
     * When true run this test. Defaults to true;
     * 
     * @parameter expresssion=""
     */
  @Parameter(defaultValue = "true") private boolean run = true;

  /**
     * Arguments to be used for running this test. Defaults to empty list. This option is only used if run=true.
     * 
     * @parameter default-value=""
     */
  @Parameter private List args = new ArrayList();

  public final String getName() throws MojoFailureException {
    if (name == null) {
      throw new MojoFailureException("NAR: Please specify <Name> as part of <Test>");
    }
    return name;
  }

  public final String getLink() {
    return link;
  }

  public final boolean shouldRun() {
    return run;
  }

  public final List getArgs() {
    return args;
  }
}