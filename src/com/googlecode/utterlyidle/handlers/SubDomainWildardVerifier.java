package com.googlecode.utterlyidle.handlers;

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
                    filter(rdn -> rdn.getType().equalsIgnoreCase(CommonName)).
                    map(Rdn::getValue).safeCast(String.class).
                    exists(cn -> cn.equals(host) || wildcardMatch(cn, host));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean wildcardMatch(final String commonName, final String host) {
        return commonName.startsWith("*.") && host.endsWith(commonName.substring(2));
    }
}
