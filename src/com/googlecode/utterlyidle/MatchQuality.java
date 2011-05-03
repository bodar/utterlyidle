package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.numbers.Numbers;

import java.util.Comparator;

class MatchQuality implements Comparator<HttpSignature> {
    private final Request request;

    private MatchQuality(Request request) {
        this.request = request;
    }

    public int compare(HttpSignature firstSignature, HttpSignature secondSignature) {
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
