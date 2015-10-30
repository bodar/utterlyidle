package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.functions.Function2;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.ArrayList;
import java.util.List;

public class Decorators {
    private final List<DecoratorRule> rules = new ArrayList<DecoratorRule>();
    private final DecoratorProvider provider;

    public Decorators(DecoratorProvider provider) {
        this.provider = provider;
    }

    public Decorators add(DecoratorRule rule) {
        rules.add(rule);
        return this;
    }

    public Decorator getDecoratorFor(Request request, Response response) {
        for (DecoratorRule rule : rules) {
            if (rule.matches(Pair.pair(request, response))) {
                return provider.get(rule.templateName(), request);
            }
        }
        return new NoneDecorator();
    }

    public static Function2<? super Decorators, ? super DecoratorRule, Decorators> add() {
        return (decorators, rule) -> decorators.add(rule);
    }



}
