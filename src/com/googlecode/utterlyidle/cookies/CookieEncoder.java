package com.googlecode.utterlyidle.cookies;

import static com.googlecode.utterlyidle.cookies.CookieBuilder.modify;

public class CookieEncoder {

    public static CookieEncoder cookieEncoder(CookieEncoding encoding) {
        return new CookieEncoder(encoding);
    }

    private CookieEncoding encoding;

    public CookieEncoder(CookieEncoding encoding) {
        this.encoding = encoding;
    }

    public Cookie encode(Cookie cookie) {
        try {
            return modify(cookie).value(encoding.encode(cookie.value())).build();
        } catch (Exception e) {
            return cookie;
        }
    }

    public Cookie decode(Cookie cookie) {
        try {
            return modify(cookie).value(encoding.decode(cookie.value())).build();
        } catch (Exception e) {
            return cookie;
        }
    }
}
