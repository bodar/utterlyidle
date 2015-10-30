package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicates;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.Matchers.matcher;
import static com.googlecode.utterlyidle.Request.Builder.delete;
import static com.googlecode.utterlyidle.Request.Builder.get;
import static com.googlecode.utterlyidle.Request.Builder.head;
import static com.googlecode.utterlyidle.Request.Builder.options;
import static com.googlecode.utterlyidle.Request.Builder.post;
import static com.googlecode.utterlyidle.Request.Builder.put;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MethodMatcherTest {
    @Test
    public void matchesIsCaseInsensitive() throws Exception {
        assertThat(new MethodMatcher("post").matches(post("/")), is(true));
    }

    @Test
    public void allShouldMatchEveryVerb() throws Exception {
        assertThat(sequence(post("/"), get("/"), put("/"), delete("/"), head("/"), options("/")).map(matches()),
                matcher(Predicates.<Boolean>forAll(Predicates.is(true))));
    }

    private Function1<Request, Boolean> matches() {
        return request -> new MethodMatcher("*").matches(request);
    }
}
