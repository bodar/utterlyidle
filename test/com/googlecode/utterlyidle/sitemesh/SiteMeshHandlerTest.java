package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.googlecode.utterlyidle.MemoryResponse.response;
import static com.googlecode.utterlyidle.PathMatcher.path;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.io.Url.url;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;
import static com.googlecode.utterlyidle.sitemesh.MetaTagRule.metaTagRule;
import static com.googlecode.utterlyidle.sitemesh.StaticDecoratorRule.staticRule;
import static com.googlecode.utterlyidle.sitemesh.TemplateName.templateName;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SiteMeshHandlerTest {
    private static final String ORIGINAL_CONTENT = "<html><head><meta name=\"decorator\" content=\"world\"/></head><body>Hello</body></html>";
    private static final String DECORATED_CONTENT = "Hello World!";
    private static final String VALID_TEMPLATE_NAME = "world";

    @Test
    public void shouldAllowAccessToMetaProperties() throws Exception {
        assertDecorationResultsInResponse(
                decorators().add(staticRule(Predicates.<Pair<Request, Response>>always(), templateName("metadecorator"))),
                "Decorator is world");
    }

    @Test
    public void shouldSupportSelectingDecoratorByMetaTag() throws Exception {
        assertDecorationResultsInResponse(decorators().add(metaTagRule("decorator")), DECORATED_CONTENT);
    }

    @Test
    public void shouldNotDecorateHtmlWhenNoDecorators() throws Exception {
        assertDecorationResultsInResponse(decorators(), ORIGINAL_CONTENT);
    }

    @Test
    public void shouldOnlyDecorateHtml() throws Exception {
        assertDecorationResultsInResponse(
                decorators().add(staticRule(contentType(TEXT_XML).and(Predicates.<Pair<Request, Response>>always()), templateName(VALID_TEMPLATE_NAME))),
                ORIGINAL_CONTENT);
    }

    @Test
    public void shouldBeAbleToSelectBasedOnPath() throws Exception {
        assertDecorationResultsInResponse(
                decorators().add(staticRule(path("foo"), templateName("neverGetsHere"))).
                        add(staticRule(path("/bar"), templateName(VALID_TEMPLATE_NAME))),
                DECORATED_CONTENT,
                "bar");
    }


    @Test
    public void shouldDecorateHtml() throws Exception {
        assertDecorationResultsInResponse(
                decorators().add(staticRule(Predicates.<Pair<Request, Response>>always(), templateName(VALID_TEMPLATE_NAME))),
                DECORATED_CONTENT);
    }

    @Test
    public void shouldChooseFirstAppropriateDecorator() throws Exception {
        assertDecorationResultsInResponse(decorators().
                add(staticRule(Predicates.<Pair<Request, Response>>never(), templateName("shouldNeverSeeMe!"))).
                add(staticRule(Predicates.<Pair<Request, Response>>always(), templateName(VALID_TEMPLATE_NAME))),
                DECORATED_CONTENT);
    }

    private void assertDecorationResultsInResponse(final Decorators decorators, final String result) throws Exception {
        assertDecorationResultsInResponse(decorators, result, null);
    }

    private void assertDecorationResultsInResponse(final Decorators decorators, final String result, final String path) throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpHandler decorator = new SiteMeshHandler(write(ORIGINAL_CONTENT), decorators);
        decorator.handle(get(path).build(), response(outputStream));
        assertThat(outputStream.toString(), is(result));
    }

    private Decorators decorators() {
        return new StringTemplateDecorators(url(getClass().getResource("world.st")).parent());
    }


    private HttpHandler write(final String value) {
        return new HttpHandler() {
            public void handle(Request request, Response response) throws Exception {
                response.status(Status.OK);
                response.header(CONTENT_TYPE, TEXT_HTML);
                OutputStreamWriter writer = new OutputStreamWriter(response.output());
                writer.write(value);
                writer.close();
                response.close();
            }
        };
    }

}
