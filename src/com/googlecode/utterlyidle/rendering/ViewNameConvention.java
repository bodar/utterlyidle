package com.googlecode.utterlyidle.rendering;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.bindings.MatchedBinding;

import java.util.concurrent.Callable;

import static com.googlecode.utterlyidle.io.HierarchicalPath.hierarchicalPath;
import static com.googlecode.utterlyidle.rendering.ViewName.viewName;

public class ViewNameConvention implements Callable<ViewName> {
    private final Request request;
    private final MatchedBinding matchedBinding;

    public ViewNameConvention(Request request, MatchedBinding matchedBinding) {
        this.request = request;
        this.matchedBinding = matchedBinding;
    }

    public ViewName call() throws Exception {
        return viewName(matchedBinding.value().view().
                getOrElse(defaultView()));
    }

    protected String defaultView() {
        return hierarchicalPath(request.uri().path()).file();
    }
}