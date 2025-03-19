package com.bunjlabs.fuga.resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class ResourceManager {
  private static final String APP_DIR = "app";

  /**
     * Returns input stream of given resource from classpath.
     *
     * If specified resource does not exists, null value will be returned.
     *
     * @param path Path to the resource.
     * @return input stream of resource or null.
     * @throws FileNotFoundException if any specified file not founded 
     */
  protected InputStream loadFromClasspath(String... path) throws FileNotFoundException {
    String name = String.join(File.separator, path);
    if (name == null || name.isEmpty()) {
      return null;
    }
    InputStream is;
    is = ClassLoader.getSystemClassLoader().getResourceAsStream(name);
    if (is == null) {
      throw new FileNotFoundException(name);
    }
    return is;
  }

  /**
     * Returns input stream of given resource.
     *
     * This method at first find resource in current <code>app</code> dir. If it
     * fails, it try to find resource from class path. If specified resource
     * does not exists, null value will be returned.
     *
     * @param path Path to the resource.
     * @return input stream of resource or null.
     * @throws FileNotFoundException if any specified file not founded 
     */
  protected InputStream load(String... path) throws FileNotFoundException {
    String name = String.join(File.separator, path);
    if (name == null || name.isEmpty()) {
      return null;
    }
    InputStream is;
    String resourcePathStr = "./" + APP_DIR + (name.startsWith("/") ? name : (File.separator + name));
    try {
      is = new FileInputStream(resourcePathStr);
    } catch (FileNotFoundException ex) {
      return loadFromClasspath(name);
    }
    return is;
  }

  /**
     * Returns new resource representer for the given path.
     *
     * @param path Path to the represented resources.
     * @return resource representer for the given path.
     */
  public ResourceRepresenter getResourceRepresenter(String path) {
    return new ResourceRepresenter(this, path);
  }
}