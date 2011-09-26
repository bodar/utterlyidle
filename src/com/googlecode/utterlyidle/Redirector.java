package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.proxy.Invocation;

public interface Redirector {
    Response redirectTo(final Invocation invocation);
    Uri uriOf(final Invocation invocation);
    Uri uriOf(final Binding binding, final Object... arguments);
}
