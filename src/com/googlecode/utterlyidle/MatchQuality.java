package com.googlecode.utterlyidle;

import java.util.Comparator;

import static com.googlecode.totallylazy.comparators.Comparators.comparators;
import static com.googlecode.totallylazy.comparators.Comparators.descending;
import static com.googlecode.totallylazy.comparators.Comparators.where;
import static com.googlecode.utterlyidle.Binding.functions.numberOfArguments;
import static com.googlecode.utterlyidle.Binding.functions.priority;
import static com.googlecode.utterlyidle.ProducesMimeMatcher.quality;

public class MatchQuality {
    public static Comparator<Binding> matchQuality(Request request) {
        return comparators(
                where(quality(request), descending(Float.class)),
                where(priority(), descending(Integer.class)),
                where(numberOfArguments(), descending(Integer.class))
        );
    }

}
