package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.handlers.UrlStringTemplateGroup;
import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import javax.ws.rs.FormParam;
import javax.ws.rs.HttpMethod;
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

        for (HttpMethodActivator httpMethodActivator : value.matchesSoFar().filter(not(hidden()))) {
            Method method = httpMethodActivator.method();
            final HttpMethod httpMethod = new HttpMethodExtractor().extract(method).get();
            final UriTemplate uriTemplate = new UriTemplateExtractor().extract(method);
            final ParametersExtractor parametersExtractor = new ParametersExtractor(method, arguments());
            final QueryParameters queries = parametersExtractor.extract(queryParameters(), QueryParam.class);
            final FormParameters forms = parametersExtractor.extract(formParameters(), FormParam.class);
            
            model.add("resources", model().
                    add("method", httpMethod.value()).
                    add("uriTemplate", uriTemplate).
                    add("query", asModel(queries)).
                    add("form", asModel(forms)));
        }

        return template.toString();
    }

    private Model asModel(Parameters parameters) {
        Model result = model();
        for (Pair<String, String> parameter : (Iterable<Pair<String,String>>)parameters) {
            result.add(parameter.first(), parameter.second());
        }
        return result;
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
