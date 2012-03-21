package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.proxy.Invocation;

public interface Redirector {
    Response seeOther(final Uri relativeUri);
    Response seeOther(final Invocation invocation);
    Response seeOther(final Binding binding, final Object... arguments);
    Uri uriOf(final Invocation invocation);
    Uri uriOf(final Binding binding, final Object... arguments);
    Uri absoluteUriOf(final Uri relativeUri);
    Uri absoluteUriOf(final Invocation invocation);
    Uri absoluteUriOf(final Binding binding, final Object... arguments);
    Uri resourceUriOf(final Invocation invocation);
    Uri resourceUriOf(final Binding binding, final Object... arguments);
}
