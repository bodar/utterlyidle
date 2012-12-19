package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;

public class QueryParamRule implements DecoratorRule {
    private final String name;
    private String templateName;

    public QueryParamRule(String name) {
        this.name = name;
    }

    public static DecoratorRule queryParamRule(final String name) {
        return new QueryParamRule(name);
    }

    @Override
    public TemplateName templateName() {
        return TemplateName.templateName(templateName);
    }

    @Override
    public boolean matches(Pair<Request, Response> other) {
        String value = Requests.query(other.first()).getValue(name);
        if(Strings.isEmpty(value)){
            return false;
        }
        templateName = value;
        return true;
    }
}
