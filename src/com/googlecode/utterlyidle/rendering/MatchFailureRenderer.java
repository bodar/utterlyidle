package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.handlers.UrlStringTemplateGroup;
import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.IOException;
import java.lang.reflect.Type;

import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.io.Url.url;
import static com.googlecode.utterlyidle.rendering.Model.model;

public class MatchFailureRenderer implements Renderer<MatchFailure> {
    private final BasePath basePath;

    public MatchFailureRenderer(BasePath basePath) {
        this.basePath = basePath;
    }

    public String render(MatchFailure value) throws IOException {
        Url baseUrl = url(getClass().getResource("matchFailure.st")).parent();
        StringTemplateGroup group = new UrlStringTemplateGroup(baseUrl);
        Model model = model();
        StringTemplate template = group.getInstanceOf("matchFailure", model);
        model.add("base", basePath);
        model.add("status", value.status());

        for (Binding binding : value.matchesSoFar().filter(not(hidden()))) {
            final String httpMethod = binding.httpMethod();
            final UriTemplate uriTemplate = binding.uriTemplate();
            Sequence<NamedParameter> parameters = extractNamedParameters(binding.parameters());

            model.add("resources", model().
                    add("method", httpMethod).
                    add("uriTemplate", uriTemplate).
                    add("query", asModel(parameters.filter(where(parametersClass(), matches(QueryParameters.class))))).
                    add("form", asModel(parameters.filter(where(parametersClass(), matches(FormParameters.class))))));
        }

        return template.toString();
    }

    private Predicate<? super Class> matches(Class aClass) {
        return Predicates.is(aClass);
    }

    private Callable1<? super NamedParameter, Class<? extends Parameters<String, String>>> parametersClass() {
        return new Callable1<NamedParameter, Class<? extends Parameters<String, String>>>() {
            public Class<? extends Parameters<String, String>> call(NamedParameter namedParameter) throws Exception {
                 return namedParameter.parametersClass();
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Sequence<NamedParameter> extractNamedParameters(Sequence<Pair<Type, Option<NamedParameter>>> parameters) {
        return parameters.map(Callables.<Option<NamedParameter>>second()).filter(Predicates.<NamedParameter>some()).map(Callables.<NamedParameter>value());
    }

    private Model asModel(Sequence<NamedParameter> parameters) {
        Model result = model();
        for (NamedParameter parameter : parameters) {
            result.add(parameter.name(), Strings.EMPTY);
        }
        return result;
    }

    public static Predicate<? super Binding> hidden() {
        return new Predicate<Binding>() {
            public boolean matches(Binding binding) {
                return binding.hidden();
            }
        };
    }
}
