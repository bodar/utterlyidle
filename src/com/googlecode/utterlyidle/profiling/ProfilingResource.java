package com.googlecode.utterlyidle.profiling;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Triple;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Parameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.annotations.*;
import com.googlecode.utterlyidle.sitemesh.PropertyMap;
import com.googlecode.utterlyidle.sitemesh.PropertyMapParser;

import java.util.List;

import static com.googlecode.funclate.Model.model;
import static com.googlecode.totallylazy.Sequences.sequence;

@Produces(MediaType.TEXT_HTML)
public class ProfilingResource {
    private final Application application;

    public ProfilingResource(Application application) {
        this.application = application;
    }

    @GET
    @Path("{path:.*}")
    @Hidden
    public Model executionTime(@QueryParam("profile") String profile, Request request) throws Exception {
        StatsCollector instance = StatsCollector.begin();

        try {
            Parameters noProfile = Requests.query(request).remove("profile").remove("decorator");
            Uri noProfileUri = request.uri().query(noProfile.toString().replace("?", ""));
            Request newRequest = Requests.request(request.method(), noProfileUri, request.headers(), request.input());

            Response response = application.handle(newRequest);

            PropertyMapParser parser = new PropertyMapParser();
            PropertyMap map = parser.parse(new String(response.bytes(), "UTF-8"));

            List<Triple<Request, Response, Long>> pairs = instance.executionTimes();
            return Model.model().add("executionTimes", sequence(pairs).map(asModel()).toList()).
                    add("response", map);
        } finally {
            StatsCollector.end();
        }
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
