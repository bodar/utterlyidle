package com.googlecode.utterlyidle.profiling;

import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.template.Templates;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.sitemesh.PropertyMap;
import com.googlecode.utterlyidle.sitemesh.PropertyMapParser;

import java.io.IOException;

import static com.googlecode.utterlyidle.Requests.query;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.profiling.ProfilingClient.profile;

public class ProfilingHandler implements HttpHandler {
    public static final String QUERY_PARAMETER = "profile";
    private final HttpHandler httpHandler;
    private final ProfilingData profilingData;

    public ProfilingHandler(final HttpHandler httpHandler, final ProfilingData profilingData) {
        this.httpHandler = httpHandler;
        this.profilingData = profilingData;
    }

    @Override
    public Response handle(Request request) throws Exception {
        if (!shouldProfile(request)) {
            return httpHandler.handle(request);
        }

        return decorate(profile(httpHandler, profilingData).handle(request));
    }

    public static boolean shouldProfile(Request request) {
        return query(request).contains(QUERY_PARAMETER);
    }

    private Response decorate(Response response) throws IOException {
        PropertyMap html = new PropertyMapParser().parse(response.entity().toString());
        Templates group = Templates.templates(getClass()).addDefault().extension("html");
        return modify(response).entity(group.get("profile").render(Maps.map(
                    "response", html,
                    "requests", profilingData.requests(),
                    "queries", profilingData.queries()
                        ))).build();
    }
}
