package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.cookies.CookieParameters;

public interface Request {
    public String method();

    public Uri uri();

    public Request uri(Uri uri);

    public byte[] input();

    public HeaderParameters headers();

    public QueryParameters query();

    public CookieParameters cookies();

    public FormParameters form();
}