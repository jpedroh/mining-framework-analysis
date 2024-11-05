package io.jsonwebtoken;
import java.security.Key;
import java.util.Date;

public interface JwtParser {
  public static final char SEPARATOR_CHAR = '.';

  JwtParser setSigningKey(byte[] key);

  JwtParser setSigningKey(String base64EncodedKeyBytes);

  JwtParser setSigningKey(Key key);

  JwtParser setSigningKeyResolver(SigningKeyResolver signingKeyResolver);

  JwtParser setCompressionCodecResolver(CompressionCodecResolver compressionCodecResolver);

  boolean isSigned(String jwt);

  Jwt parse(String jwt) throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;

  <T extends java.lang.Object> T parse(String jwt, JwtHandler<T> handler) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;

  Jwt<Header, String> parsePlaintextJwt(String plaintextJwt) throws UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;

  Jwt<Header, Claims> parseClaimsJwt(String claimsJwt) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;

  Jws<String> parsePlaintextJws(String plaintextJws) throws UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;

  Jws<Claims> parseClaimsJws(String claimsJws) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;

  JwtParser requireId(String id);

  JwtParser requireSubject(String subject);

  JwtParser requireAudience(String audience);

  JwtParser requireIssuer(String issuer);

  JwtParser requireIssuedAt(Date issuedAt);

  JwtParser requireExpiration(Date expiration);

  JwtParser requireNotBefore(Date notBefore);

  JwtParser require(String claimName, Object value);
}