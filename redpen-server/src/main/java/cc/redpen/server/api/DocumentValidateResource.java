package cc.redpen.server.api;
import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.parser.DocumentParserFactory;
import cc.redpen.parser.Parser;
import cc.redpen.validator.ValidationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Resource to validate documents.
 */
@Path(value = "/document") public class DocumentValidateResource {
  private static final Logger LOG = LogManager.getLogger(DocumentValidateResource.class);

  private final static String DEFAULT_INTERNAL_CONFIG_PATH = "/conf/redpen-conf.xml";

  @Context private ServletContext context;

  private RedPen redPen = null;

  private RedPen getRedPen() {
    if (redPen == null) {
      LOG.info("Starting Document Validator Server.");
      String configPath = null;
      if (context != null) {
        configPath = context.getInitParameter("redpen.conf.path");
      }
      if (configPath == null) {
        configPath = DEFAULT_INTERNAL_CONFIG_PATH;
      }
      LOG.info("Config Path is set to " + "\"" + configPath + "\"");
      try {
        redPen = new RedPen.Builder().setConfigPath(configPath).build();
        LOG.info("Document Validator Server is running.");
      } catch (RedPenException e) {
        LOG.error("Unable to initialize RedPen", e);
        throw new ExceptionInInitializerError(e);
      }
    }
    return redPen;
  }

  @Path(value = "/validate") @POST @Produces(value = MediaType.APPLICATION_JSON) public Response validateDocument(@FormParam(value = "textarea") @DefaultValue(value = "") String document) throws JSONException, RedPenException, UnsupportedEncodingException {
    LOG.info("Validating document");
    RedPen server = getRedPen();
    JSONObject json = new JSONObject();
    json.put("document", document);
    Parser parser = DocumentParserFactory.generate(Parser.Type.PLAIN, server.getConfiguration(), new DocumentCollection.Builder());
    Document fileContent = parser.generateDocument(new ByteArrayInputStream(document.getBytes("UTF-8")));
    DocumentCollection d = new DocumentCollection();
    d.addDocument(fileContent);
    List<ValidationError> errors = server.check(d);
    JSONArray jsonErrors = new JSONArray();
    for (ValidationError error : errors) {
      JSONObject jsonError = new JSONObject();
      if (error.getSentence().isPresent()) {
        jsonError.put("sentence", error.getSentence().get().content);
      }
      jsonError.put("message", error.getMessage());
      jsonErrors.put(jsonError);
    }
    json.put("errors", jsonErrors);
    return Response.ok().entity(json).build();
  }
}