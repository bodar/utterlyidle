package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.*;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import static com.googlecode.totallylazy.Sequences.iterate;
import static com.googlecode.totallylazy.numbers.Numbers.increment;

public class MatchFailureRenderer implements Renderer<MatchFailure> {
    private final BasePath basePath;

    public MatchFailureRenderer(BasePath basePath) {
        this.basePath = basePath;
    }

    public String render(MatchFailure value) {
        StringTemplateGroup group = new StringTemplateGroup("");
        StringTemplate template = group.getInstanceOf(getResourceRelativeTo(getClass(), "matchFailure"));
        template.setAttribute("base", basePath);
        template.setAttribute("status", value.status());

        for (HttpMethodActivator httpMethodActivator : value.matchesSoFar()) {
            Request request = new RequestGenerator(httpMethodActivator.method()).generate(arguments());
            template.setAttribute("request.{path, query}", request.path(), request.query());
        }

        return template.toString();
    }

    private Sequence<Object> arguments() {
        return Sequences.repeat("AN_ARGUMENT").safeCast(Object.class);
    }

    public static String getResourceRelativeTo(Class aClass, String resource) {
        return aClass.getPackage().getName().replace('.', '/') + "/" + resource;
    }

}
