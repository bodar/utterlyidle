package com.googlecode.utterlyidle.flash;

import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.json.Json;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;
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

import static com.googlecode.totallylazy.Arrays.list;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.BasePath.basePath;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_XHTML_XML;
import static com.googlecode.utterlyidle.MediaType.TEXT_PLAIN;
import static com.googlecode.utterlyidle.Request.Builder.cookie;
import static com.googlecode.utterlyidle.Request.Builder.form;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.utterlyidle.Request.post;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.cookies.CookieParameters.cookies;
import static com.googlecode.utterlyidle.flash.FlashHandler.FLASH_COOKIE;
import static org.junit.Assert.assertThat;

public class FlashHandlerTest {
	private static final String CLEARED_FLASH_COOKIE_VALUE = "{}";
	private final Application application = new RestApplication(basePath("/")).
			add(new FlashMessagesModule()).
			add((ResourcesModule) resources -> resources.add(annotatedClass(FlashResource.class)));

	@Test
	public void shouldReturnFlashStateInCookie() throws Exception {
		Response redirectResponse = application.handle(Request.post("/add", form("value", "Hello world")));

		String redirectResponseCookie = flashCookie(redirectResponse);
		assertThat(
				"Should set flash cookies on non-2xx response",
                redirectResponseCookie,
				is(Json.json(Maps.map("key", "Hello world"))));

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
		Response firstError = application.handle(Request.post("/addAndError", form("value", "Error 1")));
		String firstErrorFlashCookie = flashCookie(firstError);

		Response secondError = application.handle(withFlashCookie(firstErrorFlashCookie,
				Request.post("/addAndError", form("value", "Error 2"))));

		String secondErrorFlashCookie = flashCookie(secondError);

		assertThat(
				"Should append flash cookies on non-2xx response",
                secondErrorFlashCookie,
				is(Json.json(Maps.map("key", list("Error 1", "Error 2")))));
	}

	@Test
	public void onlySetCookieIfValueChanges() throws Exception {
		Response response = application.handle(withFlashCookie(CLEARED_FLASH_COOKIE_VALUE, Request.get("/hi")));
		assertThat(response.status(), is(OK));
		assertThat(cookies(response).contains(FLASH_COOKIE), is(false));
	}

	@Test
	public void thereIsNoNeedToSetTheFlashCookieIfItsValueIsEmptyJsonAndTheIncomingRequestHasNoFlashCookie () throws Exception {
		Response response = application.handle(Request.get("/hi"));
		assertThat(response.status(), is(OK));
		assertThat(cookies(response).contains(FLASH_COOKIE), is(false));
	}

	@Test
	public void migratesPreviousEmptyCookieValueToNewEmptyCookieValue () throws Exception {
		Response response = application.handle(withFlashCookie("", Request.get("/hi")));
		assertThat(response.status(), is(OK));
		assertThat(cookies(response).getValue(FLASH_COOKIE), is(CLEARED_FLASH_COOKIE_VALUE));
	}

	private Response followRedirect(Response response) throws Exception {
		String flash = flashCookie(response);
		String redirectLocation = response.headers().getValue(LOCATION);
		return application.handle(withFlashCookie(flash, Request.get(redirectLocation)));
	}

    private String flashCookie(Response response) {
        return cookies(response).getValue(FLASH_COOKIE);
    }


    private Request withFlashCookie(String flash, Request request) {
		return modify(request, cookie(FLASH_COOKIE, flash));
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
