package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.sitemesh.DecoratorRule.asTemplateName;
import static com.googlecode.utterlyidle.sitemesh.DecoratorRule.matchingRule;

public class SiteMeshHandler implements RequestHandler {
    private final RequestHandler requestHandler;
    private final BasePath base;
    private final Includer include;
    private final Sequence<DecoratorRule> decorators;
    private final StringTemplateGroup group;

    public SiteMeshHandler(final RequestHandler requestHandler, final BasePath base, final Includer include, final Iterable<DecoratorRule> decorators, final StringTemplateGroup templateGroup) {
        this.requestHandler = requestHandler;
        this.base = base;
        this.include = include;
        this.decorators = sequence(decorators);
        group = templateGroup;
    }

    public void handle(final Request request, final Response response) throws Exception {
        Option<TemplateName> templateName = getTemplateNameFor(request, response);
        Response decoratedResponse = templateName.fold(response, decorateOutputStream(request));
        requestHandler.handle(request, decoratedResponse);
    }

    private Callable2<Response, TemplateName, Response> decorateOutputStream(final Request request) {
        return new Callable2<Response, TemplateName, Response>() {
            public Response call(Response response, TemplateName templateName) throws Exception {
                StringTemplate template = group.getInstanceOf(templateName.name());
                StringTemplateDecorator decorator = new StringTemplateDecorator(template, include, base, request.query());
                return response.output(new SiteMeshOutputStream(response.output(), decorator));

            }
        };
    }

    private Option<TemplateName> getTemplateNameFor(final Request request, final Response response) {
        return decorators.find(matchingRule(request, response)).map(asTemplateName());
    }
}