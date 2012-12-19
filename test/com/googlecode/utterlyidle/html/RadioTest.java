package com.googlecode.utterlyidle.html;

import org.junit.Test;

import static com.googlecode.totallylazy.Xml.document;
import static com.googlecode.totallylazy.Xml.selectElements;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class RadioTest {

    
    public static final String XML = "<html>"
            + "<input type=\"radio\" value=\"First\" name=\"radio\" />First<br/>"
            + "<input type=\"radio\" value=\"Second\" name=\"radio\" />Second<br/>"
            + "<input type=\"radio\" value=\"Third\" name=\"radio\" />Third<br/>"
            + "</html>";

    @Test
    public void canSetValue() throws Exception {
        Radio radio = new Radio(selectElements(document(XML), "/html/input[@name=\"radio\"]"));
        assertThat(radio.value(),is(nullValue()));
        radio.value("Second");
        assertThat(radio.value(),is(equalTo("Second")));
        radio.value("YeahRight");
        assertThat(radio.value(), is(nullValue()));
    }
    
    @Test
    public void isNamed() {
        Radio radio = new Radio(selectElements(document(XML), "/html/input[@name=\"radio\"]"));
        assertThat(radio.name(), is(equalTo("radio")));
    }
}
