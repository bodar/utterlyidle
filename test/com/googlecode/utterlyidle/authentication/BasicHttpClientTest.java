package com.googlecode.utterlyidle.authentication;

import com.googlecode.totallylazy.matchers.Matchers;
import com.googlecode.utterlyidle.Base64;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.handlers.CompositeHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import org.junit.Test;

import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Predicates.nullValue;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.WWW_AUTHENTICATE;
import static com.googlecode.utterlyidle.Request.functions.header;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.UNAUTHORIZED;
import static com.googlecode.utterlyidle.authentication.Credential.credential;
import static com.googlecode.utterlyidle.authentication.ClientCredentials.clientCredentials;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returns;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicHttpClientTest {
    @Test
    public void handlesAuthenticateChallenge() throws Exception {
        Response notAuthorised = response(UNAUTHORIZED).
                header(WWW_AUTHENTICATE, format("Basic realm=\"example.com\"")).
                build();

        CompositeHandler server = CompositeHandler.compositeHandler().
                add(where(header(AUTHORIZATION), nullValue()), returns(notAuthorised)).
                add(where(header(AUTHORIZATION), is("Basic " + Base64.encode(bytes("dan:correct")))), returns(response(OK).build()));

        ClientCredentials credentials = clientCredentials(map("example.com", credential("dan", "correct")));
        HttpClient client = new BasicHttpClient(server, credentials);
        Response response = client.handle(get("/").build());
        assertThat(response.status(), Matchers.is(OK));
    }

    @Test
    public void handlesFailedAuthenticateChallenge() throws Exception {
        Response notAuthorised = response(UNAUTHORIZED).
                header(WWW_AUTHENTICATE, format("Basic realm=\"example.com\"")).
                build();

        CompositeHandler server = CompositeHandler.compositeHandler().
                add(where(header(AUTHORIZATION), nullValue()), returns(notAuthorised)).
                add(where(header(AUTHORIZATION), notNullValue()), returns(response(UNAUTHORIZED.description("Second attempt")).build()));

        ClientCredentials credentials = clientCredentials(map("example.com", credential("dan", "incorrect")));
        HttpClient client = new BasicHttpClient(server, credentials);
        Response response = client.handle(get("/").build());
        assertThat(response.status().description(), Matchers.is("Second attempt"));
    }
}