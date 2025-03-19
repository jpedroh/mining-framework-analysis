package cc.redpen.server.api;
import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.ConfigurationLoader;
import cc.redpen.config.Symbol;
import cc.redpen.config.SymbolType;
import cc.redpen.formatter.Formatter;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.tokenizer.JapaneseTokenizer;
import cc.redpen.tokenizer.RedPenTokenizer;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.util.FormatterUtils;
import cc.redpen.util.LanguageDetector;
import cc.redpen.validator.ValidationError;
import org.apache.wink.common.annotations.Workspace;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Resource to validate documents.
 */
@Workspace(workspaceTitle = "RedPen", collectionTitle = "Document Validation") @Path(value = "/document") public class RedPenResource {
  private static final Logger LOG = LoggerFactory.getLogger(RedPenResource.class);

  private static final String DEFAULT_DOCUMENT_PARSER = "PLAIN";

  private static final String DEFAULT_LANG = "en";

  private static final String DEFAULT_CONFIGURATION = "en";

  private static final String DEFAULT_FORMAT = "json";

  static final String MIME_TYPE_XML = "application/xml; charset=utf-8";

  static final String MIME_TYPE_JSON = "application/json; charset=utf-8";

  static final String MIME_TYPE_PLAINTEXT = "text/plain; charset=utf-8";

  @Context private ServletContext context;

  /**
     * Detect language of document
     *
     * @param document the source text of the document
     */
  @Path(value = "/language") @POST @Produces(value = MediaType.APPLICATION_JSON) @WinkAPIDescriber.Description(value = "Detect language of document") public JSONObject detectLanguage(@FormParam(value = "document") @DefaultValue(value = "") String document) throws JSONException {
    String language = new LanguageDetector().detectLanguage(document);
    return new JSONObject().put("key", language);
  }

  /**
     * Validate a source document posted from a form
     *
     * @param document       the source text of the document
     * @param documentParser specifies one of PLAIN, WIKI, or MARKDOWN
     * @param lang           the source document language (en, ja, etc)
     * @param format         document format
     * @param config         the source of a RedPen XML configuration file
     * @return redpen validation errors
     * @throws RedPenException when failed to parse document
     */
  @Path(value = "/validate") @POST @Produces(value = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN }) @WinkAPIDescriber.Description(value = "Validate a document and return any redpen errors") public Response validateDocument(@FormParam(value = "document") @DefaultValue(value = "") String document, @FormParam(value = "documentParser") @DefaultValue(value = DEFAULT_DOCUMENT_PARSER) String documentParser, @FormParam(value = "lang") @DefaultValue(value = DEFAULT_CONFIGURATION) String lang, @FormParam(value = "format") @DefaultValue(value = DEFAULT_FORMAT) String format, @FormParam(value = "config") String config) throws RedPenException {
    LOG.info("Validating document");
    RedPen redPen;
    if (config == null) {
      redPen = new RedPenService(context).getRedPen(lang);
    } else {
      redPen = new RedPen(new ConfigurationLoader().secure().loadFromString(config));
    }
    Document parsedDocument = redPen.parse(DocumentParser.of(documentParser), document);
    List<ValidationError> errors = redPen.validate(parsedDocument);
    Formatter formatter = FormatterUtils.getFormatterByName(format);
    if (formatter == null) {
      throw new RedPenException("Unsupported format: " + format + " - please use xml, plain, plain2, json or json2");
    }
    return responseTyped(formatter.format(parsedDocument, errors), format);
  }

  static Response responseTyped(final String formatted, final String format) throws RedPenException {
    if (format.startsWith("xml")) {
      return Response.ok(formatted, RedPenResource.MIME_TYPE_XML).build();
    } else {
      if (format.startsWith("json")) {
        return Response.ok(formatted, RedPenResource.MIME_TYPE_JSON).build();
      } else {
        if (format.startsWith("plain")) {
          return Response.ok(formatted, RedPenResource.MIME_TYPE_PLAINTEXT).build();
        } else {
          throw new RedPenException("MIME type unknown with format: " + format);
        }
      }
    }
  }

  /**
     * Validate a request encoded in JSON. Valid properties are:
     * <p>
     * document : the source text of the document
     * documentParser : specifies one of PLAIN, WIKI, or MARKDOWN
     * lang : the source document language (en, ja, etc)
     * format : the format of the results, eg: json, json2, plain etc
     * config : the redpen validator configuration
     *
     * @param requestJSON the request, in JSON
     * @return redpen validation errors
     * @throws RedPenException when failed to validate the json
     */
  @Path(value = "/validate/json") @POST @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN }) @WinkAPIDescriber.Description(value = "Process a redpen JSON validation request and returns any redpen errors") public Response validateDocumentJSON(JSONObject requestJSON) throws RedPenException {
    LOG.info("Validating document using JSON request");
    String documentParser = getOrDefault(requestJSON, "documentParser", DEFAULT_DOCUMENT_PARSER);
    String documentText = getOrDefault(requestJSON, "document", "");
    String format = getOrDefault(requestJSON, "format", DEFAULT_FORMAT);
    String lang = DEFAULT_LANG;
    Map<String, Map<String, String>> properties = new HashMap<>();
    JSONObject config = null;
    if (requestJSON.has("config")) {
      try {
        config = requestJSON.getJSONObject("config");
        lang = getOrDefault(config, "lang", DEFAULT_LANG);
        if (config.has("validators")) {
          JSONObject validators = config.getJSONObject("validators");
          Iterator keyIter = validators.keys();
          while (keyIter.hasNext()) {
            String validator = String.valueOf(keyIter.next());
            Map<String, String> props = new HashMap<>();
            properties.put(validator, props);
            JSONObject validatorConfig = validators.getJSONObject(validator);
            if ((validatorConfig != null) && validatorConfig.has("properties")) {
              JSONObject validatorProps = validatorConfig.getJSONObject("properties");
              Iterator propsIter = validatorProps.keys();
              while (propsIter.hasNext()) {
                String propname = String.valueOf(propsIter.next());
                props.put(propname, validatorProps.getString(propname));
              }
            }
          }
        }
      } catch (Exception e) {
        LOG.error("Exception when processing JSON properties", e);
      }
    }
    RedPen redPen = new RedPenService(context).getRedPen(lang, properties);
    if ((config != null) && config.has("symbols")) {
      try {
        JSONObject symbols = config.getJSONObject("symbols");
        Iterator keyIter = symbols.keys();
        while (keyIter.hasNext()) {
          String symbolName = String.valueOf(keyIter.next());
          try {
            SymbolType symbolType = SymbolType.valueOf(symbolName);
            JSONObject symbolConfig = symbols.getJSONObject(symbolName);
            Symbol originalSymbol = redPen.getConfiguration().getSymbolTable().getSymbol(symbolType);
            if ((originalSymbol != null) && (symbolConfig != null) && symbolConfig.has("value")) {
              String value = symbolConfig.has("value") ? symbolConfig.getString("value") : String.valueOf(originalSymbol.getValue());
              boolean spaceBefore = symbolConfig.has("before_space") ? symbolConfig.getBoolean("before_space") : originalSymbol.isNeedBeforeSpace();
              boolean spaceAfter = symbolConfig.has("after_space") ? symbolConfig.getBoolean("after_space") : originalSymbol.isNeedAfterSpace();
              String invalidChars = symbolConfig.has("invalid_chars") ? symbolConfig.getString("invalid_chars") : String.valueOf(originalSymbol.getInvalidChars());
              if ((value != null) && !value.isEmpty()) {
                redPen.getConfiguration().getSymbolTable().overrideSymbol(new Symbol(symbolType, value.charAt(0), invalidChars, spaceBefore, spaceAfter));
              }
            }
          } catch (IllegalArgumentException iae) {
            LOG.error("Ignoring unknown SymbolType " + symbolName);
          }
        }
      } catch (Exception e) {
        LOG.error("Exception when processing JSON symbol overrides", e);
      }
    }
    Document parsedDocument = redPen.parse(DocumentParser.of(documentParser), documentText);
    List<ValidationError> errors = redPen.validate(parsedDocument);
    Formatter formatter = FormatterUtils.getFormatterByName(format);
    if (formatter == null) {
      throw new RedPenException("Unsupported format: " + format + " - please use xml, plain, plain2, json or json2");
    }
    return responseTyped(formatter.format(parsedDocument, errors), format);
  }

  /**
     * Tokenize some text and return the tokens
     *
     * @param document the source text of the document
     */
  @Path(value = "/tokenize") @POST @Produces(value = MediaType.APPLICATION_JSON) @WinkAPIDescriber.Description(value = "Tokenize a document") public JSONObject tokenize(@FormParam(value = "document") @DefaultValue(value = "") String document, @FormParam(value = "lang") @DefaultValue(value = DEFAULT_CONFIGURATION) String lang) throws JSONException {
    RedPenTokenizer tokenizer;
    switch (lang == null ? "en" : lang) {
      case "ja":
      tokenizer = new JapaneseTokenizer();
      break;
      default:
      tokenizer = new WhiteSpaceTokenizer();
      break;
    }
    return new JSONObject().put("tokens", tokenizer.tokenize(document == null ? "" : document));
  }

  private String getOrDefault(JSONObject json, String property, String defaultValue) {
    try {
      String value = json.getString(property);
      if (value != null) {
        return value;
      }
    } catch (Exception e) {
    }
    return defaultValue;
  }
}