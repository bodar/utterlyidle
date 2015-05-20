package com.googlecode.utterlyidle.cookies;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static com.googlecode.totallylazy.Sequences.sequence;

public class EmptyCookiesMatcher extends TypeSafeDiagnosingMatcher<Iterable<Cookie>> {

    public static Matcher<Iterable<Cookie>> isEmpty() {
        return new EmptyCookiesMatcher();
    }

    private EmptyCookiesMatcher() {
    }

    @Override
    protected boolean matchesSafely(Iterable<Cookie> cookies, Description description) {
        return sequence(cookies).isEmpty();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("no cookies");
    }
}
