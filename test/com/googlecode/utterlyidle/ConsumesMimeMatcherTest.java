package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.http.Uri;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.*;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_ATOM_XML;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_FORM_URLENCODED;
import static com.googlecode.utterlyidle.MediaType.APPLICATION_JSON;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Requests.request;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConsumesMimeMatcherTest {

    private final HeaderParameters headers = headerParameters().add(CONTENT_TYPE, APPLICATION_FORM_URLENCODED);
    private final Uri uri = Uri.uri("http://example.com");
    private final Request request = request("GET", uri, headers, null);

    @Test
    public void returnsFalseIfNoMatch() throws Exception {
        Predicate<Request> matcher = new ConsumesMimeMatcher(sequence(APPLICATION_FORM_URLENCODED));
        assertThat(matcher.matches(get("").build()), is(false));
    }

    @Test
    public void simpleMatches() throws Exception {
        Predicate<Request> matcher = new ConsumesMimeMatcher(sequence(APPLICATION_FORM_URLENCODED));
        assertThat(matcher.matches(request), is(true));
    }

    @Test
    public void matchesWithManyCandidates() throws Exception {
        Predicate<Request> matcher = new ConsumesMimeMatcher(sequence(APPLICATION_ATOM_XML, APPLICATION_FORM_URLENCODED, APPLICATION_JSON));
        assertThat(matcher.matches(request), is(true));
    }

    @Test
    public void matchesPartialContentType() throws Exception {
        Predicate<Request> matcher = new ConsumesMimeMatcher(sequence(APPLICATION_ATOM_XML, APPLICATION_FORM_URLENCODED, APPLICATION_JSON));
        assertThat(matcher.matches(request("GET", uri, headerParameters().add(CONTENT_TYPE, APPLICATION_FORM_URLENCODED + "; charset=UTF-8"), null)), is(true));
    }

    @Test
    public void simpleMisMatch() throws Exception {
        Predicate<Request> matcher = new ConsumesMimeMatcher(sequence(APPLICATION_ATOM_XML));
        assertThat(matcher.matches(request), is(false));
    }

}
