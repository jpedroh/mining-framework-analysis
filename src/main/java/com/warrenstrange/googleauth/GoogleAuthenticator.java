package com.warrenstrange.googleauth;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class implements the functionality described in RFC 6238 (TOTP: Time
 * based one-time password algorithm) and has been tested again Google's
 * implementation of such algorithm in its Google Authenticator application.
 * <p/>
 * This class lets users create a new 16-bit base32-encoded secret key with
 * the validation code calculated at time=0 (the UNIX epoch) and the URL of a
 * Google-provided QR barcode to let an user load the generated information into
 * Google Authenticator.
 * <p/>
 * This class doesn't store in any way either the generated keys nor the keys
 * passed during the authorization process.
 * <p/>
 * Java Server side class for Google Authenticator's TOTP generator was inspired
 * by an author's blog post.
 *
 * @author Enrico M. Crisostomo
 * @author Warren Strange
 * @version 1.0
 * @see <a href="http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html" />
 * @see <a href="http://code.google.com/p/google-authenticator" />
 * @see <a href="http://tools.ietf.org/id/draft-mraihi-totp-timebased-06.txt" />
 * @since 1.0
 */
public final class GoogleAuthenticator implements IGoogleAuthenticator {
  /**
     * The logger for this class.
     */
  private static final Logger LOGGER = Logger.getLogger(GoogleAuthenticator.class.getName());

  /**
     * The number of bits of a secret key in binary form. Since the Base32
     * encoding with 8 bit characters introduces an 160% overhead, we just need
     * 80 bits (10 bytes) to generate a 16 bytes Base32-encoded secret key.
     */
  private static final int SECRET_BITS = 80;

  /**
     * Number of scratch codes to generate during the key generation.
     * We are using Google's default of providing 5 scratch codes.
     */
  private static final int SCRATCH_CODES = 5;

  /**
     * Number of digits of a scratch code represented as a decimal integer.
     */
  private static final int SCRATCH_CODE_LENGTH = 8;

  /**
     * Modulus used to truncate the scratch code.
     */
  public static final int SCRATCH_CODE_MODULUS = (int) Math.pow(10, SCRATCH_CODE_LENGTH);

  /**
     * Magic number representing an invalid scratch code.
     */
  private static final int SCRATCH_CODE_INVALID = -1;

  /**
     * Length in bytes of each scratch code. We're using Google's default of
     * using 4 bytes per scratch code.
     */
  private static final int BYTES_PER_SCRATCH_CODE = 4;

  /**
     * The SecureRandom algorithm to use.
     *
     * @see java.security.SecureRandom#getInstance(String)
     */
  @SuppressWarnings(value = { "SpellCheckingInspection" }) private static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

  /**
     * Sun random number algorithm provider name.
     */
  private static final String RANDOM_NUMBER_ALGORITHM_PROVIDER = "SUN";

  /**
     * Cryptographic hash function used to calculate the HMAC (Hash-based
     * Message Authentication Code). This implementation uses the SHA1 hash
     * function.
     */
  private static final String HMAC_HASH_FUNCTION = "HmacSHA1";

  /**
     * Minimum secret key length (inclusive).
     */
  private final GoogleAuthenticatorConfig 
<<<<<<< /usr/src/app/output/wstrange/googleauth/bb6e4a0c6f4c595a93bcd3b06c05d0c55df8e365/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java/left.java
  SECRET_KEY_LENGTH_MIN = 6
=======
  config
>>>>>>> /usr/src/app/output/wstrange/googleauth/bb6e4a0c6f4c595a93bcd3b06c05d0c55df8e365/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java/right.java
  ;

  /**
     * Maximum secret key length (inclusive).
     */
  private static final int SECRET_KEY_LENGTH_MAX = 9;

  /**
     * The secret key length used by the current instance.
     */
  private final int secretKeyModule;

  /**
     * The internal SecureRandom instance used by this class. Since as of Java 7
     * Random instances are required to be thread-safe, no synchronisation is
     * required in the methods of this class using this instance.  Thread-safety
     * of this class was a de-facto standard in previous versions of Java so
     * that it is expected to work correctly in previous versions of the Java
     * platform as well.
     */
  private ReseedingSecureRandom secureRandom = new ReseedingSecureRandom(RANDOM_NUMBER_ALGORITHM, RANDOM_NUMBER_ALGORITHM_PROVIDER);

  /**
     * Default constructor.
     */
  public GoogleAuthenticator(GoogleAuthenticatorConfig config) {
    this(SECRET_KEY_LENGTH_MIN);
    checkNotNull(config, "Configuration cannot be null.");
    this.config = config;
  }

  public GoogleAuthenticator(int secretKeyLength) {
    config = new GoogleAuthenticatorConfig();
    if (secretKeyLength < SECRET_KEY_LENGTH_MIN || secretKeyLength > SECRET_KEY_LENGTH_MAX) {
      throw new IllegalArgumentException(String.format("Length must be in the [%d, %d] range.", SECRET_KEY_LENGTH_MIN, SECRET_KEY_LENGTH_MAX));
    }
    this.secretKeyModule = (int) Math.pow(10, secretKeyLength);
  }

  /**
     * Calculates the verification code of the provided key at the specified
     * instant of time using the algorithm specified in RFC 6238.
     *
     * @param key the secret key in binary format.
     * @param tm  the instant of time.
     * @return the validation code for the provided key at the specified instant
     * of time.
     */
  private int calculateCode(byte[] key, long tm) {
    byte[] data = new byte[8];
    long value = tm;
    for (int i = 8; i-- > 0; value >>>= 8) {
      data[i] = (byte) value;
    }
    SecretKeySpec signKey = new SecretKeySpec(key, HMAC_HASH_FUNCTION);
    try {
      Mac mac = Mac.getInstance(HMAC_HASH_FUNCTION);
      mac.init(signKey);
      byte[] hash = mac.doFinal(data);
      int offset = hash[hash.length - 1] & 0xF;
      long truncatedHash = 0;
      for (int i = 0; i < 4; ++i) {
        truncatedHash <<= 8;
        truncatedHash |= (hash[offset + i] & 0xFF);
      }
      truncatedHash &= 0x7FFFFFFF;
      truncatedHash %= 
<<<<<<< /usr/src/app/output/wstrange/googleauth/bb6e4a0c6f4c595a93bcd3b06c05d0c55df8e365/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java/left.java
      this.secretKeyModule
=======
      config.getKeyModulus()
>>>>>>> /usr/src/app/output/wstrange/googleauth/bb6e4a0c6f4c595a93bcd3b06c05d0c55df8e365/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java/right.java
      ;
      return (int) truncatedHash;
    } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
      LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
      throw new GoogleAuthenticatorException("The operation cannot be " + "performed now.");
    }
  }

  /**
     * Get the current TOTP password of the specified key.
     *
     * @param secret The shared secret.
     * @return the current TOTP password of the specified key.
     */
  public int getCurrentCode(String secret) {
    byte[] decodedKey = decodeSecretKey(secret);
    final long timeWindow = new Date().getTime() / KEY_VALIDATION_INTERVAL_MS;
    return calculateCode(decodedKey, timeWindow);
  }

  /**
     * This method implements the algorithm specified in RFC 6238 to check if a
     * validation code is valid in a given instant of time for the given secret
     * key.
     *
     * @param secret the Base32 encoded secret key.
     * @param code   the code to validate.
     * @param tm     the instant of time to use during the validation process.
     * @param window the window size to use during the validation process.
     * @return <code>true</code> if the validation code is valid,
     * <code>false</code> otherwise.
     */
  private boolean checkCode(String secret, long code, long timestamp, int window) {
    byte[] decodedKey = 
<<<<<<< /usr/src/app/output/wstrange/googleauth/bb6e4a0c6f4c595a93bcd3b06c05d0c55df8e365/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java/left.java
    decodeSecretKey(secret)
=======
>>>>>>> Unknown file: This is a bug in JDime.
    ;
    switch (config.getKeyRepresentation()) {
      case BASE32:
      Base32 codec32 = new Base32();
      decodedKey = codec32.decode(secret);
      break;
      case BASE64:
      Base64 codec64 = new Base64();
      decodedKey = codec64.decode(secret);
      break;
      default:
      throw new IllegalArgumentException("Unknown key representation type.");
    }
    final long timeWindow = timestamp / this.config.getTimeStepSizeInMillis();
    for (int i = -((window - 1) / 2); i <= window / 2; ++i) {
      long hash = calculateCode(decodedKey, timeWindow + i);
      if (hash == code) {
        return true;
      }
    }
    return false;
  }

  @Override public GoogleAuthenticatorKey createCredentials() {
    byte[] buffer = new byte[SECRET_BITS / 8 + SCRATCH_CODES * BYTES_PER_SCRATCH_CODE];
    secureRandom.nextBytes(buffer);
    byte[] secretKey = Arrays.copyOf(buffer, SECRET_BITS / 8);
    String generatedKey = encodeSecretKey(secretKey);
    int validationCode = calculateValidationCode(secretKey);
    List<Integer> scratchCodes = calculateScratchCodes(buffer);
    return new GoogleAuthenticatorKey(generatedKey, validationCode, scratchCodes);
  }

  @Override public GoogleAuthenticatorKey createCredentials(String userName) {
    checkNotNull(userName, "User name cannot be null.");
    GoogleAuthenticatorKey key = createCredentials();
    ICredentialRepository repository = getValidCredentialRepository();
    repository.saveUserCredentials(userName, key.getKey(), key.getVerificationCode(), key.getScratchCodes());
    return key;
  }

  private List<Integer> calculateScratchCodes(byte[] buffer) {
    List<Integer> scratchCodes = new ArrayList<>();
    while (scratchCodes.size() < SCRATCH_CODES) {
      byte[] scratchCodeBuffer = Arrays.copyOfRange(buffer, SECRET_BITS / 8 + BYTES_PER_SCRATCH_CODE * scratchCodes.size(), SECRET_BITS / 8 + BYTES_PER_SCRATCH_CODE * scratchCodes.size() + BYTES_PER_SCRATCH_CODE);
      int scratchCode = calculateScratchCode(scratchCodeBuffer);
      if (scratchCode != SCRATCH_CODE_INVALID) {
        scratchCodes.add(scratchCode);
      } else {
        scratchCodes.add(generateScratchCode());
      }
    }
    return scratchCodes;
  }

  /**
     * This method calculates a scratch code from a random byte buffer of
     * suitable size <code>#BYTES_PER_SCRATCH_CODE</code>.
     *
     * @param scratchCodeBuffer a random byte buffer whose minimum size is
     *                          <code>#BYTES_PER_SCRATCH_CODE</code>.
     * @return the scratch code.
     */
  private int calculateScratchCode(byte[] scratchCodeBuffer) {
    checkArgument(scratchCodeBuffer.length >= BYTES_PER_SCRATCH_CODE, "The provided random byte buffer is too small:", scratchCodeBuffer.length);
    int scratchCode = 0;
    for (int i = 0; i < BYTES_PER_SCRATCH_CODE; ++i) {
      scratchCode <<= 8;
      scratchCode += scratchCodeBuffer[i];
    }
    scratchCode = (scratchCode & 0x7FFFFFFF) % SCRATCH_CODE_MODULUS;
    if (validateScratchCode(scratchCode)) {
      return scratchCode;
    } else {
      return SCRATCH_CODE_INVALID;
    }
  }

  boolean validateScratchCode(int scratchCode) {
    return (scratchCode >= SCRATCH_CODE_MODULUS / 10);
  }

  /**
     * This method creates a new random byte buffer from which a new scratch
     * code is generated. This function is invoked if a scratch code generated
     * from the main buffer is invalid because it does not satisfy the scratch
     * code restrictions.
     *
     * @return A valid scratch code.
     */
  private int generateScratchCode() {
    while (true) {
      byte[] scratchCodeBuffer = new byte[BYTES_PER_SCRATCH_CODE];
      secureRandom.nextBytes(scratchCodeBuffer);
      int scratchCode = calculateScratchCode(scratchCodeBuffer);
      if (scratchCode != SCRATCH_CODE_INVALID) {
        return scratchCode;
      }
    }
  }

  /**
     * This method calculates the validation code at time 0.
     *
     * @param secretKey The secret key to use.
     * @return the validation code at time 0.
     */
  private int calculateValidationCode(byte[] secretKey) {
    return calculateCode(secretKey, 0);
  }

  /**
     * This method calculates the secret key given a random byte buffer.
     *
     * @param secretKey a random byte buffer.
     * @return the secret key.
     */
  private String encodeSecretKey(byte[] secretKey) {
    Base32 codec = new Base32();
    byte[] encodedKey = codec.encode(secretKey);
    return new String(encodedKey);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * This method calculates the secret key given a random byte buffer.
     *
     * @param secretKey a random byte buffer.
     * @return the secret key.
     */
  private String calculateSecretKey(byte[] secretKey) {
    byte[] encodedKey;
    switch (config.getKeyRepresentation()) {
      case BASE32:
      Base32 codec = new Base32();
      encodedKey = codec.encode(secretKey);
      break;
      case BASE64:
      Base64 codec64 = new Base64();
      encodedKey = codec64.encode(secretKey);
      break;
      default:
      throw new IllegalArgumentException("Unknown key representation type.");
    }
    return new String(encodedKey);
  }
>>>>>>> /usr/src/app/output/wstrange/googleauth/bb6e4a0c6f4c595a93bcd3b06c05d0c55df8e365/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java/right.java


  /**
     * This method decodes the shared secret from its Base32 representation.
     *
     * @param secret The Base32-encoded shared secret.
     * @return the secret key.
     */
  private byte[] decodeSecretKey(String secret) {
    Base32 codec = new Base32();
    byte[] decodedKey = codec.decode(secret);
    return decodedKey;
  }

  @Override public boolean authorizeUser(String userName, int verificationCode) throws GoogleAuthenticatorException {
    ICredentialRepository repository = getValidCredentialRepository();
    return authorize(repository.getSecretKey(userName), verificationCode);
  }

  @Override public boolean authorize(String secret, int verificationCode) throws GoogleAuthenticatorException {
    checkNotNull(secret, "Secret cannot be null.");
    if (verificationCode <= 0 || verificationCode >= 
<<<<<<< /usr/src/app/output/wstrange/googleauth/bb6e4a0c6f4c595a93bcd3b06c05d0c55df8e365/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java/left.java
    this.secretKeyModule
=======
    this.config.getKeyModulus()
>>>>>>> /usr/src/app/output/wstrange/googleauth/bb6e4a0c6f4c595a93bcd3b06c05d0c55df8e365/src/main/java/com/warrenstrange/googleauth/GoogleAuthenticator.java/right.java
    ) {
      return false;
    }
    return checkCode(secret, verificationCode, new Date().getTime(), this.config.getWindowSize());
  }

  /**
     * This method loads the first available and valid ICredentialRepository
     * registered using the Java service loader API.
     *
     * @return the first registered ICredentialRepository.
     * @throws java.lang.UnsupportedOperationException if no valid service is
     *                                                 found.
     */
  private ICredentialRepository getValidCredentialRepository() {
    ICredentialRepository repository = getCredentialRepository();
    if (repository == null) {
      throw new UnsupportedOperationException(String.format("An instance of the %s service must be " + "configured in order to use this feature.", ICredentialRepository.class.getName()));
    }
    return repository;
  }

  /**
     * This method loads the first available ICredentialRepository
     * registered using the Java service loader API.
     *
     * @return the first registered ICredentialRepository or <code>null</code>
     * if none is found.
     */
  private ICredentialRepository getCredentialRepository() {
    ServiceLoader<ICredentialRepository> loader = ServiceLoader.load(ICredentialRepository.class);
    for (ICredentialRepository repository : loader) {
      return repository;
    }
    return null;
  }
}