package com.googlecode.utterlyidle.flash;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.annotations.FormParam;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import org.junit.Test;

import static com.googlecode.funclate.Model.persistent.model;
import static com.googlecode.funclate.json.Json.toJson;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_XHTML_XML;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.cookies.CookieParameters.cookies;
import static com.googlecode.utterlyidle.flash.FlashHandler.FLASH_COOKIE;
import static org.junit.Assert.assertThat;

public class FlashHandlerTest {
	private static final String CLEARED_FLASH_COOKIE_VALUE = "{}";
	private final Application application = new RestApplication(basePath("/")).
			add(new FlashMessagesModule()).
			add(new ResourcesModule() {
				@Override
				public Resources addResources(Resources resources) throws Exception {
					return resources.add(annotatedClass(FlashResource.class));
				}
			});

	@Test
	public void shouldReturnFlashStateInCookie() throws Exception {
		Response redirectResponse = application.handle(post("/add").form("value", "Hello world").build());

		String redirectResponseCookie = flashCookie(redirectResponse);
		assertThat(
				"Should set flash cookies on non-2xx response",
                redirectResponseCookie,
				is(toJson(model().set("key", "Hello world"))));

		Response responseFromRedirectLocation = followRedirect(redirectResponse);

		String responseFromRedirectLocationCookie = flashCookie(responseFromRedirectLocation);

		assertThat(
				"Flash value should be taken from the last cookie sent",
				responseFromRedirectLocation.entity().toString(),
				is("Hello world"));

		assertThat(
				"Flash cookie should be removed on successful response so it only displays once",
                responseFromRedirectLocationCookie,
				is("{}"));
	}

	@Test
	public void shouldAppendValuesToFlashUntilASuccessfulResponseIsReturned() throws Exception {
		Response firstError = application.handle(post("/addAndError").
				form("value", "Error 1").build());
		String firstErrorFlashCookie = flashCookie(firstError);

		Response secondError = application.handle(withFlashCookie(firstErrorFlashCookie, post("/addAndError").
				form("value", "Error 2")).build());

		String secondErrorFlashCookie = flashCookie(secondError);

		assertThat(
				"Should append flash cookies on non-2xx response",
                secondErrorFlashCookie,
				is(toJson(model().
						add("key", "Error 1").
						add("key", "Error 2"))));
	}

	@Test
	public void onlySetCookieIfValueChanges() throws Exception {
		Response response = application.handle(withFlashCookie(CLEARED_FLASH_COOKIE_VALUE, post("/hi")).build());
		assertThat(cookies(response).contains(FLASH_COOKIE), is(false));
	}

	@Test
	public void thereIsNoNeedToSetTheFlashCookieIfItsValueIsEmptyJsonAndTheIncomingRequestHasNoFlashCookie () throws Exception {
		Response response = application.handle(post("/hi").build());
		assertThat(cookies(response).contains(FLASH_COOKIE), is(false));
	}

	private Response followRedirect(Response response) throws Exception {
		String flash = flashCookie(response);
		String redirectLocation = response.headers().getValue(LOCATION);
		return application.handle(withFlashCookie(flash, get(redirectLocation)).build());
	}

    private String flashCookie(Response response) {
        return cookies(response).getValue(FLASH_COOKIE);
    }


    private RequestBuilder withFlashCookie(String flash, RequestBuilder request) {
		return request.cookie(FLASH_COOKIE, flash);
	}

	public static class FlashResource {
		private final Redirector redirector;
		private final Flash flash;

		public FlashResource(Redirector redirector, Flash flash) {
			this.redirector = redirector;
			this.flash = flash;
		}

		@GET
        @Produces(TEXT_PLAIN)
		@Path("hi")
		public String hello(){
			return "hello";
		}

		@GET
        @Produces(APPLICATION_XHTML_XML)
		@Path("get")
		public Object get(){
			return flash.state().get("key");
		}

		@POST
		@Path("add")
		public Response set(@FormParam("value") String value){
			this.flash.add("key", value);
			return redirector.seeOther(method(on(FlashResource.class).get()));
		}

		@POST
		@Path("addAndError")
		public Response error(@FormParam("value") String value){
			this.flash.add("key", value);
			return response(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
