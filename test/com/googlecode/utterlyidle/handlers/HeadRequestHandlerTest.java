package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import org.junit.Test;

import static com.googlecode.utterlyidle.Entity.empty;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.annotations.HttpMethod.HEAD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

public class HeadRequestHandlerTest {
    public static final String ENTITY = "entity";
    public static final String TARGET = "whatever";
    public static final Status NOT_SUCCESSFUL_STATUS = NOT_FOUND;

    StubHttpHandler stubHttpHandler = new StubHttpHandler();
    HeadRequestHandler headHandler = new HeadRequestHandler(stubHttpHandler);
    Request HEAD_REQUEST = new RequestBuilder(HEAD, TARGET).build();
    Request NOT_HEAD_REQUEST = new RequestBuilder(GET, TARGET).build();

    @Test
    public void shouldReturnResponseWithNoEntityWhenTheResponseStatusIsOk() throws Exception {
        stubHttpHandler.respondsWith(response(OK).entity(ENTITY).build());

        Response response = headHandler.handle(HEAD_REQUEST);

        assertThat(response.status(), is(OK));
        assertThat(response.entity(), is(empty()));
    }

    @Test
    public void shouldReturnResponseWithNoEntityWhenTheResponseStatusIsOtherThanOk() throws Exception {
        stubHttpHandler.respondsWith(response(NOT_SUCCESSFUL_STATUS).entity(ENTITY).build());

        Response response = headHandler.handle(HEAD_REQUEST);

        assertThat(response.status(), is(NOT_SUCCESSFUL_STATUS));
        assertThat(response.entity(), is(empty()));
    }

    @Test
    public void shouldReturnResponseWithEntityIfTheRequestMethodOtherThanHead() throws Exception {
        stubHttpHandler.respondsWith(response(OK).entity(ENTITY).build());

        Response response = headHandler.handle(NOT_HEAD_REQUEST);

        assertThat(response.status(), is(OK));
        assertThat(response.entity(), is(not(empty())));
    }

}
