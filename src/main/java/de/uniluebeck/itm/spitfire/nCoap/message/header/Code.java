package de.uniluebeck.itm.spitfire.nCoap.message.header;
import de.uniluebeck.itm.spitfire.nCoap.message.options.OptionRegistry;
import java.util.Arrays;
import java.util.List;

/**
 * This enumeration contains all defined message codes (i.e. methods for requests and status for responses)
 * in CoAPs draft v7
 *
 * @author Oliver Kleine
*/public enum Code {
  EMPTY(0, new OptionRegistry.OptionName[0]),
  GET(1, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.URI_HOST, OptionRegistry.OptionName.URI_PATH, OptionRegistry.OptionName.URI_PORT, OptionRegistry.OptionName.URI_QUERY, OptionRegistry.OptionName.PROXY_URI, OptionRegistry.OptionName.ACCEPT, OptionRegistry.OptionName.ETAG, OptionRegistry.OptionName.TOKEN }),
  POST(2, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.URI_HOST, OptionRegistry.OptionName.URI_PATH, OptionRegistry.OptionName.URI_PORT, OptionRegistry.OptionName.URI_QUERY, OptionRegistry.OptionName.TOKEN }),
  PUT(3, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.URI_HOST, OptionRegistry.OptionName.URI_PATH, OptionRegistry.OptionName.URI_PORT, OptionRegistry.OptionName.URI_QUERY, OptionRegistry.OptionName.CONTENT_TYPE, OptionRegistry.OptionName.IF_MATCH, OptionRegistry.OptionName.IF_NONE_MATCH, OptionRegistry.OptionName.TOKEN }),
  DELETE(4, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.URI_HOST, OptionRegistry.OptionName.URI_PATH, OptionRegistry.OptionName.URI_PORT, OptionRegistry.OptionName.URI_QUERY, OptionRegistry.OptionName.TOKEN }),
  CREATED_201(65, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.CONTENT_TYPE, OptionRegistry.OptionName.LOCATION_PATH, OptionRegistry.OptionName.LOCATION_QUERY, OptionRegistry.OptionName.TOKEN }),
  DELETED_202(66, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.CONTENT_TYPE, OptionRegistry.OptionName.TOKEN }),
  VALID_203(67, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.ETAG, OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  CHANGED_204(68, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.CONTENT_TYPE, OptionRegistry.OptionName.TOKEN }),
  CONTENT_205(69, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.CONTENT_TYPE, OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.ETAG, OptionRegistry.OptionName.TOKEN }),
  BAD_REQUEST_400(128, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  UNAUTHORIZED_401(129, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  BAD_OPTION_402(130, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  FORBIDDEN_403(131, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  NOT_FOUND_404(132, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  METHOD_NOT_ALLOWED_405(133, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  PRECONDITION_FAILED_412(140, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  REQUEST_ENTITY_TOO_LARGE_413(141, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  UNSUPPORTED_MEDIA_TYPE_415(143, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  INTERNAL_SERVER_ERROR_500(160, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  NOT_IMPLEMENTED_501(161, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  BAD_GATEWAY_502(162, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  SERVICE_UNAVAILABLE_503(163, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  GATEWAY_TIMEOUT_504(164, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN }),
  PROXYING_NOT_SUPPORTED_505(165, new OptionRegistry.OptionName[] { OptionRegistry.OptionName.MAX_AGE, OptionRegistry.OptionName.TOKEN })
  ;

  /**
     * The corresponding numerical CoAP message code
     */
  public final int number;

  private final List<OptionRegistry.OptionName> allowedOptions;

  Code(int number, OptionRegistry.OptionName[] allowedOptions) {
    this.number = number;
    this.allowedOptions = Arrays.asList(allowedOptions);
  }

  /**
     * This method is to check whether the specified option is meaningful in the context
     * of the current message code
     * @param opt_name
     * @return <code>true</code> if the given is meaningful in the context of the message code,
     * <code>false</false> otherwise
     */
  public boolean isMeaningful(OptionRegistry.OptionName opt_name) {
    return allowedOptions.contains(opt_name);
  }

  /**
     * This method indicates wheter the message code refers to a request or a response
     * @return <code>true</code> in case of a request code, <code>false</code> in case of response code
     */
  public boolean isRequest() {
    return (number < 5 && number > 0);
  }

  /**
     * This method indicates whether a message may contain payload
     * @return <code>true</code> if payload is allowed, <code>false</code> otherwise
     */
  public boolean allowsPayload() {
    return !(number == Code.GET.number || number == Code.DELETE.number);
  }

  public static Code getCodeFromNumber(int number) {
    for (Code c : Code.values()) {
      if (c.number == number) {
        return c;
      }
    }
    return null;
  }
}