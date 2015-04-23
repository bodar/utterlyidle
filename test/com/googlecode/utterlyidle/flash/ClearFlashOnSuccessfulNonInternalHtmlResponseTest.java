package com.googlecode.utterlyidle.flash;

import com.googlecode.utterlyidle.InternalRequestMarker;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.MediaType.TEXT_HTML;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;
import static com.googlecode.utterlyidle.Status.NOT_MODIFIED;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.handlers.ApplicationId.applicationId;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClearFlashOnSuccessfulNonInternalHtmlResponseTest {
    private ClearFlashOnSuccessfulNonInternalHtmlResponse clearFlashPredicate;

    @Before
    public void setUp() throws Exception {
        clearFlashPredicate = new ClearFlashOnSuccessfulNonInternalHtmlResponse(new InternalRequestMarker(applicationId()));
    }

    @Test
    public void matchesGivenResponseIsSuccessful() throws Exception {
        Request request = get("").build();
        Response response = response(OK).contentType(TEXT_HTML).build();

        assertThat(clearFlashPredicate.matches(pair(request, response)), is(true));
    }

    @Test
    public void matchesGivenResponseHasStatusNotModified() throws Exception {
        Request request = get("").build();
        Response response = response(NOT_MODIFIED).contentType(TEXT_HTML).build();

        assertThat(clearFlashPredicate.matches(pair(request, response)), is(true));
    }

    @Test
    public void doesNotMatchGivenResponseIsError() throws Exception {
        Request request = get("").build();
        Response response = response(INTERNAL_SERVER_ERROR).contentType(TEXT_HTML).build();

        assertThat(clearFlashPredicate.matches(pair(request, response)), is(false));
    }
}