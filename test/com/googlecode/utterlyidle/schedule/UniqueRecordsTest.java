package com.googlecode.utterlyidle.schedule;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Keywords;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import org.junit.Test;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;
import static com.googlecode.lazyrecords.Record.constructors.record;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.totallylazy.matchers.NumberMatcher.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UniqueRecordsTest {
    private final Keyword<Integer> ID = keyword("ID", Integer.class).metadata(Keywords.unique, true);
    private final Keyword<Integer> SOME_OTHER_KEY = keyword("SOME_OTHER_KEY", Integer.class).metadata(Keywords.unique, true);
    private final Keyword<Uri> URI = keyword("URI", Uri.class).metadata(Keywords.unique, true);

    @Test
    public void removesRecordsWithSameUniqueField() throws Exception{
        Sequence<Record> records = sequence(record().set(ID, 12), record().set(ID, 12));
        assertThat(records.size(), is(2));
        assertThat(records.filter(new UniqueRecords(ID)).size(), is(1));
    }

    @Test
    public void ignoresDifferentRecords() throws Exception{
        Sequence<Record> records = sequence(record().set(ID, 12), record().set(ID, 23));
        assertThat(records.size(), is(2));
        assertThat(records.filter(new UniqueRecords(ID)).size(), is(2));
    }

    @Test
    public void ifUniqueKeyIsNotPresentAllowAllRecordsThrough() throws Exception{
        Sequence<Record> records = sequence(record().set(ID, 12), record().set(ID, 23));
        assertThat(records.size(), is(2));
        assertThat(records.filter(new UniqueRecords(SOME_OTHER_KEY)).size(), is(2));
    }

    @Test
    public void worksWithMultipleUniqueFields() throws Exception{
        Sequence<Record> records = sequence(record().set(ID, 12).set(URI, uri("http://server/")), record().set(ID, 12).set(URI, uri("http://DIFFERENT/")), record().set(ID, 12).set(URI, uri("http://server/")));
        assertThat(records.size(), is(3));
        assertThat(records.filter(new UniqueRecords(ID, URI)).size(), is(2));
    }
}
