package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class SiteMeshHandlerTest {
    @Test
    public void shouldNotDecorateHtmlWhenNoDecorators() throws Exception{
        OutputStream outputStream = new ByteArrayOutputStream();
        Response response = Response.response(outputStream);
        RequestHandler hello = write("<body>Hello</body>");
        List<DecoratorRule> decorators = emptyList();
        RequestHandler decorator = new SiteMeshHandler(hello, null, new Includer(), decorators, getWorldGroup());

        decorator.handle(get(null).build(), response);

        assertThat(outputStream.toString(), containsString("<body>Hello</body>"));
    }

    @Test
    public void shouldDecorateHtml() throws Exception{
        OutputStream outputStream = new ByteArrayOutputStream();
        Response response = Response.response(outputStream);
        RequestHandler hello = write("<body>Hello</body>");
        List<DecoratorRule> decorators = asList(new DecoratorRule(Predicates.<Pair<Request, Response>>always(), new TemplateName("world")));
        RequestHandler decorator = new SiteMeshHandler(hello, null, new Includer(), decorators, getWorldGroup());

        decorator.handle(get(null).build(), response);

        assertThat(outputStream.toString(), containsString("Hello World!"));
    }

    @Test
    public void shouldChooseFirstAppropriateDecorator() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        Response response = Response.response(outputStream);
        RequestHandler hello = write("<body>Hello</body>");

        List<DecoratorRule> decorators = asList(new DecoratorRule(Predicates.<Pair<Request, Response>>never(), new TemplateName("shouldNeverSeeMe!")), new DecoratorRule(Predicates.<Pair<Request, Response>>always(), new TemplateName("world")));

        RequestHandler decorator = new SiteMeshHandler(hello, null, new Includer(), decorators, getWorldGroup());

        decorator.handle(get(null).build(), response);

        assertThat(outputStream.toString(), containsString("Hello World!"));
    }

    private StringTemplateGroup getWorldGroup() {
        StringTemplateGroup groups = new StringTemplateGroup("decorators");
        groups.defineTemplate("world", "$body$ World!");
        return groups;
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
