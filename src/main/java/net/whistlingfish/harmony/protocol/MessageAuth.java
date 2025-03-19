package net.whistlingfish.harmony.protocol;
import java.util.Map;
import static java.lang.String.format;
import java.util.UUID;
import static net.whistlingfish.harmony.Jackson.OBJECT_MAPPER;
import org.jivesoftware.smack.packet.IQ;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;

public class MessageAuth {
  public static String MIME_TYPE = "vnd.logitech.connect/vnd.logitech.pair";

  public static class AuthRequest extends OAStanza {
    public AuthRequest() {
      super(MIME_TYPE);
      setType(IQ.Type.get);
    }

    @Override protected Map<String, Object> getChildElementPairs() {
      return ImmutableMap.<String, Object>builder().put(
<<<<<<< /usr/src/app/output/tuck182/harmony-java-client/fb735c719e4cfcc69791ce5365a58de81b892eab/src/main/java/net/whistlingfish/harmony/protocol/MessageAuth.java/left.java
      "method"
=======
      loginToken != null ? "token" : "method"
>>>>>>> /usr/src/app/output/tuck182/harmony-java-client/fb735c719e4cfcc69791ce5365a58de81b892eab/src/main/java/net/whistlingfish/harmony/protocol/MessageAuth.java/right.java
      , 
<<<<<<< /usr/src/app/output/tuck182/harmony-java-client/fb735c719e4cfcc69791ce5365a58de81b892eab/src/main/java/net/whistlingfish/harmony/protocol/MessageAuth.java/left.java
      "pair"
=======
      loginToken != null ? loginToken.getUserAuthToken() : "pair"
>>>>>>> /usr/src/app/output/tuck182/harmony-java-client/fb735c719e4cfcc69791ce5365a58de81b892eab/src/main/java/net/whistlingfish/harmony/protocol/MessageAuth.java/right.java
      ).put("name", generateUniqueId() + "#" + getDeviceIdentifier()).build();
    }

    private String generateUniqueId() {
      return BaseEncoding.base64().encode(UUID.randomUUID().toString().getBytes());
    }

    private String getDeviceIdentifier() {
      return "iOS6.0.1#iPhone";
    }
  }

  public static class AuthReply extends OAStanza {
    private String serverIdentity;

    private String hubId;

    private String identity;

    private String status;

    private Map<String, String> protocolVersion;

    private Map<String, String> hubProfiles;

    private String productId;

    private String friendlyName;

    @JsonCreator public AuthReply() {
      super(MIME_TYPE);
    }

    @Override protected Map<String, Object> getChildElementPairs() {
      return ImmutableMap.<String, Object>builder().put("serverIdentity", serverIdentity).put("hubId", hubId).put("identity", identity).put("status", status).put("protocolVersion", protocolVersion).put("hubProfiles", hubProfiles).put("productId", productId).put("friendlyName", friendlyName).build();
    }

    public String getUsername() {
      return format("%s@connect.logitech.com/gatorade", identity);
    }

    public String getPassword() {
      return identity;
    }

    public String getServerIdentity() {
      return serverIdentity;
    }

    public String getHubId() {
      return hubId;
    }

    public String getIdentity() {
      return identity;
    }

    public String getStatus() {
      return status;
    }

    public Map<String, String> getProtocolVersion() {
      return protocolVersion;
    }

    public Map<String, String> getHubProfiles() {
      return hubProfiles;
    }

    public String getProductId() {
      return productId;
    }

    public String getFriendlyName() {
      return friendlyName;
    }
  }

  public static class AuthReplyParser extends OAReplyParser {
    @Override public IQ parseReplyContents(String statusCode, String errorString, String contents) {
      return OBJECT_MAPPER.convertValue(parseKeyValuePairs(statusCode, errorString, contents), AuthReply.class);
    }
  }
}