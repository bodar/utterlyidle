package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import static com.googlecode.totallylazy.Sequences.sequence;

public class SubDomainWildardVerifier implements HostnameVerifier {
    public static final String CommonName = "CN";

    @Override
    public boolean verify(final String host, final SSLSession sslSession) {
        try {
            return sequence(new LdapName(sslSession.getPeerPrincipal().getName()).getRdns()).
                    filter(new Predicate<Rdn>() {
                        @Override
                        public boolean matches(final Rdn rdn) {
                            return rdn.getType().equalsIgnoreCase(CommonName);
                        }
                    }).
                    map(new Callable1<Rdn, Object>() {
                        @Override
                        public Object call(final Rdn rdn) throws Exception {
                            return rdn.getValue();
                        }
                    }).safeCast(String.class).
                    exists(new Predicate<String>() {
                        @Override
                        public boolean matches(final String cn) {
                            return cn.equals(host) || SubDomainWildardVerifier.this.wildcardMatch(cn, host);
                        }
                    });
        } catch (Exception e) {
            return false;
        }
    }

    private boolean wildcardMatch(final String commonName, final String host) {
        return commonName.startsWith("*.") && host.endsWith(commonName.substring(2));
    }
}