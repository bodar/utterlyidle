package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Hidden;
import com.googlecode.utterlyidle.HttpMethodActivator;
import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.Renderer;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestGenerator;
import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.IOException;

import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.utterlyidle.io.Url.url;

public class MatchFailureRenderer implements Renderer<MatchFailure> {
    private final BasePath basePath;

    public MatchFailureRenderer(BasePath basePath) {
        this.basePath = basePath;
    }

    public String render(MatchFailure value) throws IOException {
        Url baseUrl = url(getClass().getResource("matchFailure.st")).parent();
        StringTemplateGroup group = new UrlStringTemplateGroup(baseUrl);
        StringTemplate template = group.getInstanceOf("matchFailure");
        template.setAttribute("base", basePath);
        template.setAttribute("status", value.status());

        for (HttpMethodActivator httpMethodActivator : value.matchesSoFar().filter(not(hidden()))) {
            Request request = new RequestGenerator(httpMethodActivator.method()).generate(arguments());
            template.setAttribute("requests.{method, uri}", request.method(), request.url());
        }

        return template.toString();
    }

    private Predicate<? super HttpMethodActivator> hidden() {
        return new Predicate<HttpMethodActivator>() {
            public boolean matches(HttpMethodActivator httpMethodActivator) {
                return httpMethodActivator.method().getAnnotation(Hidden.class) != null;
            }
        };
    }

    private Sequence<Object> arguments() {
        return Sequences.repeat("REPLACE_ME").safeCast(Object.class);
    }
}
