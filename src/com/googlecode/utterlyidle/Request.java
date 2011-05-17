package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.io.Url;

import java.io.InputStream;

public interface Request {
    public String method();

    public Url url();

    public Request url(Url url);

    public byte[] input();

    public HeaderParameters headers();

    public QueryParameters query();

    public CookieParameters cookies();

    public FormParameters form();
}