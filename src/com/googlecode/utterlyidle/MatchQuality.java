package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.numbers.Numbers;

import java.util.Comparator;

class MatchQuality implements Comparator<Binding> {
    private final Request request;

    private MatchQuality(Request request) {
        this.request = request;
    }

    public int compare(Binding firstSignature, Binding secondSignature) {
        float firstQuality = new ProducesMimeMatcher(firstSignature.produces()).matchQuality(request);
        float secondQuality = new ProducesMimeMatcher(secondSignature.produces()).matchQuality(request);

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
