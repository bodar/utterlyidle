package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Xml;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static org.hamcrest.MatcherAssert.assertThat;

public class SelectTest {

    public static final String XML = "<html><select><option value=\"first\">First</option><option value=\"second\">Second</option></select></html>";

    @Test
    public void supportsEntries() throws Exception {
        Select select = new Select(Xml.selectElement(Xml.document(XML), "//select").get());

        Iterable<Pair<String,String>> entries = Sequences.sequence(select.options()).
                map(Option.asEntry());

        assertThat(entries, hasExactly(Pair.pair("first", "First"), Pair.pair("second", "Second")));

    }

}
