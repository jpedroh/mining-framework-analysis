/*
 * Copyright (C) 2021 jsonwebtoken.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jsonwebtoken.security;

import io.jsonwebtoken.JweHeader;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Classes;

import javax.crypto.SecretKey;
import java.util.Collection;

/**
 * Constant definitions and utility methods for all
 * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4">JWA (RFC 7518) Key Management Algorithms</a>.
 *
 * @see #values()
 * @see #findById(String)
 * @see #forId(String)
 * @since JJWT_RELEASE_VERSION
 */
@SuppressWarnings("rawtypes")
public final class KeyAlgorithms {

    //prevent instantiation
    private KeyAlgorithms() {
    }

    private static final String BRIDGE_CLASSNAME = "io.jsonwebtoken.impl.security.KeyAlgorithmsBridge";
    private static final Class<?> BRIDGE_CLASS = Classes.forName(BRIDGE_CLASSNAME);
    private static final Class<?>[] ID_ARG_TYPES = new Class[]{String.class};
    //private static final Class<?>[] ESTIMATE_ITERATIONS_ARG_TYPES = new Class[]{KeyAlgorithm.class, long.class};

    /**
     * Returns all JWA-standard Key Management algorithms as an unmodifiable collection.
     *
     * @return all JWA-standard Key Management algorithms as an unmodifiable collection.
     */
    public static Collection<KeyAlgorithm<?, ?>> values() {
        return Classes.invokeStatic(BRIDGE_CLASS, "values", null, (Object[]) null);
    }

    /**
     * Returns the JWE Key Management Algorithm with the specified
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.1">{@code alg} key algorithm identifier</a> or
     * {@code null} if an algorithm for the specified {@code id} cannot be found.  If a JWA-standard
     * instance must be resolved, consider using the {@link #forId(String)} method instead.
     *
     * @param id a JWA standard {@code alg} key algorithm identifier
     * @return the associated KeyAlgorithm instance or {@code null} otherwise.
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.1">RFC 7518, Section 4.1</a>
     * @see #forId(String)
     */
    public static KeyAlgorithm<?, ?> findById(String id) {
        Assert.hasText(id, "id cannot be null or empty.");
        return Classes.invokeStatic(BRIDGE_CLASS, "findById", ID_ARG_TYPES, id);
    }

    /**
     * Returns the JWE Key Management Algorithm with the specified
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.1">{@code alg} key algorithm identifier</a> or
     * throws an {@link IllegalArgumentException} if there is no JWE-standard algorithm for the specified
     * {@code id}.  If a JWE-standard instance result is not mandatory, consider using the {@link #findById(String)}
     * method instead.
     *
     * @param id a JWA standard {@code alg} key algorithm identifier
     * @return the associated {@code KeyAlgorithm} instance.
     * @throws IllegalArgumentException if there is no JWA-standard algorithm for the specified identifier.
     * @see #findById(String)
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.1">RFC 7518, Section 4.1</a>
     */
    public static KeyAlgorithm<?, ?> forId(String id) {
        return forId0(id);
    }

    // do not change this visibility.  Raw type method signature not be publicly exposed
    private static <T> T forId0(String id) {
        Assert.hasText(id, "id cannot be null or empty.");
        return Classes.invokeStatic(BRIDGE_CLASS, "forId", ID_ARG_TYPES, id);
    }

    /**
     * Key algorithm reflecting direct use of a shared symmetric key as the JWE AEAD encryption key, as defined
     * by <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.5">RFC 7518 (JWA), Section 4.5</a>.  This
     * algorithm does not produce encrypted key ciphertext.
     */
    public static final KeyAlgorithm<SecretKey, SecretKey> DIRECT = forId0("dir");

    /**
     * AES Key Wrap algorithm with default initial value using a 128-bit key, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.4">RFC 7518 (JWA), Section 4.4</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with a 128-bit shared symmetric key using the
     *     AES Key Wrap algorithm, producing encrypted key ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Decrypts the encrypted key ciphertext with the 128-bit shared symmetric key,
     *     using the AES Key Unwrap algorithm, producing the decryption key plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final SecretKeyAlgorithm A128KW = forId0("A128KW");

    /**
     * AES Key Wrap algorithm with default initial value using a 192-bit key, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.4">RFC 7518 (JWA), Section 4.4</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with a 192-bit shared symmetric key using the
     *     AES Key Wrap algorithm, producing encrypted key ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Decrypts the encrypted key ciphertext with the 192-bit shared symmetric key,
     *     using the AES Key Unwrap algorithm, producing the decryption key plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final SecretKeyAlgorithm A192KW = forId0("A192KW");

    /**
     * AES Key Wrap algorithm with default initial value using a 256-bit key, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.4">RFC 7518 (JWA), Section 4.4</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with a 256-bit shared symmetric key using the
     *     AES Key Wrap algorithm, producing encrypted key ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Decrypts the encrypted key ciphertext with the 256-bit shared symmetric key,
     *     using the AES Key Unwrap algorithm, producing the decryption key plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final SecretKeyAlgorithm A256KW = forId0("A256KW");

    /**
     * Key wrap algorithm with AES GCM using a 128-bit key, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7">RFC 7518 (JWA), Section 4.7</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Generates a new secure-random 96-bit Initialization Vector to use during key wrap/encryption.</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with a 128-bit shared symmetric key using the
     *     AES GCM Key Wrap algorithm with the generated Initialization Vector, producing encrypted key ciphertext
     *     and GCM authentication tag.</li>
     *     <li>Sets the generated initialization vector as the required
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.1">&quot;iv&quot;
     *     (Initialization Vector) Header Parameter</a></li>
     *     <li>Sets the resulting GCM authentication tag as the required
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.2">&quot;tag&quot;
     *     (Authentication Tag) Header Parameter</a></li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Obtains the required initialization vector from the
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.1">&quot;iv&quot;
     *     (Initialization Vector) Header Parameter</a></li>
     *     <li>Obtains the required GCM authentication tag from the
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.2">&quot;tag&quot;
     *     (Authentication Tag) Header Parameter</a></li>
     *     <li>Decrypts the encrypted key ciphertext with the 128-bit shared symmetric key, the initialization vector
     *     and GCM authentication tag using the AES GCM Key Unwrap algorithm, producing the decryption key
     *     plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final SecretKeyAlgorithm A128GCMKW = forId0("A128GCMKW");

    /**
     * Key wrap algorithm with AES GCM using a 192-bit key, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7">RFC 7518 (JWA), Section 4.7</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Generates a new secure-random 96-bit Initialization Vector to use during key wrap/encryption.</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with a 192-bit shared symmetric key using the
     *     AES GCM Key Wrap algorithm with the generated Initialization Vector, producing encrypted key ciphertext
     *     and GCM authentication tag.</li>
     *     <li>Sets the generated initialization vector as the required
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.1">&quot;iv&quot;
     *     (Initialization Vector) Header Parameter</a></li>
     *     <li>Sets the resulting GCM authentication tag as the required
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.2">&quot;tag&quot;
     *     (Authentication Tag) Header Parameter</a></li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Obtains the required initialization vector from the
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.1">&quot;iv&quot;
     *     (Initialization Vector) Header Parameter</a></li>
     *     <li>Obtains the required GCM authentication tag from the
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.2">&quot;tag&quot;
     *     (Authentication Tag) Header Parameter</a></li>
     *     <li>Decrypts the encrypted key ciphertext with the 192-bit shared symmetric key, the initialization vector
     *     and GCM authentication tag using the AES GCM Key Unwrap algorithm, producing the decryption key \
     *     plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final SecretKeyAlgorithm A192GCMKW = forId0("A192GCMKW");

    /**
     * Key wrap algorithm with AES GCM using a 256-bit key, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7">RFC 7518 (JWA), Section 4.7</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Generates a new secure-random 96-bit Initialization Vector to use during key wrap/encryption.</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with a 256-bit shared symmetric key using the
     *     AES GCM Key Wrap algorithm with the generated Initialization Vector, producing encrypted key ciphertext
     *     and GCM authentication tag.</li>
     *     <li>Sets the generated initialization vector as the required
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.1">&quot;iv&quot;
     *     (Initialization Vector) Header Parameter</a></li>
     *     <li>Sets the resulting GCM authentication tag as the required
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.2">&quot;tag&quot;
     *     (Authentication Tag) Header Parameter</a></li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Obtains the required initialization vector from the
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.1">&quot;iv&quot;
     *     (Initialization Vector) Header Parameter</a></li>
     *     <li>Obtains the required GCM authentication tag from the
     *     <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.7.1.2">&quot;tag&quot;
     *     (Authentication Tag) Header Parameter</a></li>
     *     <li>Decrypts the encrypted key ciphertext with the 256-bit shared symmetric key, the initialization vector
     *     and GCM authentication tag using the AES GCM Key Unwrap algorithm, producing the decryption key \
     *     plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final SecretKeyAlgorithm A256GCMKW = forId0("A256GCMKW");
    public static final KeyAlgorithm<PasswordKey, PasswordKey> PBES2_HS256_A128KW = forId0("PBES2-HS256+A128KW");
    public static final KeyAlgorithm<PasswordKey, PasswordKey> PBES2_HS384_A192KW = forId0("PBES2-HS384+A192KW");
    public static final KeyAlgorithm<PasswordKey, PasswordKey> PBES2_HS512_A256KW = forId0("PBES2-HS512+A256KW");

    /**
     * Key Encryption with {@code RSAES-PKCS1-v1_5}, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.2">RFC 7518 (JWA), Section 4.2</a>.
     * This algorithm requires a 2048-bit key.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with the RSA key wrap algorithm, using the JWE
     *     recipient's RSA Public Key, producing encrypted key ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Decrypts the encrypted key ciphertext with the RSA key unwrap algorithm, using the JWE recipient's
     *     RSA Private Key, producing the decryption key plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final RsaKeyAlgorithm RSA1_5 = forId0("RSA1_5");

    /**
     * Key Encryption with {@code RSAES OAEP using default parameters}, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.3">RFC 7518 (JWA), Section 4.3</a>.
     * This algorithm requires a 2048-bit key.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with the RSA OAEP with SHA-1 and MGF1 key wrap algorithm,
     *     using the JWE recipient's RSA Public Key, producing encrypted key ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Decrypts the encrypted key ciphertext with the RSA OAEP with SHA-1 and MGF1 key unwrap algorithm,
     *     using the JWE recipient's RSA Private Key, producing the decryption key plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final RsaKeyAlgorithm RSA_OAEP = forId0("RSA-OAEP");

    /**
     * Key Encryption with {@code RSAES OAEP using SHA-256 and MGF1 with SHA-256}, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.3">RFC 7518 (JWA), Section 4.3</a>.
     * This algorithm requires a 2048-bit key.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *     specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with the RSA OAEP with SHA-256 and MGF1 key wrap
     *     algorithm, using the JWE recipient's RSA Public Key, producing encrypted key ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Receives the encrypted key ciphertext embedded in the received JWE.</li>
     *     <li>Decrypts the encrypted key ciphertext with the RSA OAEP with SHA-256 and MGF1 key unwrap algorithm,
     *     using the JWE recipient's RSA Private Key, producing the decryption key plaintext.</li>
     *     <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *     JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}. </li>
     * </ol>
     */
    public static final RsaKeyAlgorithm RSA_OAEP_256 = forId0("RSA-OAEP-256");

    /**
     * Key Agreement with {@code ECDH-ES using Concat KDF} as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6">RFC J518 (JW), Section 4.6</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random Elliptic Curve public/private key pair on the same curve as the
     *     JWE recipient's EC Public Key.</li>
     *     <li>Generates a shared secret with the ECDH key agreement algorithm using the generated EC Private Key
     *     and the JWE recipient's EC Public Key.</li>
     *     <li><a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.2">Derives<a/> a symmetric Content
     *     Encryption {@code SecretKey} with the Concat KDF algorithm using the
     *     generated shared secret and any available
     *     {@link JweHeader#getAgreementPartyUInfo() PartyUInfo} and
     *     {@link JweHeader#getAgreementPartyVInfo() PartyVInfo}.</li>
     *     <li>Sets the generated EC key pair's Public Key as the required
     *      <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">&quot;epk&quot;
     *      (Ephemeral Public Key) Header Parameter</a> to be transmitted in the JWE.</li>
     *     <li>Returns the derived symmetric {@code SecretKey} for JJWT to use to encrypt the entire JWE with the
     *     associated {@link AeadAlgorithm}. Encrypted key ciphertext is not produced with this algorithm, so
     *     the resulting JWE will not contain any embedded key ciphertext.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Obtains the required ephemeral Elliptic Curve Public Key from the
     *      <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">&quot;epk&quot;
     *      (Ephemeral Public Key) Header Parameter</a>.</li>
     *     <li>Validates that the ephemeral Public Key is on the same curve as the recipient's EC Private Key.</li>
     *     <li>Obtains the shared secret with the ECDH key agreement algorithm using the obtained EC Public Key
     *      and the JWE recipient's EC Private Key.</li>
     *     <li><a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.2">Derives<a/> the symmetric Content
     *      Encryption {@code SecretKey} with the Concat KDF algorithm using the
     *      obtained shared secret and any available
     *      {@link JweHeader#getAgreementPartyUInfo() PartyUInfo} and
     *      {@link JweHeader#getAgreementPartyVInfo() PartyVInfo}.</li>
     *      <li>Returns the derived symmetric {@code SecretKey} for JJWT to use to decrypt the entire
     *      JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}.</li>
     * </ol>
     */
    public static final EcKeyAlgorithm ECDH_ES = forId0("ECDH-ES");

    /**
     * Key Agreement with Key Wrapping via
     * <code>ECDH-ES using Concat KDF and CEK wrapped with &quot;A128KW&quot;</code> as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6">RFC J518 (JW), Section 4.6</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random Elliptic Curve public/private key pair on the same curve as the
     *     JWE recipient's EC Public Key.</li>
     *     <li>Generates a shared secret with the ECDH key agreement algorithm using the generated EC Private Key
     *     and the JWE recipient's EC Public Key.</li>
     *     <li><a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.2">Derives<a/> a 128-bit symmetric Key
     *     Encryption {@code SecretKey} with the Concat KDF algorithm using the
     *     generated shared secret and any available
     *     {@link JweHeader#getAgreementPartyUInfo() PartyUInfo} and
     *     {@link JweHeader#getAgreementPartyVInfo() PartyVInfo}.</li>
     *     <li>Sets the generated EC key pair's Public Key as the required
     *      <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">&quot;epk&quot;
     *      (Ephemeral Public Key) Header Parameter</a> to be transmitted in the JWE.</li>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *      specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with the {@code A128KW} key wrap
     *      algorithm using the derived symmetric Key Encryption Key from step {@code #3}, producing encrypted key ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Obtains the required ephemeral Elliptic Curve Public Key from the
     *      <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">&quot;epk&quot;
     *      (Ephemeral Public Key) Header Parameter</a>.</li>
     *     <li>Validates that the ephemeral Public Key is on the same curve as the recipient's EC Private Key.</li>
     *     <li>Obtains the shared secret with the ECDH key agreement algorithm using the obtained EC Public Key
     *      and the JWE recipient's EC Private Key.</li>
     *     <li><a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.2">Derives<a/> the symmetric Key
     *      Encryption {@code SecretKey} with the Concat KDF algorithm using the
     *      obtained shared secret and any available
     *      {@link JweHeader#getAgreementPartyUInfo() PartyUInfo} and
     *      {@link JweHeader#getAgreementPartyVInfo() PartyVInfo}.</li>
     *      <li>Obtains the encrypted key ciphertext embedded in the received JWE.</li>
     *      <li>Decrypts the encrypted key ciphertext with the AES Key Unwrap algorithm using the
     *      128-bit derived symmetric key from step {@code #4}, producing the decryption key plaintext.</li>
     *      <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *      JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}.</li>
     * </ol>
     */
    public static final EcKeyAlgorithm ECDH_ES_A128KW = forId0("ECDH-ES+A128KW");

    /**
     * Key Agreement with Key Wrapping via
     * <code>ECDH-ES using Concat KDF and CEK wrapped with &quot;A192KW&quot;</code> as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6">RFC J518 (JW), Section 4.6</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random Elliptic Curve public/private key pair on the same curve as the
     *     JWE recipient's EC Public Key.</li>
     *     <li>Generates a shared secret with the ECDH key agreement algorithm using the generated EC Private Key
     *     and the JWE recipient's EC Public Key.</li>
     *     <li><a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.2">Derives<a/> a 192-bit symmetric Key
     *     Encryption {@code SecretKey} with the Concat KDF algorithm using the
     *     generated shared secret and any available
     *     {@link JweHeader#getAgreementPartyUInfo() PartyUInfo} and
     *     {@link JweHeader#getAgreementPartyVInfo() PartyVInfo}.</li>
     *     <li>Sets the generated EC key pair's Public Key as the required
     *      <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">&quot;epk&quot;
     *      (Ephemeral Public Key) Header Parameter</a> to be transmitted in the JWE.</li>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *      specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with the {@code A192KW} key wrap
     *      algorithm using the derived symmetric Key Encryption Key from step {@code #3}, producing encrypted key
     *      ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Obtains the required ephemeral Elliptic Curve Public Key from the
     *      <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">&quot;epk&quot;
     *      (Ephemeral Public Key) Header Parameter</a>.</li>
     *     <li>Validates that the ephemeral Public Key is on the same curve as the recipient's EC Private Key.</li>
     *     <li>Obtains the shared secret with the ECDH key agreement algorithm using the obtained EC Public Key
     *      and the JWE recipient's EC Private Key.</li>
     *     <li><a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.2">Derives<a/> the 192-bit symmetric
     *      Key Encryption {@code SecretKey} with the Concat KDF algorithm using the
     *      obtained shared secret and any available
     *      {@link JweHeader#getAgreementPartyUInfo() PartyUInfo} and
     *      {@link JweHeader#getAgreementPartyVInfo() PartyVInfo}.</li>
     *      <li>Obtains the encrypted key ciphertext embedded in the received JWE.</li>
     *      <li>Decrypts the encrypted key ciphertext with the AES Key Unwrap algorithm using the
     *      192-bit derived symmetric key from step {@code #4}, producing the decryption key plaintext.</li>
     *      <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *      JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}.</li>
     * </ol>
     */
    public static final EcKeyAlgorithm ECDH_ES_A192KW = forId0("ECDH-ES+A192KW");

    /**
     * Key Agreement with Key Wrapping via
     * <code>ECDH-ES using Concat KDF and CEK wrapped with &quot;A256KW&quot;</code> as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6">RFC J518 (JW), Section 4.6</a>.
     *
     * <p>During JWE creation, this algorithm:</p>
     * <ol>
     *     <li>Generates a new secure-random Elliptic Curve public/private key pair on the same curve as the
     *     JWE recipient's EC Public Key.</li>
     *     <li>Generates a shared secret with the ECDH key agreement algorithm using the generated EC Private Key
     *     and the JWE recipient's EC Public Key.</li>
     *     <li><a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.2">Derives<a/> a 256-bit symmetric Key
     *     Encryption {@code SecretKey} with the Concat KDF algorithm using the
     *     generated shared secret and any available
     *     {@link JweHeader#getAgreementPartyUInfo() PartyUInfo} and
     *     {@link JweHeader#getAgreementPartyVInfo() PartyVInfo}.</li>
     *     <li>Sets the generated EC key pair's Public Key as the required
     *      <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">&quot;epk&quot;
     *      (Ephemeral Public Key) Header Parameter</a> to be transmitted in the JWE.</li>
     *     <li>Generates a new secure-random content encryption {@link SecretKey} suitable for use with a
     *      specified {@link AeadAlgorithm} (using {@link AeadAlgorithm#keyBuilder()}).</li>
     *     <li>Encrypts this newly-generated {@code SecretKey} with the {@code A256KW} key wrap
     *      algorithm using the derived symmetric Key Encryption Key from step {@code #3}, producing encrypted key
     *      ciphertext.</li>
     *     <li>Returns the encrypted key ciphertext for inclusion in the final JWE as well as the newly-generated
     *     {@code SecretKey} for JJWT to use to encrypt the entire JWE with associated {@link AeadAlgorithm}.</li>
     * </ol>
     * <p>For JWE decryption, this algorithm:</p>
     * <ol>
     *     <li>Obtains the required ephemeral Elliptic Curve Public Key from the
     *      <a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.1.1">&quot;epk&quot;
     *      (Ephemeral Public Key) Header Parameter</a>.</li>
     *     <li>Validates that the ephemeral Public Key is on the same curve as the recipient's EC Private Key.</li>
     *     <li>Obtains the shared secret with the ECDH key agreement algorithm using the obtained EC Public Key
     *      and the JWE recipient's EC Private Key.</li>
     *     <li><a href="https://datatracker.ietf.org/doc/html/rfc7518#section-4.6.2">Derives<a/> the 256-bit symmetric
     *      Key Encryption {@code SecretKey} with the Concat KDF algorithm using the
     *      obtained shared secret and any available
     *      {@link JweHeader#getAgreementPartyUInfo() PartyUInfo} and
     *      {@link JweHeader#getAgreementPartyVInfo() PartyVInfo}.</li>
     *      <li>Obtains the encrypted key ciphertext embedded in the received JWE.</li>
     *      <li>Decrypts the encrypted key ciphertext with the AES Key Unwrap algorithm using the
     *      256-bit derived symmetric key from step {@code #4}, producing the decryption key plaintext.</li>
     *      <li>Returns the decryption key plaintext as a {@link SecretKey} for JJWT to use to decrypt the entire
     *      JWE using the JWE's identified &quot;enc&quot; {@link AeadAlgorithm}.</li>
     * </ol>
     */
    public static final EcKeyAlgorithm ECDH_ES_A256KW = forId0("ECDH-ES+A256KW");

    /*
    public static int estimateIterations(KeyAlgorithm<PasswordKey, PasswordKey> alg, long desiredMillis) {
        return Classes.invokeStatic(BRIDGE_CLASS, "estimateIterations", ESTIMATE_ITERATIONS_ARG_TYPES, alg, desiredMillis);
    }
     */
}
