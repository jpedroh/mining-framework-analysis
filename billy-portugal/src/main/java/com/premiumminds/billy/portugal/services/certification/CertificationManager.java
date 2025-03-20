package com.premiumminds.billy.portugal.services.certification;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertificationManager {
  private static final Logger log = LoggerFactory.getLogger(CertificationManager.class);

  private static final int EXPECTED_HASH_LENGTH = 172;

  private PrivateKey privateKey;

  private PublicKey publicKey;

  private Signature signature;

  private boolean autoVerifyHash;

  public CertificationManager() {
    this.privateKey = null;
    this.publicKey = null;
    this.autoVerifyHash = false;
    try {
      this.signature = Signature.getInstance("SHA1withRSA");
    } catch (NoSuchAlgorithmException e) {
      CertificationManager.log.error(e.getMessage(), e);
    }
  }

  public void setAutoVerifyHash(boolean verify) {
    this.autoVerifyHash = verify;
  }

  public String getHashBase64(String source) throws InvalidHashException, InvalidKeyException {
    String hashBase64 = Base64.encodeBase64String(this.getHashBinary(source));
    if (this.autoVerifyHash) {
      if ((!this.verifyHashBase64(source, hashBase64)) || (hashBase64.length() != CertificationManager.EXPECTED_HASH_LENGTH)) {
        throw new InvalidHashException();
      }
    }
    return hashBase64;
  }

  public byte[] getHashBinary(String source) throws InvalidHashException, InvalidKeyException {
    byte[] hash;
    try {
      this.signature.initSign(this.privateKey);
      this.signature.update(source.getBytes());
      hash = this.signature.sign();
      if (this.autoVerifyHash) {
        if (!this.verifyHashBinary(source, hash)) {
          throw new InvalidHashException();
        }
      }
    } catch (SignatureException e) {
      throw new InvalidHashException("Signature exception - should not happen");
    }
    return hash;
  }

  public boolean verifyHashBase64(String source, String hashBase64) throws InvalidKeyException {
    return (this.verifyHashBinary(source, Base64.decodeBase64(hashBase64)) && (hashBase64.length() == CertificationManager.EXPECTED_HASH_LENGTH));
  }

  public boolean verifyHashBinary(String source, byte[] hash) throws InvalidKeyException {
    try {
      this.signature.initVerify(this.publicKey);
      this.signature.update(source.getBytes());
      return this.signature.verify(hash);
    } catch (SignatureException e) {
      CertificationManager.log.error(e.getMessage(), e);
    }
    return false;
  }

  public void setPrivateKey(PrivateKey key) throws InvalidKeySpecException {
    this.privateKey = key;
  }

  public void setPublicKey(PublicKey key) throws InvalidKeySpecException {
    this.publicKey = key;
  }
}