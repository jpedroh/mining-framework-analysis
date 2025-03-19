package org.rdfhdt.hdt.hdt;
import org.rdfhdt.hdt.hdt.impl.HDTImpl;
import org.rdfhdt.hdt.hdt.impl.ModeOfLoading;
import org.rdfhdt.hdt.hdt.impl.TempHDTImpl;
import org.rdfhdt.hdt.options.HDTOptions;
import org.rdfhdt.hdt.options.HDTSpecification;

/**
 * Factory that creates HDT objects
 * 
 */
public class HDTFactory {
  private static TempDictTriplesFactory tempFactory;

  private HDTFactory() {
  }

  public static TempDictTriplesFactory getTempFactory() {
    if (tempFactory == null) {
      try {
        Class<?> managerImplClass = Class.forName("org.rdfhdt.hdtdisk.HDTDiskFactory");
        tempFactory = (TempDictTriplesFactory) managerImplClass.newInstance();
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Class org.rdfhdt.hdtdisk.HDTDiskFactory not found. Did you include the hdt-disk.jar in the classpath?");
      } catch (InstantiationException e) {
        throw new RuntimeException("Cannot create implementation for HDTDiskFactory. Does the class org.rdfhdt.hdtdisk.HDTDiskFactory implement TempDictTriplesFactory?");
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return tempFactory;
  }

  /**
	 * Creates a default HDT
	 * 
	 * @return HDT
	 */
  public static HDT createHDT() {
    return new HDTImpl(new HDTSpecification());
  }

  /**
	 * Creates an HDT with the specified spec
	 * 
	 * @return HDT
	 */
  public static HDT createHDT(HDTOptions spec) {
    return new HDTImpl(spec);
  }

  /**
	 * Creates a TempHDT with the specified spec, baseUri and ModeOfLoading.
	 * 
	 * ModeOfLoading can be null if the TempHDT is not meant to be populated from RDF
	 * (i.e. ModHDTImporter object not used).
	 * 
	 * @return TempHDT
	 */
  public static TempHDT createTempHDT(HDTSpecification spec, String baseUri, ModeOfLoading modeOfLoading) {
    return new TempHDTImpl(spec, baseUri, modeOfLoading);
  }
}