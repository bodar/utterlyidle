package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Either;
import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.bindings.BindingMatcher;
import org.junit.Test;

import static com.googlecode.utterlyidle.Entity.empty;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.annotations.HttpMethod.HEAD;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

public class HeadRequestHandlerTest {
    public static final String ENTITY = "entity";
    public static final String TARGET = "whatever";
    public static final Status NOT_SUCCESSFUL_STATUS = NOT_FOUND;

    Request HEAD_REQUEST = new RequestBuilder(HEAD, TARGET).build();
    Request NOT_HEAD_REQUEST = new RequestBuilder(GET, TARGET).build();
    private RecordingHttpHandler httpHandler;


    @Test
    public void shouldReturnResponseWithNoEntityWhenTheResponseStatusIsOkAndNoHeadBindingMatch() throws Exception {
        HeadRequestHandler headHandler = respondsWith(response(OK).entity(ENTITY).build());

        Response response = headHandler.handle(HEAD_REQUEST);

        assertThat(response.status(), is(OK));
        assertThat(response.entity(), is(empty()));
        assertThat(httpHandler.lastRequest().method(), is(GET));
    }

    @Test
    public void shouldReturnResponseWithNoEntityWhenTheResponseStatusIsOtherThanOk() throws Exception {
        HeadRequestHandler headHandler = respondsWith(response(NOT_SUCCESSFUL_STATUS).entity(ENTITY).build());

        Response response = headHandler.handle(HEAD_REQUEST);

        assertThat(response.status(), is(NOT_SUCCESSFUL_STATUS));
        assertThat(response.entity(), is(empty()));
        assertThat(httpHandler.lastRequest().method(), is(GET));
    }

    @Test
    public void shouldReturnResponseWithEntityIfTheRequestMethodOtherThanHead() throws Exception {
        HeadRequestHandler headHandler = respondsWith(response(OK).entity(ENTITY).build());

        Response response = headHandler.handle(NOT_HEAD_REQUEST);

        assertThat(response.status(), is(OK));
        assertThat(response.entity(), is(not(empty())));
        assertThat(httpHandler.lastRequest().method(), is(NOT_HEAD_REQUEST.method()));
    }

    @Test
    public void shouldReturnResponseForHeadRequestIfTheHeadBindingMatches() throws Exception {
        HeadRequestHandler headHandler = createHeadRequestHandler(response(OK).build(), new ConstantBindingMatcher(true));

        headHandler.handle(HEAD_REQUEST);

        assertThat(httpHandler.lastRequest().method(), is(HEAD));
    }


    private HeadRequestHandler createHeadRequestHandler(Response response, BindingMatcher bindingMatcher) {
        httpHandler = RecordingHttpHandler.recordingHttpHandler(returnsResponse(response));
        return new HeadRequestHandler(httpHandler, bindingMatcher);
    }

    private HeadRequestHandler respondsWith(Response response) {
        return createHeadRequestHandler(response, new ConstantBindingMatcher(false));
    }

    private class ConstantBindingMatcher implements BindingMatcher {
        private final boolean willMatch;

        private ConstantBindingMatcher(boolean willMatch) {
            this.willMatch = willMatch;
        }

        @Override
        public Either<MatchFailure, Binding> match(final Request request) {
            return willMatch ?
                    Either.<MatchFailure, Binding>right(null) :
                    Either.<MatchFailure, Binding>left(null);
        }
    }

}