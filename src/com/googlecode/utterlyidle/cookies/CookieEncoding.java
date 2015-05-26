package com.googlecode.utterlyidle.cookies;

public interface CookieEncoding {

    CookieEncoding NONE = new IdentityEncoding();
    CookieEncoding BASE64_ENCODING = new PrefixedEncoding(new Base64Encoding());
    CookieEncoding URL_ENCODING = new PrefixedEncoding(new UrlEncoding());

    String decode(String input);
    String encode(String input);
}
