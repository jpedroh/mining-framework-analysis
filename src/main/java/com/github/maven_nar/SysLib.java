package com.github.maven_nar;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.tools.ant.Project;
import com.github.maven_nar.cpptasks.CUtil;
import com.github.maven_nar.cpptasks.types.LibraryTypeEnum;
import com.github.maven_nar.cpptasks.types.SystemLibrarySet;

/**
 * Keeps info on a system library
 *
 * @author Mark Donszelmann
 */
public class SysLib {
  /**
   * Name of the system library
   */
  @Parameter(required = true) private String name;

  /**
   * Type of linking for this system library
   */
  @Parameter(defaultValue = "shared") private final String type = Library.SHARED;

  public final SystemLibrarySet getSysLibSet(final Project antProject) throws MojoFailureException {
    if (this.name == null) {
      throw new MojoFailureException("NAR: Please specify <Name> as part of <SysLib>");
    }
    final SystemLibrarySet sysLibSet = new SystemLibrarySet();
    sysLibSet.setProject(antProject);
    sysLibSet.setLibs(new CUtil.StringArrayBuilder(this.name));
    final LibraryTypeEnum sysLibType = new LibraryTypeEnum();
    sysLibType.setValue(this.type);
    sysLibSet.setType(sysLibType);
    return sysLibSet;
  }

  @Override public String toString() {
    return this.name + " (" + this.type + ")";
  }
}