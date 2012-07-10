package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static com.googlecode.totallylazy.Xml.document;
import static com.googlecode.totallylazy.Xml.selectElement;
import static com.googlecode.utterlyidle.RequestBuilder.get;
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
        assertThat(form.submit(), is(get("/location").query("field", "value").build()));
    }
}
