package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.cookies.CookieParameters;

public interface Request {
    public String method();

    public Uri uri();

    public HeaderParameters headers();

    public Entity entity();
}