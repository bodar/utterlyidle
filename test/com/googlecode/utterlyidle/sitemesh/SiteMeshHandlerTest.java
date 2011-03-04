package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Strings;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.yadic.Container;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.PathMatcher.path;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.io.Url.url;
import static com.googlecode.utterlyidle.sitemesh.ContentTypePredicate.contentType;
import static com.googlecode.utterlyidle.sitemesh.MetaTagRule.metaTagRule;
import static com.googlecode.utterlyidle.sitemesh.StaticDecoratorRule.staticRule;
import static com.googlecode.utterlyidle.sitemesh.TemplateName.templateName;
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
                decorators().add(staticRule(path(BasePath.basePath("/"), "foo"), templateName("neverGetsHere"))).
                        add(staticRule(path(BasePath.basePath("/"), "bar"), templateName(VALID_TEMPLATE_NAME))),
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
        assertDecorationResultsInResponse(decorators, result, "bar");
    }

    private void assertDecorationResultsInResponse(final Decorators decorators, final String result, final String path) throws Exception {
        Response response = createApplication(decorators).handle(get(path));
        assertThat(Strings.toString(response.bytes()), is(result));
    }

    private TestApplication createApplication(Decorators decorators) {
        TestApplication application = new TestApplication();
        application.add(new SiteMeshTestModule(decorators, SomeResource.class));
        return application;
    }

    private Decorators decorators() {
        return new StringTemplateDecorators(url(getClass().getResource("world.st")).parent(), basePath("/"));
    }

    public static class SomeResource{
        @GET
        @Path("bar")
        @Produces(MediaType.TEXT_HTML)
        public String html(){
            return ORIGINAL_CONTENT;
        }

//        @GET
//        @Path("redirect")
//        public Response redirect(){
//            return Responses.seeOther("foo");
//        }
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
            resources.add(resourceClass);
            return this;
        }
    }
}
