package com.googlecode.utterlyidle.html;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TableRowColumnMatcher extends TypeSafeMatcher<TableRow> {
    private final Matcher<? super TableCell> cellMatcher;
    private final String columnName;

    public static Matcher<TableRow> columnValue(final String columnName, final Matcher<? super TableCell> cellMatcher) {
        return new TableRowColumnMatcher(cellMatcher, columnName);
    }

    public TableRowColumnMatcher(Matcher<? super TableCell> cellMatcher, String columnName) {
        this.cellMatcher = cellMatcher;
        this.columnName = columnName;
    }

    @Override
    protected boolean matchesSafely(TableRow tableRow) {
        return cellMatcher.matches(tableRow.get(columnName));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("TableRow where column '");
        description.appendText(columnName);
        description.appendText("' ");
        description.appendDescriptionOf(cellMatcher);
    }
}
