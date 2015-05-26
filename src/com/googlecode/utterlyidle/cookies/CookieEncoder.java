package com.googlecode.utterlyidle.cookies;

import static com.googlecode.utterlyidle.cookies.CookieBuilder.modify;

class CookieEncoder {

    static CookieEncoder cookieEncoder(CookieEncoding encoding) {
        return new CookieEncoder(encoding);
    }

    private CookieEncoding encoding;

    private CookieEncoder(CookieEncoding encoding) {
        this.encoding = encoding;
    }

    Cookie encode(Cookie cookie) {
        try {
            return modify(cookie).value(encoding.encode(cookie.value())).build();
        } catch (Exception e) {
            return cookie;
        }
    }

    Cookie decode(Cookie cookie) {
        try {
            return modify(cookie).value(encoding.decode(cookie.value())).build();
        } catch (Exception e) {
            return cookie;
        }
    }
}
