package com.googlecode.utterlyidle.cookies;

import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.comment;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.domain;
import static com.googlecode.utterlyidle.cookies.CookieBuilder.modify;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class CookieBuilderTest {

    @Test
    public void canModifyExistingCookie() throws Exception {
        Cookie existing = Cookie.cookie("cookie1", "Custard Cream", comment("comment"));
        Cookie modified = modify(existing).value("Chocolate Hob Nob").attribute(domain("localhost")).build();

        assertThat(modified.name(), is(existing.name()));
        assertThat(modified.value(), is("Chocolate Hob Nob"));
        assertThat(modified.attributes(), containsInAnyOrder(comment("comment"), domain("localhost")));
    }
}