package io.jsonwebtoken.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.CompressionCodecResolver;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtHandler;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import io.jsonwebtoken.impl.crypto.JwtSignatureValidator;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Objects;
import io.jsonwebtoken.lang.Strings;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.MissingClaimException;

@SuppressWarnings(value = { "unchecked" }) public class DefaultJwtParser implements JwtParser {
  private static final String ISO_8601_FORMAT = "yyyy-MM-dd\'T\'HH:mm:ssZ";

  private ObjectMapper objectMapper = new ObjectMapper();

  private byte[] keyBytes;

  private Key key;

  private SigningKeyResolver signingKeyResolver;

  private CompressionCodecResolver compressionCodecResolver = new DefaultCompressionCodecResolver();

  @Override public JwtParser setSigningKey(byte[] key) {
    Assert.notEmpty(key, "signing key cannot be null or empty.");
    this.keyBytes = key;
    return this;
  }

  @Override public JwtParser setSigningKey(String base64EncodedKeyBytes) {
    Assert.hasText(base64EncodedKeyBytes, "signing key cannot be null or empty.");
    this.keyBytes = TextCodec.BASE64.decode(base64EncodedKeyBytes);
    return this;
  }

  @Override public JwtParser setSigningKey(Key key) {
    Assert.notNull(key, "signing key cannot be null.");
    this.key = key;
    return this;
  }

  @Override public JwtParser setSigningKeyResolver(SigningKeyResolver signingKeyResolver) {
    Assert.notNull(signingKeyResolver, "SigningKeyResolver cannot be null.");
    this.signingKeyResolver = signingKeyResolver;
    return this;
  }

  @Override public JwtParser setCompressionCodecResolver(CompressionCodecResolver compressionCodecResolver) {
    Assert.notNull(compressionCodecResolver, "compressionCodecResolver cannot be null.");
    this.compressionCodecResolver = compressionCodecResolver;
    return this;
  }

  @Override public boolean isSigned(String jwt) {
    if (jwt == null) {
      return false;
    }
    int delimiterCount = 0;
    for (int i = 0; i < jwt.length(); i++) {
      char c = jwt.charAt(i);
      if (delimiterCount == 2) {
        return !Character.isWhitespace(c) && c != SEPARATOR_CHAR;
      }
      if (c == SEPARATOR_CHAR) {
        delimiterCount++;
      }
    }
    return false;
  }

  @Override public Jwt parse(String jwt) throws ExpiredJwtException, MalformedJwtException, SignatureException {
    Assert.hasText(jwt, "JWT String argument cannot be null or empty.");
    String base64UrlEncodedHeader = null;
    String base64UrlEncodedPayload = null;
    String base64UrlEncodedDigest = null;
    int delimiterCount = 0;
    StringBuilder sb = new StringBuilder(128);
    for (char c : jwt.toCharArray()) {
      if (c == SEPARATOR_CHAR) {
        String token = Strings.clean(sb.toString());
        if (delimiterCount == 0) {
          base64UrlEncodedHeader = token;
        } else {
          if (delimiterCount == 1) {
            base64UrlEncodedPayload = token;
          }
        }
        delimiterCount++;
        sb = new StringBuilder(128);
      } else {
        sb.append(c);
      }
    }
    if (delimiterCount != 2) {
      String msg = "JWT strings must contain exactly 2 period characters. Found: " + delimiterCount;
      throw new MalformedJwtException(msg);
    }
    if (sb.length() > 0) {
      base64UrlEncodedDigest = sb.toString();
    }
    if (base64UrlEncodedPayload == null) {
      throw new MalformedJwtException("JWT string \'" + jwt + "\' is missing a body/payload.");
    }
    Header header = null;
    CompressionCodec compressionCodec = null;
    if (base64UrlEncodedHeader != null) {
      String origValue = TextCodec.BASE64URL.decodeToString(base64UrlEncodedHeader);
      Map<String, Object> m = readValue(origValue);
      if (base64UrlEncodedDigest != null) {
        header = new DefaultJwsHeader(m);
      } else {
        header = new DefaultHeader(m);
      }
      compressionCodec = compressionCodecResolver.resolveCompressionCodec(header);
    }
    String payload = TextCodec.BASE64URL.decodeToString(base64UrlEncodedPayload);
    if (compressionCodec != null) {
      byte[] decompressed = compressionCodec.decompress(TextCodec.BASE64URL.decode(base64UrlEncodedPayload));
      payload = new String(decompressed, Strings.UTF_8);
    } else {
      payload = TextCodec.BASE64URL.decodeToString(base64UrlEncodedPayload);
    }
    Claims claims = null;
    if (payload.charAt(0) == '{' && payload.charAt(payload.length() - 1) == '}') {
      Map<String, Object> claimsMap = readValue(payload);
      claims = new DefaultClaims(claimsMap);
    }
    if (base64UrlEncodedDigest != null) {
      JwsHeader jwsHeader = (JwsHeader) header;
      SignatureAlgorithm algorithm = null;
      if (header != null) {
        String alg = jwsHeader.getAlgorithm();
        if (Strings.hasText(alg)) {
          algorithm = SignatureAlgorithm.forName(alg);
        }
      }
      if (algorithm == null || algorithm == SignatureAlgorithm.NONE) {
        String msg = "JWT string has a digest/signature, but the header does not reference a valid signature " + "algorithm.";
        throw new MalformedJwtException(msg);
      }
      if (key != null && keyBytes != null) {
        throw new IllegalStateException("A key object and key bytes cannot both be specified. Choose either.");
      } else {
        if ((key != null || keyBytes != null) && signingKeyResolver != null) {
          String object = key != null ? "a key object" : "key bytes";
          throw new IllegalStateException("A signing key resolver and " + object + " cannot both be specified. Choose either.");
        }
      }
      Key key = this.key;
      if (key == null) {
        byte[] keyBytes = this.keyBytes;
        if (Objects.isEmpty(keyBytes) && signingKeyResolver != null) {
          if (claims != null) {
            key = signingKeyResolver.resolveSigningKey(jwsHeader, claims);
          } else {
            key = signingKeyResolver.resolveSigningKey(jwsHeader, payload);
          }
        }
        if (!Objects.isEmpty(keyBytes)) {
          Assert.isTrue(!algorithm.isRsa(), "Key bytes cannot be specified for RSA signatures.  Please specify a PublicKey or PrivateKey instance.");
          key = new SecretKeySpec(keyBytes, algorithm.getJcaName());
        }
      }
      Assert.notNull(key, "A signing key must be specified if the specified JWT is digitally signed.");
      String jwtWithoutSignature = base64UrlEncodedHeader + SEPARATOR_CHAR + base64UrlEncodedPayload;
      JwtSignatureValidator validator;
      try {
        validator = createSignatureValidator(algorithm, key);
      } catch (IllegalArgumentException e) {
        String algName = algorithm.getValue();
        String msg = "The parsed JWT indicates it was signed with the " + algName + " signature " + "algorithm, but the specified signing key of type " + key.getClass().getName() + " may not be used to validate " + algName + " signatures.  Because the specified " + "signing key reflects a specific and expected algorithm, and the JWT does not reflect " + "this algorithm, it is likely that the JWT was not expected and therefore should not be " + "trusted.  Another possibility is that the parser was configured with the incorrect " + "signing key, but this cannot be assumed for security reasons.";
        throw new UnsupportedJwtException(msg, e);
      }
      if (!validator.isValid(jwtWithoutSignature, base64UrlEncodedDigest)) {
        String msg = "JWT signature does not match locally computed signature. JWT validity cannot be " + "asserted and should not be trusted.";
        throw new SignatureException(msg);
      }
    }
    if (claims != null) {
      Date now = null;
      SimpleDateFormat sdf;
      Date exp = claims.getExpiration();
      if (exp != null) {
        now = new Date();
        if (now.equals(exp) || now.after(exp)) {
          sdf = new SimpleDateFormat(ISO_8601_FORMAT);
          String expVal = sdf.format(exp);
          String nowVal = sdf.format(now);
          String msg = "JWT expired at " + expVal + ". Current time: " + nowVal;
          throw new ExpiredJwtException(header, claims, msg);
        }
      }
      Date nbf = claims.getNotBefore();
      if (nbf != null) {
        if (now == null) {
          now = new Date();
        }
        if (now.before(nbf)) {
          sdf = new SimpleDateFormat(ISO_8601_FORMAT);
          String nbfVal = sdf.format(nbf);
          String nowVal = sdf.format(now);
          String msg = "JWT must not be accepted before " + nbfVal + ". Current time: " + nowVal;
          throw new PrematureJwtException(header, claims, msg);
        }
      }
      validateExpectedClaims(header, claims);
    }
    Object body = claims != null ? claims : payload;
    if (base64UrlEncodedDigest != null) {
      return new DefaultJws<Object>((JwsHeader) header, body, base64UrlEncodedDigest);
    } else {
      return new DefaultJwt<Object>(header, body);
    }
  }

  protected JwtSignatureValidator createSignatureValidator(SignatureAlgorithm alg, Key key) {
    return new DefaultJwtSignatureValidator(alg, key);
  }

  @Override public <T extends java.lang.Object> T parse(String compact, JwtHandler<T> handler) throws ExpiredJwtException, MalformedJwtException, SignatureException {
    Assert.notNull(handler, "JwtHandler argument cannot be null.");
    Assert.hasText(compact, "JWT String argument cannot be null or empty.");
    Jwt jwt = parse(compact);
    if (jwt instanceof Jws) {
      Jws jws = (Jws) jwt;
      Object body = jws.getBody();
      if (body instanceof Claims) {
        return handler.onClaimsJws((Jws<Claims>) jws);
      } else {
        return handler.onPlaintextJws((Jws<String>) jws);
      }
    } else {
      Object body = jwt.getBody();
      if (body instanceof Claims) {
        return handler.onClaimsJwt((Jwt<Header, Claims>) jwt);
      } else {
        return handler.onPlaintextJwt((Jwt<Header, String>) jwt);
      }
    }
  }

  @Override public Jwt<Header, String> parsePlaintextJwt(String plaintextJwt) {
    return parse(plaintextJwt, new JwtHandlerAdapter<Jwt<Header, String>>() {
      @Override public Jwt<Header, String> onPlaintextJwt(Jwt<Header, String> jwt) {
        return jwt;
      }
    });
  }

  @Override public Jwt<Header, Claims> parseClaimsJwt(String claimsJwt) {
    try {
      return parse(claimsJwt, new JwtHandlerAdapter<Jwt<Header, Claims>>() {
        @Override public Jwt<Header, Claims> onClaimsJwt(Jwt<Header, Claims> jwt) {
          return jwt;
        }
      });
    } catch (IllegalArgumentException iae) {
      throw new UnsupportedJwtException("Signed JWSs are not supported.", iae);
    }
  }

  @Override public Jws<String> parsePlaintextJws(String plaintextJws) {
    try {
      return parse(plaintextJws, new JwtHandlerAdapter<Jws<String>>() {
        @Override public Jws<String> onPlaintextJws(Jws<String> jws) {
          return jws;
        }
      });
    } catch (IllegalArgumentException iae) {
      throw new UnsupportedJwtException("Signed JWSs are not supported.", iae);
    }
  }

  @Override public Jws<Claims> parseClaimsJws(String claimsJws) {
    return parse(claimsJws, new JwtHandlerAdapter<Jws<Claims>>() {
      @Override public Jws<Claims> onClaimsJws(Jws<Claims> jws) {
        return jws;
      }
    });
  }

  @SuppressWarnings(value = { "unchecked" }) protected Map<String, Object> readValue(String val) {
    try {
      return objectMapper.readValue(val, Map.class);
    } catch (IOException e) {
      throw new MalformedJwtException("Unable to read JSON value: " + val, e);
    }
  }

  Claims expectedClaims = new DefaultClaims();

  @Override public JwtParser requireIssuedAt(Date issuedAt) {
    expectedClaims.setIssuedAt(issuedAt);
    return this;
  }

  @Override public JwtParser requireIssuer(String issuer) {
    expectedClaims.setIssuer(issuer);
    return this;
  }

  @Override public JwtParser requireAudience(String audience) {
    expectedClaims.setAudience(audience);
    return this;
  }

  @Override public JwtParser requireSubject(String subject) {
    expectedClaims.setSubject(subject);
    return this;
  }

  @Override public JwtParser requireId(String id) {
    expectedClaims.setId(id);
    return this;
  }

  @Override public JwtParser requireExpiration(Date expiration) {
    expectedClaims.setExpiration(expiration);
    return this;
  }

  @Override public JwtParser requireNotBefore(Date notBefore) {
    expectedClaims.setNotBefore(notBefore);
    return this;
  }

  @Override public JwtParser require(String claimName, Object value) {
    Assert.hasText(claimName, "claim name cannot be null or empty.");
    Assert.notNull(value, "The value cannot be null for claim name: " + claimName);
    expectedClaims.put(claimName, value);
    return this;
  }

  private void validateExpectedClaims(Header header, Claims claims) {
    for (String expectedClaimName : expectedClaims.keySet()) {
      Object expectedClaimValue = expectedClaims.get(expectedClaimName);
      Object actualClaimValue = claims.get(expectedClaimName);
      if (Claims.ISSUED_AT.equals(expectedClaimName) || Claims.EXPIRATION.equals(expectedClaimName) || Claims.NOT_BEFORE.equals(expectedClaimName)) {
        expectedClaimValue = expectedClaims.get(expectedClaimName, Date.class);
        actualClaimValue = claims.get(expectedClaimName, Date.class);
      } else {
        if (expectedClaimValue instanceof Date && actualClaimValue != null && actualClaimValue instanceof Long) {
          actualClaimValue = new Date((Long) actualClaimValue);
        }
      }
      InvalidClaimException invalidClaimException = null;
      if (actualClaimValue == null) {
        String msg = String.format(ClaimJwtException.MISSING_EXPECTED_CLAIM_MESSAGE_TEMPLATE, expectedClaimName, expectedClaimValue);
        invalidClaimException = new MissingClaimException(header, claims, msg);
      } else {
        if (!expectedClaimValue.equals(actualClaimValue)) {
          String msg = String.format(ClaimJwtException.INCORRECT_EXPECTED_CLAIM_MESSAGE_TEMPLATE, expectedClaimName, expectedClaimValue, actualClaimValue);
          invalidClaimException = new IncorrectClaimException(header, claims, msg);
        }
      }
      if (invalidClaimException != null) {
        invalidClaimException.setClaimName(expectedClaimName);
        invalidClaimException.setClaimValue(expectedClaimValue);
        throw invalidClaimException;
      }
    }
  }
}