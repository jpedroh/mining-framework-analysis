package com.lambdaworks.redis;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.lambdaworks.codec.Base16;

/**
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/LettuceStrings.java/left.java
 * Helper for {@link String} checks.
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/LettuceStrings.java/base.java
=======
 * Helper for {@link String} checks.
 * 
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/LettuceStrings.java/right.java
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 */
public class LettuceStrings {

    /**
     * Utility constructor.
     */
    private LettuceStrings() {

    }

    /**
     * Checks if a CharSequence is empty ("") or null.
     * 
     * @param cs the char sequence
     * @return true if empty
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * Checks if a CharSequence is not empty ("") and not null.
     * 
     * @param cs the char sequence
     * @return true if not empty
     * 
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * Convert double to string. If double is infinite, returns positive/negative infinity {@code +inf} and {@code -inf}.
     * 
     * @param n the double.
     * @return string representation of {@code n}
     */
    public static String string(double n) {
        if (Double.isInfinite(n)) {
            return (n > 0) ? "+inf" : "-inf";
        }
        return Double.toString(n);
    }

    /**
     * Create SHA1 digest from Lua script.
     * 
     * @param script the script
     * @return the Base16 encoded SHA1 value
     */
    public static String digest(byte[] script) {
        return digest(ByteBuffer.wrap(script));
    }

    /**
     * Create SHA1 digest from Lua script.
     * 
     * @param script the script
     * @return the Base16 encoded SHA1 value
     */
    public static String digest(ByteBuffer script) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(script);
            return new String(Base16.encode(md.digest(), false));
        } catch (NoSuchAlgorithmException e) {
            throw new RedisException("JVM does not support SHA1");
        }
    }
}
