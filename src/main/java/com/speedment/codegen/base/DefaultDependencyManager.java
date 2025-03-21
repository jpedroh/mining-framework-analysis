package com.speedment.codegen.base;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import static com.speedment.codegen.util.Formatting.*;

/**
 * Default implementation of the {@link DependencyManager} interface.
 * 
 * @author Emil Forslund
 */
public class DefaultDependencyManager implements DependencyManager {
  private final Set<String> dependencies = new HashSet<>();

  private final Set<String> ignorePackages;

  /**
	 * Initalises the DependencyManager.
	 */
  public DefaultDependencyManager() {
    ignorePackages = new HashSet<>();
  }

  /**
	 * Initalises the DependencyManager.
     * 
	 * @param ignoredPackage  a package that should be on the ignore list
	 */
  public DefaultDependencyManager(String ignoredPackage) {
    ignorePackages = new HashSet<>();
    ignorePackages.add(ignoredPackage);
  }

  /**
	 * Initalises the DependencyManager.
     * 
	 * @param ignoredPackages  packages that should be ignored
	 */
  public DefaultDependencyManager(String[] ignoredPackages) {
    ignorePackages = Arrays.stream(ignoredPackages).collect(Collectors.toSet());
  }

  /**
	 * Initalises the DependencyManager.
     * 
	 * @param ignoredPackage   a package that should be on the ignore list
	 * @param ignoredPackages  more packages that should be on the ignore list
	 */
  public DefaultDependencyManager(String ignoredPackage, String... ignoredPackages) {
    this(ignoredPackages);
    ignorePackages.add(ignoredPackage);
  }

  /**
	 * Adds the specified package to the ignore list. This is the opposite as
	 * calling <code>acceptPackage</code>.
     * 
	 * @param packageName  the full name of the package
	 */
  @Override public void ignorePackage(String packageName) {
    ignorePackages.add(packageName);
  }

  /**
	 * Removes the specified package from the ignore list. This is the opposite 
	 * as calling <code>ignorePackage</code>.
     * 
	 * @param packageName  the full name of the package
	 */
  @Override public void acceptPackage(String packageName) {
    ignorePackages.removeIf((p) -> packageName.startsWith(p + DOT));
  }

  /**
	 * Returns true if the specified class belongs to a package that is on the
	 * ignore list.
     * 
	 * @param fullname  the full name of a package or a class
	 * @return          true if it should be ignored as a dependency
	 */
  @Override public boolean isIgnored(String fullname) {
    return ignorePackages.stream().anyMatch((p) -> fullname.startsWith(p + DOT) || fullname.equals(p));
  }

  /**
     * Add the specified dependency to the manager. If the dependency is 
     * redundant, false is returned and the dependency is not added.
     * 
     * @param fullname  the full name of the dependency
     * @return          true if the dependency was accepted
     */
  @Override public boolean load(String fullname) {
    if (isLoaded(fullname)) {
      return false;
    } else {
      dependencies.add(fullname);
      return true;
    }
  }

  /**
     * Checks if the specified dependency is already loaded. If the dependency
     * matches an ignored package, true is returned even if it is not
     * specifically loaded.
     * 
     * @param fullname  the name of the dependency
     * @return          true if it is loaded or redundant
     */
  @Override public boolean isLoaded(String fullname) {
    return dependencies.contains(fullname) || isIgnored(fullname);
  }

  /**
     * Clear the list of dependencies.
     */
  @Override public void clearDependencies() {
    dependencies.clear();
  }
}