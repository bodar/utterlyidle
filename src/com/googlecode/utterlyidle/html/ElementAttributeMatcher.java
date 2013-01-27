package com.googlecode.utterlyidle.html;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ElementAttributeMatcher extends TypeSafeMatcher<ContainerElement> {
    private final String name;
    private final Matcher<? super String> matcher;

    public static ElementAttributeMatcher elementAttribute(String expression, Matcher<? super String> matcher) {
        return new ElementAttributeMatcher(expression, matcher);
    }

    public ElementAttributeMatcher(String name, Matcher<? super String> matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(ContainerElement node) {
        return matcher.matches(node.attribute(name));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an element where attribute '");
        description.appendText(name);
        description.appendText("' ");
        description.appendDescriptionOf(matcher);
    }
}
