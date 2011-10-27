package com.googlecode.utterlyidle;

import java.net.InetAddress;

public class ClientAddress {
    private final String value;

    private ClientAddress(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ClientAddress clientAddress(InetAddress address) {
        return clientAddress(address.getHostAddress());
    }

    public static ClientAddress clientAddress(String remoteAddr) {
        return new ClientAddress(remoteAddr);
    }
}
