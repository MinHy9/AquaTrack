package com.aquatrack.common.mqtt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

import javax.net.ssl.*;
import java.io.FileReader;
import java.security.*;
import java.security.cert.X509Certificate;

public class AwsIotMqttUtil {

    public static SSLSocketFactory getSocketFactory(String caFile, String certFile, String keyFile) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // Load CA certificate
        PEMParser reader = new PEMParser(new FileReader(caFile));
        X509Certificate caCert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate((X509CertificateHolder) reader.readObject());
        reader.close();

        // Load client certificate
        reader = new PEMParser(new FileReader(certFile));
        X509Certificate clientCert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate((X509CertificateHolder) reader.readObject());
        reader.close();

        // Load private key
        reader = new PEMParser(new FileReader(keyFile));
        PEMKeyPair keyPair = (PEMKeyPair) reader.readObject();
        PrivateKey privateKey = new JcaPEMKeyConverter()
                .setProvider("BC")
                .getPrivateKey(keyPair.getPrivateKeyInfo());
        reader.close();

        // Create KeyStore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca-certificate", caCert);
        keyStore.setKeyEntry("private-key", privateKey, "".toCharArray(), new java.security.cert.Certificate[]{clientCert});

        // KeyManager
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "".toCharArray());

        // TrustManager
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Create SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }
}
