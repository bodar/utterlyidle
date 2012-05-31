package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Xml;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class SelectTest {

    public static final String XML = "<html><select><option value=\"first\">First</option><option value=\"second\">Second</option></select></html>";

    @Test
    public void supportsEntries() throws Exception {
        Iterable<Pair<String,String>> entries = Sequences.sequence(new Select(Xml.selectElement(Xml.document(XML), "/html/select").get()).options()).map(Option.asEntry());
        assertThat(entries, IsIterableContainingInOrder.contains(Pair.pair("first", "First"), Pair.pair("second", "Second")));

    }

}
