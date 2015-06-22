package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Predicates;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.Matchers.matcher;
import static com.googlecode.utterlyidle.RequestBuilder.delete;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.head;
import static com.googlecode.utterlyidle.RequestBuilder.options;
import static com.googlecode.utterlyidle.RequestBuilder.post;
import static com.googlecode.utterlyidle.RequestBuilder.put;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MethodMatcherTest {
    @Test
    public void matchesIsCaseInsensitive() throws Exception {
        assertThat(new MethodMatcher("post").matches(post("/").build()), is(true));
    }

    @Test
    public void allShouldMatchEveryVerb() throws Exception {
        assertThat(sequence(post("/"), get("/"), put("/"), delete("/"), head("/"), options("/")).map(matches()),
                matcher(Predicates.<Boolean>forAll(Predicates.is(true))));
    }

    private Function1<RequestBuilder, Boolean> matches() {
        return request -> new MethodMatcher("*").matches(request.build());
    }
}
