package com.googlecode.utterlyidle.rendering;

import com.googlecode.funclate.stringtemplate.EnhancedStringTemplateGroup;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.*;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.IOException;

import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.EMPTY;
import static com.googlecode.totallylazy.URLs.packageUrl;
import static com.googlecode.utterlyidle.rendering.Model.model;

public class MatchFailureRenderer implements Renderer<MatchFailure> {
    private final BasePath basePath;
    private final Redirector redirector;

    public MatchFailureRenderer(BasePath basePath, final Redirector redirector) {
        this.basePath = basePath;
        this.redirector = redirector;
    }

    public String render(MatchFailure value) throws IOException {
        StringTemplateGroup group = new EnhancedStringTemplateGroup(packageUrl(getClass()));
        Model model = model();
        StringTemplate template = group.getInstanceOf("matchFailure", model);
        model.add("base", basePath);
        model.add("status", value.status());

        for (Binding binding : value.matchesSoFar().filter(not(hidden()))) {
            final String httpMethod = binding.httpMethod();
            Sequence<NamedParameter> parameters = binding.namedParameters();

            model.add("resources", model().
                    add("method", httpMethod).
                    add("path",  redirector.uriOf(binding).path()).
                    add("query", asModel(parameters.filter(where(parametersClass(), matches(QueryParameters.class))))).
                    add("form", asModel(parameters.filter(where(parametersClass(), matches(FormParameters.class))))));
        }

        return template.toString();
    }

    public static Predicate<? super Class> matches(Class aClass) {
        return Predicates.is(aClass);
    }

    public static Callable1<NamedParameter, Class<? extends Parameters<String, String>>> parametersClass() {
        return new Callable1<NamedParameter, Class<? extends Parameters<String, String>>>() {
            public Class<? extends Parameters<String, String>> call(NamedParameter namedParameter) throws Exception {
                 return namedParameter.parametersClass();
            }
        };
    }

    private Model asModel(Sequence<NamedParameter> parameters) {
        Model result = model();
        for (NamedParameter parameter : parameters) {
            result.add(parameter.name(), parameter.defaultValue().getOrElse(EMPTY));
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
