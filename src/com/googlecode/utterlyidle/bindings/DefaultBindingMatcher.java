package com.googlecode.utterlyidle.bindings;

import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.Bindings;
import com.googlecode.utterlyidle.ExceptionLogger;
import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Status;

import static com.googlecode.totallylazy.Left.left;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Right.right;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.ConsumesMimeMatcher.contentMatches;
import static com.googlecode.utterlyidle.MatchFailure.matchFailure;
import static com.googlecode.utterlyidle.MatchQuality.matchQuality;
import static com.googlecode.utterlyidle.MethodMatcher.methodMatches;
import static com.googlecode.utterlyidle.ParametersExtractor.parametersMatches;
import static com.googlecode.utterlyidle.PathMatcher.pathMatches;
import static com.googlecode.utterlyidle.ProducesMimeMatcher.producesMatches;

public class DefaultBindingMatcher implements BindingMatcher {
    private final Bindings bindings;
    private final Application application;
    private final ExceptionLogger logger;

    public DefaultBindingMatcher(Bindings bindings, Application application, final ExceptionLogger logger) {
        this.bindings = bindings;
        this.application = application;
        this.logger = logger;
    }

    @Override
    public Either<MatchFailure, Binding> match(Request request) {
        return bestMatch(request, matchingBindings(request));

    }

    private Either<MatchFailure, Sequence<Binding>> matchingBindings(Request request) {
        return filter(
                    pair(pathMatches(request), Status.NOT_FOUND),
                    pair(methodMatches(request), Status.METHOD_NOT_ALLOWED),
                    pair(contentMatches(request), Status.UNSUPPORTED_MEDIA_TYPE),
                    pair(producesMatches(request), Status.NOT_ACCEPTABLE),
                    pair(parametersMatches(request, application, logger), Status.UNSATISFIABLE_PARAMETERS)
            );
    }

    private Either<MatchFailure, Binding> bestMatch(Request request, Either<MatchFailure, Sequence<Binding>> failureOrBindings) {
        if (failureOrBindings.isLeft())
            return left(failureOrBindings.left());
        return right(findBestMatch(request, failureOrBindings.right()));
    }

    private Binding findBestMatch(Request request, final Sequence<Binding> bindings) {
        return bindings.sortBy(matchQuality(request)).head();
    }

    private Either<MatchFailure, Sequence<Binding>> filter(Pair<? extends Predicate<Binding>, Status>... filterAndResult) {
        Sequence<Binding> activators = sequence(bindings);
        for (Pair<? extends Predicate<Binding>, Status> pair : filterAndResult) {
            Sequence<Binding> matchesSoFar = activators;
            activators = activators.filter(pair.first());
            if (activators.isEmpty()) {
                return left(matchFailure(pair.second(), matchesSoFar));
            }
        }
        return right(activators);
    }


}
