package com.mobilewallet.android.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by GabrielK on 07-Jan-18.
 */

public class HttpsCertificateUtils {
    private final static String TAG = "CertificateUtils";
    private final static String ROOT_CA_CERTIFICATE_FILE_NAME = "MobileWalletRootCA.pem";
    private final static String CLIENT_CERT_FILE_NAME = "AndroidClient.p12";

    private static SSLContext mSslContextCache;

    private static void addCertFromFileToKeystore(Context context, KeyStore keyStore, String fileName)
            throws IOException, CertificateException, KeyStoreException {
        Certificate certificate = getCertificateFromFile(context, fileName);
        Log.i(TAG, "Certificate loaded. CA=" + ((X509Certificate) certificate).getSubjectDN());
        addCertToKeyStore(keyStore, certificate);
    }

    private static Certificate getCertificateFromFile(Context context, String fileName)
            throws IOException, CertificateException, KeyStoreException {
        InputStream certificateInputStream = null;
        try {
            AssetManager assetManager = context.getAssets();
            certificateInputStream = assetManager.open(fileName);

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(certificateInputStream);

            return certificate;
        } finally {
            if (certificateInputStream != null) {
                certificateInputStream.close();
            }
        }
    }

    private static void addCertToKeyStore(KeyStore keyStore, Certificate certificate)
            throws KeyStoreException {
        keyStore.setCertificateEntry("certificate", certificate);
    }

    private static KeyStore initializeKeystore()
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);

        return keyStore;
    }

    private static KeyStore initializeClientKeystore(Context context)
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");InputStream certificateInputStream = null;
        try {
            AssetManager assetManager = context.getAssets();
            certificateInputStream = assetManager.open(CLIENT_CERT_FILE_NAME);
            keyStore.load(certificateInputStream, "projekt".toCharArray());
        } finally {
            if (certificateInputStream != null) {
                certificateInputStream.close();
            }
        }

        return keyStore;
    }

    private static TrustManager[] initializeTrustManager(KeyStore keyStore)
            throws NoSuchAlgorithmException, KeyStoreException {
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        return tmf.getTrustManagers();
    }

    private static KeyManager[] initializeKeyManager(KeyStore keystore)
            throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        String kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgorithm);
        kmf.init(keystore, "changeit".toCharArray());

        return kmf.getKeyManagers();
    }

    public static SSLSocketFactory getSslFactoryWithTrustedCertificate(Context context)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
                IOException, KeyManagementException, UnrecoverableKeyException {
        if (mSslContextCache == null) {
            KeyStore keyStore = initializeKeystore();
            KeyStore clientKeyStore = initializeClientKeystore(context);

            addCertFromFileToKeystore(context, keyStore, ROOT_CA_CERTIFICATE_FILE_NAME);

            mSslContextCache = SSLContext.getInstance("TLS");
            mSslContextCache.init(initializeKeyManager(clientKeyStore), initializeTrustManager(keyStore), null);
        }

        return mSslContextCache.getSocketFactory();
    }

    public static SSLSocketFactory getSslFactoryWithTrustAll(Context context)
            throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            {
                // Not implemented
            }
        }};

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());

        return sc.getSocketFactory();
    }
}
