package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.Renderer;

public class MatchFailureRenderer implements Renderer<MatchFailure> {
    public String render(MatchFailure value) {
        return String.format("%s\n\nDid you mean one of these resources:\n%s", value.status(), value.matchesSoFar().toString("\n"));
    }
}
