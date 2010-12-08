package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class SiteMeshHandlerTest {
    @Test
    public void shouldDecorateHtml() throws Exception{
        OutputStream outputStream = new ByteArrayOutputStream();
        Response response = Response.response(outputStream);
        RequestHandler hello = write("<body>Hello</body>");
        RequestHandler decorator = new SiteMeshHandler(hello, null, new Includer());

        decorator.handle(get(null).build(), response);

        assertThat(outputStream.toString(), containsString("Hello World!"));
    }

    @Test
    public void shouldChooseFirstAppropriateDecorator() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        Response response = Response.response(outputStream);
        RequestHandler hello = write("<body>Hello</body>");

        List<Predicate<Pair<Request, Response>>> decorators = asList(none(), all());

        RequestHandler decorator = new SiteMeshHandler(hello, null, new Includer());

        decorator.handle(get(null).build(), response);

        assertThat(outputStream.toString(), containsString("Hello World!"));
    }

    private Predicate<Pair<Request, Response>> all() {
        return not(none());
    }

    private Predicate<Pair<Request, Response>> none() {
        return new Predicate<Pair<Request, Response>>() {
            public boolean matches(Pair<Request, Response> requestResponsePair) {
                return false;
            }
        };
    }

    private RequestHandler write(final String value) {
        return new RequestHandler() {
            public void handle(Request request, Response response) throws Exception {
                response.write(value);
                response.close();
            }
        };
    }

}
