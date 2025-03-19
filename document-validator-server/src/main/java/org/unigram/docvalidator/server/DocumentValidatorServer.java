package org.unigram.docvalidator.server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.unigram.docvalidator.ConfigurationLoader;
import org.unigram.docvalidator.server.util.ServerConfigurationLoader;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.DocumentValidator;
import org.unigram.docvalidator.util.Formatter;

/**
 * Document validator server.
 */
public class DocumentValidatorServer {
  private static Logger log = LogManager.getLogger(DocumentValidatorServer.class);

  private static DocumentValidatorServer documentValidatorServer;

  private DocumentValidator validator;

  private DVResource documentValidatorResource;

  private DocumentValidatorServer() throws DocumentValidatorException {
    ConfigurationLoader configLoader = new ServerConfigurationLoader();
    documentValidatorResource = configLoader.loadConfiguration(getClass().getClassLoader().getResourceAsStream("/conf/dv-conf.xml"));

<<<<<<< Unknown file: This is a bug in JDime.
=======
    ResultDistributor distributor = ResultDistributorFactory.createDistributor(Formatter.Type.PLAIN, System.out);
>>>>>>> /usr/src/app/output/recruit-tech/redpen/ea815d7ad42ed88e93bc78aac909f030439f77d3/document-validator-server/src/main/java/org/unigram/docvalidator/server/DocumentValidatorServer.java/right.java

    validator = new DocumentValidator.Builder().setResource(documentValidatorResource).build();
  }

  public DocumentValidator getValidator() {
    return validator;
  }

  public DVResource getDocumentValidatorResource() {
    return documentValidatorResource;
  }

  public static DocumentValidatorServer getInstance() throws DocumentValidatorException {
    if (documentValidatorServer == null) {
      initialize();
    }
    return documentValidatorServer;
  }

  public static void initialize() throws DocumentValidatorException {
    log.info("Initializing Document Validator");
    documentValidatorServer = new DocumentValidatorServer();
  }
}