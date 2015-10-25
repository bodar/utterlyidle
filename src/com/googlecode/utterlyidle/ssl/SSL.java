package com.googlecode.utterlyidle.ssl;

import com.googlecode.totallylazy.Maps;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static com.googlecode.totallylazy.LazyException.lazyException;

public class SSL {
    public static final String DEFAULT_SSL_CONTEXT = "TLSv1.2";
    private static final Map<String, String> storeType = Maps.map(
            "jks", "JKS",
            "p12", "PKCS12");

    public static KeyStore keyStore(SecureString password, File file) {
        try {
            try (InputStream inputStream = new FileInputStream(file)) {
                return keyStore(password, inputStream, storeType.get(extension(file)));
            }
        } catch (Exception e) {
            throw lazyException(e);
        }
    }

    public static KeyStore keyStore(final SecureString password, final InputStream inputStream) {
        return keyStore(password, inputStream, "JKS");
    }

    public static KeyStore keyStore(final SecureString password, final InputStream inputStream, final String type) {
        try {
            KeyStore keyStore = KeyStore.getInstance(type);
            keyStore.load(inputStream, password.characters());
            return keyStore;
        } catch (Exception e) {
            throw lazyException(e);
        }
    }

    private static String extension(File file) {
        String[] parts = file.getName().split("\\.");
        return parts[parts.length - 1];
    }

    public static SSLContext sslContext(KeyStore keyStore) {
        return sslContext(keyStore, DEFAULT_SSL_CONTEXT);
    }

    public static SSLContext sslContext(final KeyStore keyStore, final SecureString privateKeyPassword) {
        return sslContext(keyStore, DEFAULT_SSL_CONTEXT, privateKeyPassword);
    }

    public static SSLContext sslContext(final KeyStore keyStore, final String name, final SecureString privateKeyPassword) {
        return sslContext(name, keyManagers(keyStore, privateKeyPassword), trustManagers(keyStore));
    }

    public static SSLContext sslContext(final KeyStore keyStore, final String name) {
        return sslContext(name, null, trustManagers(keyStore));
    }

    public static SSLContext sslContext(final String name, final KeyManager[] keyManagers, final TrustManager[] trustManagers) {
        try {
            SSLContext context = SSLContext.getInstance(name);
            context.init(keyManagers, trustManagers, null);
            return context;
        } catch (Exception e) {
            throw lazyException(e);
        }
    }

    public static KeyManager[] keyManagers(final KeyStore keyStore, final SecureString secureString) {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, secureString.characters());
            return keyManagerFactory.getKeyManagers();
        } catch (Exception e) {
            throw lazyException(e);
        }
    }

    public static TrustManager[] trustManagers(final KeyStore keyStore) {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            return trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            throw lazyException(e);
        }
    }

    public static SSLContext sslContext() {
        try {
            return SSLContext.getDefault();
        } catch (Exception e) {
            throw lazyException(e);
        }
    }
}
