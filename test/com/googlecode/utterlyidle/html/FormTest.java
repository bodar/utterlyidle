package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static com.googlecode.totallylazy.Xml.document;
import static com.googlecode.totallylazy.Xml.selectElement;

public class FormTest {
    @Test(expected = IllegalStateException.class)
    public void shouldNotSubmitIfButtonDisabled() throws Exception {
        Form form = new Form(selectElement(document("<form><input disabled='disabled' type='submit'/></form>"), "//form").get());
        form.submit("//input");
    }
}
