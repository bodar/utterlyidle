package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callers;
import com.googlecode.totallylazy.CombinerFunction;
import com.googlecode.totallylazy.ReducerCombiner;
import com.googlecode.totallylazy.Unary;
import com.googlecode.totallylazy.callables.Compose;
import com.googlecode.utterlyidle.proxies.NoProxy;
import com.googlecode.utterlyidle.proxies.ProxyFor;
import com.googlecode.utterlyidle.ssl.SSL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import static com.googlecode.totallylazy.Sequences.sequence;

public interface ClientConfiguration {
    int timeout();

    ProxyFor proxyFor();

    HostnameVerifier hostnameVerifier();

    SSLContext sslContext();


    class Builder {
        public static ClientConfiguration clientConfiguration() {
            return clientConfiguration(0, NoProxy.instance, HttpsURLConnection.getDefaultHostnameVerifier(), SSL.sslContext());
        }

        public static ClientConfiguration clientConfiguration(final int timeout, final ProxyFor proxyFor, final HostnameVerifier hostnameVerifier, final SSLContext sslContext) {
            return new ClientConfiguration() {
                @Override
                public int timeout() {
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
        public static ClientConfiguration clientConfiguration(Unary<ClientConfiguration>... builders) {
            return modify(clientConfiguration(), builders);
        }

        public static ClientConfiguration modify(ClientConfiguration clientConfiguration, final Unary<ClientConfiguration>... builders) {
            for (Unary<ClientConfiguration> builder : builders) {
                clientConfiguration = Callers.call(builder, clientConfiguration);
            }
            return clientConfiguration;
        }

        public static Unary<ClientConfiguration> timeout(final int milliseconds){
            return new Unary<ClientConfiguration>() {
                @Override
                public ClientConfiguration call(final ClientConfiguration config) throws Exception {
                    return clientConfiguration(milliseconds, config.proxyFor(), config.hostnameVerifier(), config.sslContext());
                }
            };
        }

        public static Unary<ClientConfiguration> proxyFor(final ProxyFor proxyFor){
            return new Unary<ClientConfiguration>() {
                @Override
                public ClientConfiguration call(final ClientConfiguration config) throws Exception {
                    return clientConfiguration(config.timeout(), proxyFor, config.hostnameVerifier(), config.sslContext());
                }
            };
        }

        public static Unary<ClientConfiguration> hostnameVerifier(final HostnameVerifier hostnameVerifier){
            return new Unary<ClientConfiguration>() {
                @Override
                public ClientConfiguration call(final ClientConfiguration config) throws Exception {
                    return clientConfiguration(config.timeout(), config.proxyFor(), hostnameVerifier, config.sslContext());
                }
            };
        }

        public static Unary<ClientConfiguration> sslContext(final SSLContext sslContext){
            return new Unary<ClientConfiguration>() {
                @Override
                public ClientConfiguration call(final ClientConfiguration config) throws Exception {
                    return clientConfiguration(config.timeout(), config.proxyFor(), config.hostnameVerifier(), sslContext);
                }
            };
        }
    }


}
