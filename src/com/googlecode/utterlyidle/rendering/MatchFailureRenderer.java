package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.totallylazy.template.Templates;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.FormParameters;
import com.googlecode.utterlyidle.MatchFailure;
import com.googlecode.utterlyidle.NamedParameter;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Renderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Strings.EMPTY;

public class MatchFailureRenderer implements Renderer<MatchFailure> {
    private final BasePath basePath;
    private final Redirector redirector;

    public MatchFailureRenderer(BasePath basePath, final Redirector redirector) {
        this.basePath = basePath;
        this.redirector = redirector;
    }

    public String render(MatchFailure value) throws IOException {
        Templates group = Templates.templates(getClass()).addDefault().extension("html");
        Map<String,Object> model = new HashMap<>();
        model.put("base", basePath);
        model.put("status", value.status());
        model.put("resources", value.matchesSoFar().filter(notHidden()).map(new Callable1<Binding, Map<String, Object>>() {
            @Override
            public Map<String, Object> call(final Binding binding) throws Exception {
                final String httpMethod = binding.httpMethod();
                final Sequence<NamedParameter> parameters = binding.namedParameters();

                return new HashMap<String,Object>() {{
                    put("method", httpMethod.equals("*") ? "ANY" : httpMethod);
                    put("path", redirector.uriOf(binding).path());
                    put("query", parameterAsModel(parameters.filter(paramsClassIs(QueryParameters.class))));
                    put("form", parameterAsModel(parameters.filter(paramsClassIs(FormParameters.class))));
                }};
            }
        }));
        return group.get("matchFailure").render(model);
    }

    private Predicate<NamedParameter> paramsClassIs(final Class<? extends Parameters> paramsClass) {
        return new Predicate<NamedParameter>() {
            @Override
            public boolean matches(final NamedParameter p) {
                return p.parametersClass().equals(paramsClass);
            }
        };
    }

    private LogicalPredicate<Binding> notHidden() {
        return not(new Predicate<Binding>() {
            @Override
            public boolean matches(final Binding binding) {
                return binding.hidden();
            }
        });
    }

    private Map<String,Object> parameterAsModel(Sequence<NamedParameter> parameters) {
        Map<String,Object> result = new HashMap<>();
        for (NamedParameter parameter : parameters) {
            result.put(parameter.name(), parameter.defaultValue().getOrElse(EMPTY));
        }
        return result;
    }

}
