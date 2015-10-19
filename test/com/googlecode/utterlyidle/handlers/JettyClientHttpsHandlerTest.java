package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Protocol;
import com.googlecode.utterlyidle.Server;
import com.googlecode.utterlyidle.proxies.NoProxy;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.jetty.eclipse.RestServer.restServer;

public class JettyClientHttpsHandlerTest extends AbstractTestClientHttpHandler {

    @Override
    protected Server server(Application application) throws Exception {
        return restServer(application, defaultConfiguration().protocol(Protocol.HTTPS), serverSslFactory());
    }

    @Override
    protected ClientHttpHandler clientHttpHandler(final int timeout) throws Exception {
        return new ClientHttpHandler(timeout, timeout, NoProxy.instance, clientSslFactory(), anyHostNameVerifier());
    }

    private HostnameVerifier anyHostNameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostName, SSLSession sslSession) {
                return true;
            }
        };
    }

    private SSLSocketFactory clientSslFactory() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("jks");
        trustStore.load(loadTrustStore(), "password".toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);
        return sslContext.getSocketFactory();
    }

    private FileInputStream loadTrustStore() throws FileNotFoundException, URISyntaxException {
        File trustStore = Paths.get("./test/com/googlecode/utterlyidle/handlers/testtruststore.jks").toFile();
        if (!trustStore.exists()) {
            throw new IllegalArgumentException("Trust store file not found: " + trustStore);
        }
        return new FileInputStream(trustStore);
    }

    private SslContextFactory serverSslFactory() throws URISyntaxException {
        SslContextFactory sslContextFactory = new SslContextFactory();
        Path keyStorePath = Paths.get("./test/com/googlecode/utterlyidle/handlers/testkeystore.jks");
        if (!Files.exists(keyStorePath)) {
            throw new IllegalArgumentException("Key store file not found: " + keyStorePath);
        }
        sslContextFactory.setKeyStorePath(keyStorePath.toAbsolutePath().toString());
        sslContextFactory.setKeyStorePassword("password");
        return sslContextFactory;
    }
}
