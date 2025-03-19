package io.jsonwebtoken.impl;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.lang.Strings;
import java.util.Map;

@SuppressWarnings(value = { "unchecked" }) public class DefaultHeader<T extends Header<T>> extends JwtMap implements Header<T> {
  public DefaultHeader() {
    super();
  }

  public DefaultHeader(Map<String, Object> map) {
    super(map);
  }

  @Override public String getType() {
    return getString(TYPE);
  }

  @Override public T setType(String typ) {
    setValue(TYPE, typ);
    return (T) this;
  }

  @Override public String getContentType() {
    return getString(CONTENT_TYPE);
  }

  @Override public T setContentType(String cty) {
    setValue(CONTENT_TYPE, cty);
    return (T) this;
  }

  @SuppressWarnings(value = { "deprecation" }) @Override public String getCompressionAlgorithm() {
    String 
<<<<<<< /usr/src/app/output/jwtk/jjwt/cb5734d8a64949a05604ebf1c2a7065f055bacb9/src/main/java/io/jsonwebtoken/impl/DefaultHeader.java/left.java
    s = getString(COMPRESSION_ALGORITHM)
=======
    alg = getString(COMPRESSION_ALGORITHM)
>>>>>>> /usr/src/app/output/jwtk/jjwt/cb5734d8a64949a05604ebf1c2a7065f055bacb9/src/main/java/io/jsonwebtoken/impl/DefaultHeader.java/right.java
    ;
    if (!Strings.hasText(
<<<<<<< /usr/src/app/output/jwtk/jjwt/cb5734d8a64949a05604ebf1c2a7065f055bacb9/src/main/java/io/jsonwebtoken/impl/DefaultHeader.java/left.java
    s
=======
    alg
>>>>>>> /usr/src/app/output/jwtk/jjwt/cb5734d8a64949a05604ebf1c2a7065f055bacb9/src/main/java/io/jsonwebtoken/impl/DefaultHeader.java/right.java
    )) {

<<<<<<< /usr/src/app/output/jwtk/jjwt/cb5734d8a64949a05604ebf1c2a7065f055bacb9/src/main/java/io/jsonwebtoken/impl/DefaultHeader.java/left.java
      s
=======
      alg
>>>>>>> /usr/src/app/output/jwtk/jjwt/cb5734d8a64949a05604ebf1c2a7065f055bacb9/src/main/java/io/jsonwebtoken/impl/DefaultHeader.java/right.java
       = getString(DEPRECATED_COMPRESSION_ALGORITHM);
    }
    return 
<<<<<<< /usr/src/app/output/jwtk/jjwt/cb5734d8a64949a05604ebf1c2a7065f055bacb9/src/main/java/io/jsonwebtoken/impl/DefaultHeader.java/left.java
    s
=======
    alg
>>>>>>> /usr/src/app/output/jwtk/jjwt/cb5734d8a64949a05604ebf1c2a7065f055bacb9/src/main/java/io/jsonwebtoken/impl/DefaultHeader.java/right.java
    ;
  }

  @Override public T setCompressionAlgorithm(String compressionAlgorithm) {
    setValue(COMPRESSION_ALGORITHM, compressionAlgorithm);
    return (T) this;
  }
}