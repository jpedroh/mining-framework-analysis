package com.premiumminds.billy.spain.util;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates {@link PrivateKey} and {@link PublicKey}.
 */
public class KeyGenerator {
  private static final Logger log = LoggerFactory.getLogger(KeyGenerator.class);

  private String privateKeyPath;

  /**
     * Generates the {@link PrivateKey} and {@link PublicKey} based on the
     * {@link PrivateKey} location.
     *
     * @param privateKeyPath
     */
  public KeyGenerator(String privateKeyPath) {
    if (Security.getProvider("BC") == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
    this.privateKeyPath = privateKeyPath;
  }

  private String getKeyFromFile() {
    InputStream inputStream = null;
    String key = "";
    try {
      inputStream = this.getClass().getResourceAsStream(this.privateKeyPath);
      key = IOUtils.toString(inputStream);
    } catch (IOException e) {
      KeyGenerator.log.error(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
    return key;
  }

  private KeyPair getKeyPair() {
    PEMReader pemReader = new PEMReader(new StringReader(this.getKeyFromFile()));
    KeyPair pair = null;
    try {
      pair = (KeyPair) pemReader.readObject();
    } catch (IOException e) {
      KeyGenerator.log.error(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(pemReader);
    }
    return pair;
  }

  /**
     * @return {@link PrivateKey}
     */
  public PrivateKey getPrivateKey() {
    return this.getKeyPair().getPrivate();
  }

  /**
     * @return {@link PublicKey}
     */
  public PublicKey getPublicKey() {
    return this.getKeyPair().getPublic();
  }
}