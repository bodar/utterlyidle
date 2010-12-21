package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class StaticDecoratorRule implements DecoratorRule {
    private final Predicate<? super Pair<Request, Response>> predicate;
    private final TemplateName templateName;

    private StaticDecoratorRule(Predicate<? super Pair<Request, Response>> predicate, TemplateName templateName) {
        this.predicate = predicate;
        this.templateName = templateName;
    }

    public static DecoratorRule staticRule(Predicate<? super Pair<Request, Response>> predicate, TemplateName templateName) {
        return new StaticDecoratorRule(predicate, templateName);
    }

    public boolean matches(Pair<Request, Response> pair) {
        return predicate.matches(pair);
    }

    public TemplateName templateName() {
        return templateName;
    }

//    public static Predicate<DecoratorRule> matchingRule(final Request request, final Response response) {
//        return new Predicate<DecoratorRule>() {
//            public boolean matches(StaticDecoratorRule decoratorRule) {
//                return decoratorRule.matches(pair(request, response));
//            }
//        };
//    }
//
//
//    public static Callable1<? super DecoratorRule, TemplateName> asTemplateName() {
//        return new Callable1<DecoratorRule, TemplateName>() {
//            public TemplateName call(DecoratorRule decoratorRule) throws Exception {
//                return decoratorRule.templateName();
//            }
//        };
//    }
}
