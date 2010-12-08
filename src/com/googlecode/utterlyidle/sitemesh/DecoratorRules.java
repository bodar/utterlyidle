package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.sitemesh.DecoratorRule.asTemplateName;
import static com.googlecode.utterlyidle.sitemesh.DecoratorRule.matchingRule;

public class DecoratorRules {
    private final Sequence<DecoratorRule> decorators;

    private DecoratorRules(Iterable<DecoratorRule> decorators) {
        this.decorators = sequence(decorators);
    }

    public static DecoratorRules decoratorRules(Iterable<DecoratorRule> decorators) {
        return new DecoratorRules(decorators);
    }

    public static DecoratorRules decoratorRules(DecoratorRule... decorators) {
        return decoratorRules(sequence(decorators));
    }

    public Option<TemplateName> getTemplateNameFor(final Request request, final Response response) {
        return decorators.find(matchingRule(request, response)).map(asTemplateName());
    }
}
