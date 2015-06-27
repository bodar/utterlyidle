package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public interface DecoratorRule extends Predicate<Pair<Request, Response>> {
    TemplateName templateName();
}
