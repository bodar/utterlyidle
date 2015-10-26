package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Compose;
import com.googlecode.totallylazy.functions.Unary;
import com.googlecode.utterlyidle.proxies.NoProxy;
import com.googlecode.utterlyidle.proxies.ProxyFor;
import com.googlecode.utterlyidle.ssl.SSL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.time.Duration;

import static com.googlecode.totallylazy.Sequences.sequence;

public interface ClientConfiguration {
    default Duration timeout() { return Duration.ofSeconds(0); }

    default ProxyFor proxyFor() { return NoProxy.instance; }

    default HostnameVerifier hostnameVerifier() { return HttpsURLConnection.getDefaultHostnameVerifier(); }

    default SSLContext sslContext() { return SSL.sslContext(); }


    interface Builder {
        static ClientConfiguration clientConfiguration() {
            return new ClientConfiguration() { };
        }

        static ClientConfiguration clientConfiguration(Duration timeout, ProxyFor proxyFor, HostnameVerifier hostnameVerifier, SSLContext sslContext) {
            return new ClientConfiguration() {
                @Override
                public Duration timeout() {
                    return timeout;
                }

                @Override
                public ProxyFor proxyFor() {
                    return proxyFor;
                }

                @Override
                public HostnameVerifier hostnameVerifier() {
                    return hostnameVerifier;
                }

                @Override
                public SSLContext sslContext() {
                    return sslContext;
                }
            };
        }

        @SafeVarargs
        static ClientConfiguration clientConfiguration(Unary<ClientConfiguration>... builders) {
            return modify(clientConfiguration(), builders);
        }

        static ClientConfiguration modify(final ClientConfiguration clientConfiguration, final Unary<ClientConfiguration>... builders) {
            return sequence(builders).reduce(Compose.<ClientConfiguration>compose()).apply(clientConfiguration);
        }

        static Unary<ClientConfiguration> timeout(Duration duration){
            return config -> clientConfiguration(duration, config.proxyFor(), config.hostnameVerifier(), config.sslContext());
        }

        static Unary<ClientConfiguration> proxyFor(ProxyFor proxyFor){
            return config -> clientConfiguration(config.timeout(), proxyFor, config.hostnameVerifier(), config.sslContext());
        }

        static Unary<ClientConfiguration> hostnameVerifier(HostnameVerifier hostnameVerifier){
            return config -> clientConfiguration(config.timeout(), config.proxyFor(), hostnameVerifier, config.sslContext());
        }

        static Unary<ClientConfiguration> sslContext(SSLContext sslContext){
            return config -> clientConfiguration(config.timeout(), config.proxyFor(), config.hostnameVerifier(), sslContext);
        }
    }


}
