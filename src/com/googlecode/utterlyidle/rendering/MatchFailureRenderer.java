package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.annotations.ParametersExtractor;
import com.googlecode.utterlyidle.handlers.UrlStringTemplateGroup;
import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.utterlyidle.FormParameters.formParameters;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
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

        for (Activator activator : value.matchesSoFar().filter(not(hidden()))) {
            Method method = activator.method();
            HttpSignature httpSignature = activator.httpSignature();
            final String httpMethod = httpSignature.httpMethod();
            final UriTemplate uriTemplate = httpSignature.uriTemplate();
            final ParametersExtractor parametersExtractor = new ParametersExtractor(method, arguments());
            final QueryParameters queries = parametersExtractor.extract(queryParameters(), QueryParam.class);
            final FormParameters forms = parametersExtractor.extract(formParameters(), FormParam.class);
            
            model.add("resources", model().
                    add("method", httpMethod).
                    add("uriTemplate", uriTemplate).
                    add("query", asModel(queries)).
                    add("form", asModel(forms)));
        }

        return template.toString();
    }

    private Model asModel(Parameters<String, String> parameters) {
        Model result = model();
        for (Pair<String, String> parameter : parameters) {
            result.add(parameter.first(), parameter.second());
        }
        return result;
    }

    private Predicate<? super Activator> hidden() {
        return new Predicate<Activator>() {
            public boolean matches(Activator activator) {
                return activator.method().getAnnotation(Hidden.class) != null;
            }
        };
    }

    private Sequence<Object> arguments() {
        return Sequences.repeat(Strings.EMPTY).safeCast(Object.class);
    }
}
