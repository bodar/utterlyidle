package com.googlecode.utterlyidle.profiling;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Triple;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Responses;
import com.googlecode.utterlyidle.sitemesh.PropertyMap;
import com.googlecode.utterlyidle.sitemesh.PropertyMapParser;

import java.util.List;

import static com.googlecode.funclate.Model.model;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.profiling.FunclateModelRenderer.funclateModelRenderer;

public class ProfilingHandler implements HttpHandler {
    public static final String QUERY_PARAMETER = "profile";
    private Application application;
    private HttpHandler httpHandler;

    public ProfilingHandler(final Application application, final HttpHandler httpHandler) {
        this.application = application;
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(Request request) throws Exception {
        QueryParameters parameters = Requests.query(request);

        if (!parameters.contains(QUERY_PARAMETER)) {
            return httpHandler.handle(request);
        }

        StatsCollector stats = StatsCollector.begin();
        try {
            Parameters noProfile = parameters.remove(QUERY_PARAMETER).remove("decorator");
            Uri noProfileUri = request.uri().query(noProfile.toString().replace("?", ""));
            Request newRequest = Requests.request(request.method(), noProfileUri, request.headers(), request.entity());

            Response response = application.handle(newRequest);

            PropertyMap html = new PropertyMapParser().parse(new String(response.bytes(), "UTF-8"));

            List<Triple<Request, Response, Long>> pairs = stats.executionTimes();
            return render(Model.model().add("executionTimes", sequence(pairs).map(asModel()).toList()).
                    add("response", html));
        } finally {
            StatsCollector.end();
        }
    }

    private Response render(Model model) throws Exception {
        return Responses.response().bytes(funclateModelRenderer(ProfilingHandler.class).render(model).getBytes());
    }

    private Callable1<Triple<Request, Response, Long>, Model> asModel() {
        return new Callable1<Triple<Request, Response, Long>, Model>() {
            @Override
            public Model call(Triple<Request, Response, Long> triple) throws Exception {
                return model().add("path", triple.first().uri()).
                        add("statusCode", triple.second().status()).
                        add("executionTime", triple.third());
            }
        };
    }
}
