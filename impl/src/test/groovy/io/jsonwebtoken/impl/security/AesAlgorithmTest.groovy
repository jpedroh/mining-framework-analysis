package io.jsonwebtoken.impl.security

import io.jsonwebtoken.security.*
import org.junit.Test

import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

import static org.junit.Assert.*

/**
 * @since JJWT_RELEASE_VERSION
 */
class AesAlgorithmTest {

    @Test(expected = IllegalArgumentException)
    void testConstructorWithoutRequiredKeyLength() {
        new TestAesAlgorithm('foo', 'foo', 0)
    }

    @Test
    void testAssertKeyLength() {

        def alg = new TestAesAlgorithm('foo', 'foo', 192)

        def key = TestKeys.A128GCM //weaker than required

        def request = new DefaultCryptoRequest(null, null, new byte[1], key)

        try {
            alg.assertKey(request)
            fail()
        } catch (SecurityException expected) {
        }
    }

    @Test
    void testValidateLengthKeyExceptionPropagated() {

        def alg = new TestAesAlgorithm('foo', 'foo', 192)
        def ex = new java.lang.SecurityException("HSM: not allowed")
        def key = new SecretKeySpec(new byte[1], 'AES') {
            @Override
            byte[] getEncoded() {
                throw ex
            }
        }

        try {
            alg.validateLength(key, 192, true)
            fail()
        } catch (java.lang.SecurityException expected) {
            assertSame ex, expected
        }
    }

    @Test
    void testValidateLengthKeyExceptionNotPropagated() {

        def alg = new TestAesAlgorithm('foo', 'foo', 192)
        def ex = new java.lang.SecurityException("HSM: not allowed")
        def key = new SecretKeySpec(new byte[1], 'AES') {
            @Override
            byte[] getEncoded() {
                throw ex
            }
        }

        //exception thrown, but we don't propagate:
        assertNull alg.validateLength(key, 192, false)
    }

    @Test
    void testAssertBytesWithLengthMismatch() {
        int reqdBitLen = 192
        def alg = new TestAesAlgorithm('foo', 'foo', reqdBitLen)
        byte[] bytes = new byte[(reqdBitLen - 8) / Byte.SIZE]
        try {
            alg.assertBytes(bytes, 'test arrays', reqdBitLen)
            fail()
        } catch (IllegalArgumentException iae) {
            String msg = "The 'foo' algorithm requires test arrays with a length of 192 bits (24 bytes).  " +
                    "The provided key has a length of 184 bits (23 bytes)."
            assertEquals msg, iae.getMessage()
        }
    }

    @Test
    void testGetSecureRandomWhenRequestHasSpecifiedASecureRandom() {

        def alg = new TestAesAlgorithm('foo', 'foo', 128)

        def secureRandom = new SecureRandom()

        def req = new DefaultAeadRequest(null, secureRandom, 'data'.getBytes(), alg.keyBuilder().build(), 'aad'.getBytes())

        def returnedSecureRandom = alg.ensureSecureRandom(req)

        assertSame(secureRandom, returnedSecureRandom)
    }

    static class TestAesAlgorithm extends AesAlgorithm implements AeadAlgorithm {

        TestAesAlgorithm(String name, String transformationString, int requiredKeyLengthInBits) {
            super(name, transformationString, requiredKeyLengthInBits)
        }

        @Override
        AeadResult encrypt(AeadRequest symmetricAeadRequest) {
            return null
        }

        @Override
        Message decrypt(DecryptAeadRequest symmetricAeadDecryptionRequest) {
            return null
        }
    }

}
