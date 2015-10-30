package com.googlecode.utterlyidle.html;

import org.junit.Test;

import java.util.NoSuchElementException;

import static com.googlecode.utterlyidle.html.Html.html;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class RadioTest {

    
    public static final String XML = "<html>"
            + "<input type=\"radio\" value=\"First\" name=\"radio\" /><span>First text</span><br/>"
            + "<input type=\"radio\" value=\"Second\" name=\"radio\" /><span>Second text</span><br/>"
            + "<input type=\"radio\" value=\"Third\" name=\"radio\" /><span>Third text</span><br/>"
            + "</html>";

    @Test
    public void canSetValue() throws Exception {
        Radio radio = new Radio(html(XML), "/html/input[@name=\"radio\"]");
        assertThat(radio.value(),is(nullValue()));
        radio.value("Second");
        assertThat(radio.value(),is(equalTo("Second")));

        radio.clearValue();
        assertThat(radio.value(), is(nullValue()));
    }
    
    @Test
    public void canSetValueBasedOnXPath() throws Exception {
        Radio radio = new Radio(html(XML), "/html/input[@name=\"radio\"]");
        assertThat(radio.value(),is(nullValue()));
        radio.valueWithXPath("self::*[following-sibling::span[1][text()='Second text']]");
        assertThat(radio.value(),is(equalTo("Second")));
    }

    @Test
    public void throwsAnExceptionWhenTryingToSetAValueThatDoesntExist() throws Exception {
        Radio radio = new Radio(html(XML), "/html/input[@name=\"radio\"]");
        String xpath = "not_there";
        try {
            radio.valueWithXPath(xpath);
            fail("Expected exception");
        } catch (NoSuchElementException e) {
            assertThat(e.getMessage(), containsString(xpath));
        }
    }

    @Test
    public void isNamed() throws Exception {
        Radio radio = new Radio(html(XML), "/html/input[@name=\"radio\"]");
        assertThat(radio.name(), is(equalTo("radio")));
    }
}
