package com.googlecode.utterlyidle.flash;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.cookies.CookieAttribute;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.path;

public class FlashCookieAttributes implements Iterable<CookieAttribute> {

    private final Sequence<CookieAttribute> cookieAttributes;

    public FlashCookieAttributes(BasePath basePath) {
        this(basePath, new CookieAttribute[0]);
    }

    private FlashCookieAttributes(BasePath basePath, CookieAttribute... cookieAttributes) {
        this.cookieAttributes = sequence(cookieAttributes)
                .append(path(basePath.toString()));
    }

    public static FlashCookieAttributes flashCookieAttributes(BasePath basePath, CookieAttribute... cookieAttributes) {
        return new FlashCookieAttributes(basePath, cookieAttributes);
    }

    @Override
    public Iterator<CookieAttribute> iterator() {
        return cookieAttributes.iterator();
    }

    @Override
    public void forEach(final Consumer<? super CookieAttribute> action) {
        cookieAttributes.forEach(action);
    }

    @Override
    public Spliterator<CookieAttribute> spliterator() {
        return cookieAttributes.spliterator();
    }
}
