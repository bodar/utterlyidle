package com.googlecode.utterlyidle.rendering;

import com.googlecode.utterlyidle.annotations.View;
import com.googlecode.utterlyidle.bindings.MatchedBinding;

import java.util.concurrent.Callable;

public class ViewConvention implements Callable<View> {
    private final MatchedBinding matchedBinding;

    public ViewConvention(MatchedBinding matchedBinding) {
        this.matchedBinding = matchedBinding;
    }

    public View call() throws Exception {
        return matchedBinding.value().view();
    }

}