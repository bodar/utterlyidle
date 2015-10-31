package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_JSON;
import static com.googlecode.utterlyidle.Request.Builder.contentType;
import static com.googlecode.utterlyidle.HttpMessage.Builder.entity;
import static com.googlecode.utterlyidle.Request.get;
import static com.googlecode.utterlyidle.Request.head;
import static com.googlecode.utterlyidle.Request.post;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.FOUND;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RedirectHttpHandlerTest {

    private final Request NON_GET_REQUEST = Request.head("http://redirected.url");
    private final Request NON_GET_REDIRECTED_REQUEST = Request.head("http://newtarg.et");
    private final Request REDIRECTED_GET_REQUEST = Request.get("http://newtarg.et");
    private final Request POST_REQUEST_WITH_ENTITY_AND_HEADERS = Request.post("http://redirected.url", entity("{'name': 'value'}"), contentType(APPLICATION_JSON));
    private final Request REDIRECTED_REQUEST_WITH_ENTITY_AND_HEADERS = Request.post("http://newtarg.et", entity("{'name': 'value'}"), contentType(APPLICATION_JSON));
    private final Response ANY_REDIRECTING_RESPONSE_BUT_SEE_OTHER = response(FOUND).header(LOCATION, "http://newtarg.et").build();
    private final Response REDIRECTING_RESPONSE_SEE_OTHER = response(SEE_OTHER).header(LOCATION, "http://newtarg.et").build();
    private final StubHttpHandler stubHttpHandler = new StubHttpHandler();
    private final RedirectHttpHandler redirectHttpHandler = new RedirectHttpHandler(stubHttpHandler);

    @Test
    public void shouldMakeAGetRequestToTheRedirectedUrlWhenResponseIsRedirectedWithSeeOtherStatusCode303() throws Exception{
        given(stubHttpHandler).whenRequestIs(NON_GET_REQUEST).thenReturns((REDIRECTING_RESPONSE_SEE_OTHER));

        redirectHttpHandler.handle(NON_GET_REQUEST);

        assertThat(stubHttpHandler.lastRequest(), is(REDIRECTED_GET_REQUEST));
    }

    @Test
    public void shouldOnlyModifyTheUriOfTheRequestWhenResponseIsARedirectionAndStatusCodeIsNot303() throws Exception {
        given(stubHttpHandler).whenRequestIs(NON_GET_REQUEST).thenReturns((ANY_REDIRECTING_RESPONSE_BUT_SEE_OTHER));

        redirectHttpHandler.handle(NON_GET_REQUEST);

        assertThat(stubHttpHandler.lastRequest(), is(NON_GET_REDIRECTED_REQUEST));
    }

    @Test
    public void shouldKeepTheRequestEntityAndHeadersWhenTheyExist() throws Exception {
        given(stubHttpHandler).whenRequestIs(POST_REQUEST_WITH_ENTITY_AND_HEADERS).thenReturns((ANY_REDIRECTING_RESPONSE_BUT_SEE_OTHER));

        redirectHttpHandler.handle(POST_REQUEST_WITH_ENTITY_AND_HEADERS);

        assertThat(stubHttpHandler.lastRequest(), is(REDIRECTED_REQUEST_WITH_ENTITY_AND_HEADERS));
    }

    private StubHttpHandler given(final StubHttpHandler stubHttpHandler) {
        return stubHttpHandler;
    }

    private static class StubHttpHandler implements HttpClient {

        private Request request;
        private Request lastRequest;
        final private Map<Request, Response> rules = new HashMap<>();

        @Override
        public Response handle(final Request request) throws Exception {
            this.lastRequest = request;
            final Response response = rules.get(request);
            if (response == null)
                return response(OK).entity("Success!").build();
            return response;
        }

        private StubHttpHandler whenRequestIs(final Request request) {
            this.request = request;
            return this;
        }

        private void thenReturns(final Response response) {
            rules.put(request,response);
        }

        public Request lastRequest() {
            return lastRequest;
        }
    }
}
