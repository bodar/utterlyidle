package com.googlecode.utterlyidle.tagsoup;

import com.googlecode.totallylazy.Xml;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HtmlCleanerTest {
    @Test
    public void canGetDocumentFromBadlyFormedHtml() throws Exception {
        String html = "<html><head><body>sheep<br>cheese</body></html>";
        assertThat(Xml.asString(HtmlCleaner.document(html)), is(String.format("<html xmlns:html=\"http://www.w3.org/1999/xhtml\">%n<head>%n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">%n</head>%n<body>sheep<br clear=\"none\">cheese</body>%n</html>%n")));
    }
}
