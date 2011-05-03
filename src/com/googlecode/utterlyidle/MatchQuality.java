package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.numbers.Numbers;

import java.util.Comparator;

class MatchQuality implements Comparator<Activator> {
    private final Request request;

    private MatchQuality(Request request) {
        this.request = request;
    }

    public int compare(Activator first, Activator second) {
        float firstQuality = first.matchQuality(request);
        float secondQuality = second.matchQuality(request);

        HttpSignature firstSignature = first.httpSignature();
        HttpSignature secondSignature = second.httpSignature();

        if (firstQuality == secondQuality){
            int firstPriority = firstSignature.priority();
            int secondPriority = secondSignature.priority();

            if(firstPriority == secondPriority){
                return Numbers.compare(secondSignature.numberOfArguments(), firstSignature.numberOfArguments());
            }

            return firstPriority > secondPriority ? -1 : 1;
        }

        return firstQuality > secondQuality ? -1 : 1;
    }

    public static MatchQuality matchQuality(Request request) {
        return new MatchQuality(request);
    }
}
