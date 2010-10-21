package com.googlecode.utterlyidle;

import java.util.Comparator;

class MatchQuality implements Comparator<Activator> {
    private final Request request;

    private MatchQuality(Request request) {
        this.request = request;
    }

    public int compare(Activator first, Activator second) {
        float firstQuality = first.matchQuality(request);
        float secondQuality = second.matchQuality(request);
        if (firstQuality == secondQuality)
            return second.numberOfArguments() - first.numberOfArguments();
        return firstQuality > secondQuality ? -1 : 1;
    }

    public static MatchQuality matchQuality(Request request) {
        return new MatchQuality(request);
    }
}
