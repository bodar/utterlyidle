package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.regex.Matches;
import com.googlecode.totallylazy.regex.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.PathParameters.pairIntoParameters;

public class UriTemplate implements Extractor<String, PathParameters>, Matcher<String> {
    Regex pathParameters = regex("\\{([^\\}]+?)(?:\\:([^\\}]+))?\\}");
    private final String template;
    private final Matches matches;
    private final Sequence<String> names;
    private final Regex templateRegex;

    public UriTemplate(String template) {
        this.template = template;
        matches = pathParameters.matches(template + "{$:(/.*)?}");
        names = matches.map(new Callable1<MatchResult, String>() {
            public String call(MatchResult m) throws Exception {
                return m.group(1);
            }
        });
        templateRegex = regex(matches.replace(new Callable1<CharSequence, CharSequence>() {
            public CharSequence call(CharSequence notMatched) throws Exception {
                return Pattern.quote(notMatched.toString());
            }
        }, new Callable1<MatchResult, CharSequence>() {
            public CharSequence call(MatchResult matched) throws Exception {
                return matched.group(2) == null ? "([^/]+)" : "(" + matched.group(2) + ")";
            }
        }));
    }

    public boolean isMatch(final String uri) {
        return templateRegex.matches(uri).headOption().map(new Callable1<MatchResult, Boolean>() {
            public Boolean call(MatchResult matchResult) throws Exception {
                return matchResult.start() == 0 && matchResult.end() == uri.length();
            }
        }).getOrElse(false);
    }

    public PathParameters extract(String uri) {
        List<String> values = groupValues(templateRegex.matches(uri).head());
        return (PathParameters) names.zip(values).foldLeft(new PathParameters(), pairIntoParameters());
    }

    private List<String> groupValues(MatchResult matchResult) {
        List<String> result = new ArrayList<String>();
        for (int i = 1; i < matchResult.groupCount(); i++) {
            result.add(matchResult.group(i));
        }
        return result;
    }

    public String generate(final PathParameters parameters){
        return matches.replace(new Callable1<MatchResult, CharSequence>() {
            public CharSequence call(MatchResult matchResult) throws Exception {
                return parameters.getValue(matchResult.group(1));
            }
        });
    }

    @Override
    public String toString() {
        return template;
    }
}