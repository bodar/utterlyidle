package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.TestApplication;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.yadic.Container;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.MediaType.TEXT_XML;
import static com.googlecode.utterlyidle.PathMatcher.path;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.io.Url.url;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;
import static com.googlecode.utterlyidle.sitemesh.MetaTagRule.metaTagRule;
import static com.googlecode.utterlyidle.sitemesh.StaticDecoratorRule.staticRule;
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
                sequence(staticRule(path(BasePath.basePath("/"), "foo"), templateName("neverGetsHere")),
                        staticRule(path(BasePath.basePath("/"), "bar"), templateName(VALID_TEMPLATE_NAME))),
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
                "My name is fred", "hello" ,ServerSideIncludeResource.class);
    }

    private Predicate<Pair<Request, Response>> onlyMatchRequestTo(final String path) {
        return new Predicate<Pair<Request, Response>>() {
            public boolean matches(Pair<Request, Response> other) {
                return other.first().url().toString().equals(path);
            }
        };
    }

    private void assertDecorationResultsInResponse(final Sequence<DecoratorRule> decorators, final String result) throws Exception {
        assertDecorationResultsInResponse(decorators, result, "bar", SomeResource.class);
    }

    private void assertDecorationResultsInResponse(Sequence<DecoratorRule> decoratorRules, final String result, final String path, final Class resourceClass) throws Exception {
        TestApplication application = new TestApplication();
        Decorators decorators = new StringTemplateDecorators(url(getClass().getResource("world.st")).parent(), basePath("/"), application);
        for (DecoratorRule decoratorRule : decoratorRules) {
            decorators.add(decoratorRule);
        }
        application.add(new SiteMeshTestModule(decorators, resourceClass));
        Response response = application.handle(get(path));
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
        @Path("world")
        @Produces(MediaType.TEXT_HTML)
        public String include() {
            return "fred";
        }
    }

    public static class SiteMeshTestModule implements RequestScopedModule, ResourcesModule {
        private final Decorators decorators;
        private final Class resourceClass;

        public SiteMeshTestModule(Decorators decorators, Class resourceClass) {
            this.decorators = decorators;
            this.resourceClass = resourceClass;
        }

        public Module addPerRequestObjects(Container container) {
            container.addInstance(Decorators.class, decorators);
            container.decorate(HttpHandler.class, SiteMeshHandler.class);
            return this;
        }

        public Module addResources(Resources resources) {
            resources.add(annotatedClass(resourceClass));
            return this;
        }
    }
}
