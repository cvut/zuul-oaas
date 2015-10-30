/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.saml.sp.support

import groovy.util.logging.Slf4j
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMReader
import org.springframework.core.io.Resource
import org.springframework.util.Assert

import javax.security.auth.x500.X500Principal
import java.security.KeyPair
import java.security.KeyStore
import java.security.KeyStore.LoadStoreParameter
import java.security.PrivateKey
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * This class is used to build {@link KeyStore} from PEM-formatted X.509
 * certificates and PEM-formatted PKCS#8 private keys. These formats are
 * typically used with OpenSSL and are de facto standard on Linux systems.
 */
@Slf4j
class OpenSSLKeyStoreBuilder {

    /**
     * Password used to protect private keys stored in the KeyStore.
     * This is quite useless, but KeyStore doesn't allow null passwords.
     */
    static final KEY_PASSWORD = 'nopass'

    private final certFactory = CertificateFactory.getInstance('X.509')

    /**
     * The keystore being build.
     */
    final KeyStore keyStore = KeyStore.getInstance(KeyStore.defaultType)


    OpenSSLKeyStoreBuilder() {
        if (! Security.getProvider('BC')) {
            Security.addProvider(new BouncyCastleProvider())
        }
        keyStore.load((LoadStoreParameter) null)
    }

    /**
     * Adds the given trusted certificate to the keystore.
     *
     * If the given alias identifies an existing entry, the certificate in the existing
     * entry is overridden by the given certificate.
     *
     * @param alias The alias name.
     * @param certFile The PEM-formatted X.509 certificate to add into the keystore.
     * @return this (for method chaining)
     *
     * @throws IOException if the stream could not be opened.
     * @throws java.security.cert.CertificateException on parsing errors.
     * @throws java.security.KeyStoreException if the given alias already exists and does
     *         not identify an entry containing a trusted certificate, or this operation
     *         fails for some other reason.
     */
    OpenSSLKeyStoreBuilder addCertificate(String alias, Resource certFile) {

        def cert = readCertificate(certFile)

        log.debug 'Adding certificate for subject "{}" with alias "{}" to keystore.',
            certificateName(cert), alias
        keyStore.setCertificateEntry(alias, cert)

        this
    }

    /**
     * Adds the given key to the keystore.
     *
     * If the given alias already exists, the keystore information associated
     * with it is overridden by the given key (and possibly certificate chain).
     *
     * @param alias The alias name.
     * @param keyFile The PEM-formatted PKCS#8 private key to add into the keystore.
     * @param certsChainFile The certificate chain (as PEM-formatted X.509 certificates)
     *        for the corresponding public key.
     * @return this (for method chaining)
     *
     * @throws IOException if the stream could not be opened, or the key is not recognized.
     * @throws IllegalArgumentException if the key file doesn't represent a private key,
     *         or the certificate fail doesn't contain any certificates.
     * @throws java.security.KeyStoreException if the given key cannot be protected,
     *         or this operation fails for some other reason.
     */
    OpenSSLKeyStoreBuilder addKey(String alias, Resource keyFile, Resource certsChainFile) {

        def certs = readCertificates(certsChainFile)
        def key = readPemEncodedKey(keyFile)

        log.debug 'Adding key with alias "{}" for certificate with subject "{}" to keystore.',
            alias, certificateName(certs.first())
        keyStore.setKeyEntry(alias, key, KEY_PASSWORD.chars, certs as Certificate[])

        this
    }


    private readCertificate(Resource file) {
        certFactory.generateCertificate(file.inputStream) as X509Certificate
    }

    private readCertificates(Resource file) {

        def certs = certFactory.generateCertificates(file.inputStream).asList()
        Assert.notEmpty(certs, "No certificate found in ${file.filename}")

        certs as List<X509Certificate>
    }

    private readPemEncodedKey(Resource file) {

        def object = new PEMReader(file.inputStream.newReader()).readObject()
        if (object instanceof KeyPair) {
            object = object.private
        }
        Assert.isInstanceOf(PrivateKey, object, "File ${file} is not PEM encoded private key")

        object as PrivateKey
    }

    private certificateName(X509Certificate certificate) {
        certificate.subjectX500Principal.getName(X500Principal.CANONICAL)
    }
}
