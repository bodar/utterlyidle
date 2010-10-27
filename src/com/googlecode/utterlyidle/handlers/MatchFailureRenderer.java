package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.IOException;
import java.net.URL;

import static com.googlecode.totallylazy.numbers.Numbers.increment;
import static com.googlecode.utterlyidle.io.Converter.asString;
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

        for (HttpMethodActivator httpMethodActivator : value.matchesSoFar()) {
            Request request = new RequestGenerator(httpMethodActivator.method()).generate(arguments());
            template.setAttribute("requests.{method, path, query}", request.method(), request.path(), request.query());
        }

        return template.toString();
    }

    private Sequence<Object> arguments() {
        return Sequences.repeat("AN_ARGUMENT").safeCast(Object.class);
    }
}
