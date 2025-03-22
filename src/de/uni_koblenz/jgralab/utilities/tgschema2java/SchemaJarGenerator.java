package de.uni_koblenz.jgralab.utilities.tgschema2java;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SchemaJarGenerator {
  private String path;

  private String packageName;

  private String jarFileName;

  private int storeMethod;

  /**
	 * Creates a new SchemaJarGenerator.
	 * 
	 * @param pathToFiles
	 *            the path to the directory where the files that should be part
	 *            of the jar are located excluding the package name
	 * @param packageName
	 *            the name of the package the fiels are located in
	 * @param jarFileName
	 *            the name of the jar-file to create
	 */
  public SchemaJarGenerator(String pathToFiles, String packageName, String jarFileName) {
    this(pathToFiles, packageName, jarFileName, false);
  }

  /**
	 * Creates a new SchemaJarGenerator.
	 * 
	 * @param pathToFiles
	 *            the path to the directory where the files that should be part
	 *            of the jar are located excluding the package name
	 * @param packageName
	 *            the name of the package the fiels are located in
	 * @param jarFileName
	 *            the name of the jar-file to create
	 * @param compress
	 *            toggles wether the contents of thew jar should be compressed
	 *            or not
	 */
  public SchemaJarGenerator(String pathToFiles, String packageName, String jarFileName, boolean compress) {
    path = pathToFiles;
    this.packageName = packageName;
    this.jarFileName = jarFileName;
    if (compress) {
      storeMethod = ZipEntry.STORED;
    } else {
      storeMethod = ZipEntry.DEFLATED;
    }
  }

  public void createJar() throws Exception {
    ZipOutputStream zipStream = null;
    try {
      System.out.println("Jar file name is: " + jarFileName);
      zipStream = new ZipOutputStream(new FileOutputStream(path + "/" + jarFileName));
      zipStream.setMethod(storeMethod);
      String ifacePackageDir = packageName.replaceAll("\\.", "/");
      File interfaceDir = new File(path + "/" + ifacePackageDir);
      putDirInJar(zipStream, interfaceDir.getAbsolutePath(), ifacePackageDir + "/");
      putDirInJar(zipStream, interfaceDir.getAbsolutePath() + "/impl", ifacePackageDir + "/impl/");
      zipStream.closeEntry();
    }  finally {
      zipStream.close();
    }
  }

  private void putDirInJar(ZipOutputStream zipStream, String dirPath, String pathInJar) throws IOException {
    ZipEntry interfaceDirEntry = new ZipEntry(pathInJar);
    zipStream.putNextEntry(interfaceDirEntry);
    File[] interfaces = new File(dirPath).listFiles();
    for (File currentInterface : interfaces) {
      if (currentInterface.getName().startsWith(".")) {
        continue;
      }
      if (currentInterface.isDirectory()) {
      } else {
        ZipEntry entry = new ZipEntry(pathInJar + "/" + currentInterface.getName());
        zipStream.putNextEntry(entry);
        FileInputStream in = null;
        try {
          in = new FileInputStream(currentInterface);
          int len = 0;
          byte[] buf = new byte[4096];
          while ((len = in.read(buf)) > 0) {
            zipStream.write(buf, 0, len);
          }
        }  finally {
          in.close();
        }
      }
    }
    zipStream.closeEntry();
  }
}