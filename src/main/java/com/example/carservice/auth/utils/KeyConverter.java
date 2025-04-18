package com.example.carservice.auth.utils;

import lombok.experimental.UtilityClass;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Utility class for converting PEM-encoded RSA keys into {@link PublicKey} and {@link PrivateKey} objects.
 * <p>
 * Uses BouncyCastle's {@link PEMParser} and {@link JcaPEMKeyConverter} to parse and convert the key data.
 * </p>
 *
 * <p>Example PEM format:</p>
 * <pre>
 * -----BEGIN PUBLIC KEY-----
 * ...
 * -----END PUBLIC KEY-----
 *
 * -----BEGIN PRIVATE KEY-----
 * ...
 * -----END PRIVATE KEY-----
 * </pre>
 *
 * @see org.bouncycastle.openssl.PEMParser
 * @see org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
 */
@UtilityClass
public class KeyConverter {

    /**
     * Converts a PEM-encoded RSA public key string into a {@link PublicKey} object.
     *
     * @param publicPemKey the PEM-formatted public key string
     * @return the converted {@link PublicKey} instance
     * @throws RuntimeException if the conversion fails due to parsing errors
     */
    public PublicKey convertPublicKey(final String publicPemKey) {

        final StringReader keyReader = new StringReader(publicPemKey);
        try {
            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo
                    .getInstance(new PEMParser(keyReader).readObject());
            return new JcaPEMKeyConverter().getPublicKey(publicKeyInfo);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    /**
     * Converts a PEM-encoded RSA private key string into a {@link PrivateKey} object.
     *
     * @param privatePemKey the PEM-formatted private key string
     * @return the converted {@link PrivateKey} instance
     * @throws RuntimeException if the conversion fails due to parsing errors
     */
    public PrivateKey convertPrivateKey(final String privatePemKey) {

        StringReader keyReader = new StringReader(privatePemKey);
        try {
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo
                    .getInstance(new PEMParser(keyReader).readObject());
            return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

}
