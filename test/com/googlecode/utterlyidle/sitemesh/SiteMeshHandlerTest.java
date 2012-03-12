package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Responses;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.Produces;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.URLs.packageUrl;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.MediaType.TEXT_XML;
import static com.googlecode.utterlyidle.PathMatcher.path;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;
import static com.googlecode.utterlyidle.sitemesh.MetaTagRule.metaTagRule;
import static com.googlecode.utterlyidle.sitemesh.QueryParamRule.queryParamRule;
import static com.googlecode.utterlyidle.sitemesh.StaticDecoratorRule.staticRule;
import static com.googlecode.utterlyidle.sitemesh.StringTemplateDecorators.stringTemplateDecorators;
import static com.googlecode.utterlyidle.sitemesh.TemplateName.templateName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SiteMeshHandlerTest {
    private static final String ORIGINAL_CONTENT = "<html><head><meta name=\"decorator\" content=\"world\"/></head><body>Hello</body></html>";
    private static final String DECORATED_CONTENT = "Hello World!";
    private static final String VALID_TEMPLATE_NAME = "world";

    @Test
    public void shouldAllowAccessToMetaProperties() throws Exception {
        assertDecorationResultsInResponse(
                sequence(staticRule(Predicates.<Pair<Request, Response>>always(), templateName("metadecorator"))),
                "Decorator is world");
    }

    @Test
    public void shouldSupportSelectingDecoratorByMetaTag() throws Exception {
        assertDecorationResultsInResponse(sequence(metaTagRule("decorator")), DECORATED_CONTENT);
    }

    @Test
    public void shouldSupportSelectingDecoratorByQueryParameter() throws Exception {
        assertDecorationResultsInResponse(sequence(queryParamRule("decorator")), DECORATED_CONTENT);
    }

    @Test
    public void shouldNotDecorateHtmlWhenNoDecorators() throws Exception {
        assertDecorationResultsInResponse(Sequences.<DecoratorRule>empty(), ORIGINAL_CONTENT);
    }

    @Test
    public void shouldOnlyDecorateHtml() throws Exception {
        assertDecorationResultsInResponse(
                sequence(staticRule(contentType(TEXT_XML).and(Predicates.<Pair<Request, Response>>always()), templateName(VALID_TEMPLATE_NAME))),
                ORIGINAL_CONTENT);
    }

    @Test
    public void shouldBeAbleToSelectBasedOnPath() throws Exception {
        assertDecorationResultsInResponse(
                sequence(staticRule(path("/foo"), templateName("neverGetsHere")),
                        staticRule(path("/bar"), templateName(VALID_TEMPLATE_NAME))),
                DECORATED_CONTENT);
    }


    @Test
    public void shouldDecorateHtml() throws Exception {
        assertDecorationResultsInResponse(
                sequence(staticRule(Predicates.<Pair<Request, Response>>always(), templateName(VALID_TEMPLATE_NAME))),
                DECORATED_CONTENT);
    }

    @Test
    public void shouldChooseFirstAppropriateDecorator() throws Exception {
        assertDecorationResultsInResponse(sequence(
                staticRule(Predicates.<Pair<Request, Response>>never(), templateName("shouldNeverSeeMe!")),
                staticRule(Predicates.<Pair<Request, Response>>always(), templateName(VALID_TEMPLATE_NAME))),
                DECORATED_CONTENT);
    }

    @Test
    public void shouldPerformServerSideIncludes() throws Exception {
        assertDecorationResultsInResponse(
                sequence(staticRule(onlyMatchRequestTo("hello"), templateName("templateWithServerSideInclude"))),
                "My name is fred", "hello", ServerSideIncludeResource.class);
    }

    @Test
    public void shouldNotPerformServerSideIncludeWhenResponseOtherThan200() throws Exception {
        assertDecorationResultsInResponse(
                sequence(staticRule(onlyMatchRequestTo("notFound"), templateName("templateWithServerSideIncludeNot200"))),
                "", "not200", ServerSideIncludeResource.class);
    }

    @Test
    public void shouldPerformServerSideIncludesEvenWhenUrlParameterIsATemplate() throws Exception {
        assertDecorationResultsInResponse(
                sequence(staticRule(onlyMatchRequestTo("hello"), templateName("templateWithServerSideIncludeWithTemplate"))),
                "My name is fred", "hello", ServerSideIncludeResource.class);
    }

    private Predicate<Pair<Request, Response>> onlyMatchRequestTo(final String path) {
        return new Predicate<Pair<Request, Response>>() {
            public boolean matches(Pair<Request, Response> other) {
                return other.first().uri().path().equals(path);
            }
        };
    }

    private void assertDecorationResultsInResponse(final Sequence<DecoratorRule> decorators, final String result) throws Exception {
        assertDecorationResultsInResponse(decorators, result, "bar", SomeResource.class);
    }

    private void assertDecorationResultsInResponse(final Sequence<DecoratorRule> decoratorRules, final String result, final String path, final Class resourceClass) throws Exception {
        Response response = application().
                addAnnotated(resourceClass).
                add(stringTemplateDecorators(packageUrl(SiteMeshHandlerTest.class), decoratorRules)).
                handle(get(path).query("decorator", VALID_TEMPLATE_NAME));
        assertThat(Strings.toString(response.bytes()), is(result));
    }

    public static class SomeResource {
        @GET
        @Path("bar")
        @Produces(MediaType.TEXT_HTML)
        public String html() {
            return ORIGINAL_CONTENT;
        }
    }

    public static class ServerSideIncludeResource {

        @GET
        @Path("hello")
        @Produces(MediaType.TEXT_HTML)
        public String get() {
            return "My name is";
        }

        @GET
        @Path("not200")
        @Produces(MediaType.TEXT_HTML)
        public Response not200() {
            return Responses.response(Status.UNAUTHORIZED);
        }

        @GET
        @Path("world")
        @Produces(MediaType.TEXT_HTML)
        public String include() {
            return "fred";
        }
    }

}
