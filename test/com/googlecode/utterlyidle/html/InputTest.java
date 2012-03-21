package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static com.googlecode.totallylazy.Xml.document;
import static com.googlecode.totallylazy.Xml.selectElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class InputTest {
    @Test
    public void supportsEnabledAndDisabled() throws Exception {
        Input enabledInput = new Input(selectElement(document("<html><input/></html>"), "//input").get());
        assertThat(enabledInput.enabled(), is(true));
        assertThat(enabledInput.disabled(), is(false));
        Input disabledInput = new Input(selectElement(document("<html><input disabled='disabled'/></html>"), "//input").get());
        assertThat(disabledInput.enabled(), is(false));
        assertThat(disabledInput.disabled(), is(true));
    }
}
