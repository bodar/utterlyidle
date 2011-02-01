package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.cookies.Cookies;
import com.googlecode.utterlyidle.io.Url;

import java.io.InputStream;

public interface Request {
    public String method();

    public Url url();

    public Request url(Url url);

    public InputStream input();

    public HeaderParameters headers();

    public QueryParameters query();

    public Cookies cookies();

    public FormParameters form();

    public ResourcePath resourcePath();

    public BasePath basePath();
}