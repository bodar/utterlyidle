package com.googlecode.utterlyidle.sitemesh;

import org.junit.Test;

import static com.googlecode.utterlyidle.sitemesh.TemplateName.templateName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TemplateNameTest {
    @Test
    public void supportsEquality() throws Exception {
        TemplateName template1 = templateName("some");
        TemplateName template2 = templateName("some");
        TemplateName template3 = templateName("123someOther");

        assertThat(template1.equals(template2), is(true));
        assertThat(template1.hashCode() == template2.hashCode(), is(true));
        assertThat(template1.equals(template3), is(false));
        assertThat(template1.hashCode() == template3.hashCode(), is(false));
    }
}
