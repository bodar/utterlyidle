package com.googlecode.utterlyidle.io;

import com.googlecode.totallylazy.matchers.NumberMatcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HierarchicalPathTest {
    @Test
    public void correctlyJoinsPaths() throws Exception {
        HierarchicalPath path = new HierarchicalPath("");

        assertThat(path.segments().size(), NumberMatcher.is(0));
        assertThat(path.file("bob").toString(), is("bob"));
    }

    @Test
    public void correctlyJoinsPathsThatStartWithASlash() throws Exception {
        HierarchicalPath path = new HierarchicalPath("/");

        assertThat(path.segments().size(), NumberMatcher.is(1));
        assertThat(path.file("bob").toString(), is("/bob"));
    }

    @Test
    public void shouldBeComparable() {
        HierarchicalPath a = new HierarchicalPath("a");
        HierarchicalPath b = new HierarchicalPath("b");
        HierarchicalPath c = new HierarchicalPath("c");

        List<HierarchicalPath> paths = new ArrayList<HierarchicalPath>();
        paths.add(c);
        paths.add(a);
        paths.add(b);

        Collections.sort(paths);
        
        assertThat(paths, hasExactly(a,b,c));
    }

    @Test
    public void shouldSupportContainedBy() throws Exception {
        HierarchicalPath basePath = new HierarchicalPath("/twig/gui/");
        HierarchicalPath resourcePath = new HierarchicalPath("/twig/gui");
        HierarchicalPath anotherResourcePath = new HierarchicalPath("/twig/gui/directoryNumbers");
        HierarchicalPath otherPath = new HierarchicalPath("/twig/funnyfarm");

        assertThat(resourcePath.containedBy(basePath), is(true));
        assertThat(anotherResourcePath.containedBy(basePath), is(true));
        assertThat(otherPath.containedBy(basePath), is(false));
    }

    @Test
    public void shouldSupportRemove() throws Exception {
        HierarchicalPath path = new HierarchicalPath("/fred/sucks");
        assertThat(path.remove(new HierarchicalPath("/fred/")), is(new HierarchicalPath("sucks")));
        assertThat(path.remove(new HierarchicalPath("/fred")), is(new HierarchicalPath("sucks")));
        assertThat(path.remove(new HierarchicalPath("/ooogabooga")), is(path));



    }


}
