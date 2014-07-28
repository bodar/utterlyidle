package com.googlecode.utterlyidle.html;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ElementContentsMatcher extends TypeSafeMatcher<BlockLevelElement> {
    private final String expression;
    private final Matcher<? super String> matcher;

    public static ElementContentsMatcher elementContents(Matcher<? super String> matcher) {
        return elementContents(".", matcher);
    }
    public static ElementContentsMatcher elementContents(String expression, Matcher<? super String> matcher) {
        return new ElementContentsMatcher(expression, matcher);
    }

    public ElementContentsMatcher(String expression, Matcher<? super String> matcher) {
        this.expression = expression;
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(BlockLevelElement node) {
        return matcher.matches(node.contents(expression).trim());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has element contents at '");
        description.appendText(expression);
        description.appendText("' ");
        description.appendDescriptionOf(matcher);
    }
}
