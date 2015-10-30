package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static com.googlecode.totallylazy.xml.Xml.document;
import static com.googlecode.totallylazy.xml.Xml.selectElement;
import static com.googlecode.utterlyidle.Request.Builder.get;
import static com.googlecode.utterlyidle.Request.Builder.query;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FormTest {
    @Test(expected = IllegalStateException.class)
    public void shouldNotSubmitIfButtonDisabled() throws Exception {
        Form form = new Form(selectElement(document("<form><input disabled='disabled' type='submit'/></form>"), "//form").get());
        form.submit("//input");
    }

    @Test
    public void shouldSubmitGetMethodToActionWithFieldValues(){
        Form form = new Form(selectElement(document("<form action='/location' method='get'><input name='field' value='value'/></form>"), "//form").get());
        assertThat(form.submit(), is(get("/location", query("field", "value"))));
    }

    @Test
    public void canReturnTheAction(){
        Form form = new Form(selectElement(document("<form action='/location' method='get'></form>"), "//form").get());
        assertThat(form.action(), is(equalTo("/location")));
    }

    @Test
    public void canReturnTheMethod(){
        Form form = new Form(selectElement(document("<form action='/location' method='post'></form>"), "//form").get());
        assertThat(form.method(), is(equalTo("post")));
    }
}
