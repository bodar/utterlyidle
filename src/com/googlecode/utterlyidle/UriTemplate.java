package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.regex.Matches;
import com.googlecode.totallylazy.regex.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.PathParameters.pathParameters;

public class UriTemplate implements Extractor<String, PathParameters>, Predicate<String> {
    private static final Regex pathParameters = regex("\\{([^\\}]+?)(?:\\:([^\\}]+))?\\}");
    private final String template;
    private final Matches matches;
    private final Sequence<String> names;
    private final Regex templateRegex;

    private UriTemplate(String template) {
        this.template = trimSlashes(template);
        matches = pathParameters.findMatches(this.template + "{$:(/.*)?}");
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

    public static UriTemplate uriTemplate(String template) {
        return new UriTemplate(template);
    }

    private static final Pattern trim = Pattern.compile("^(/)?(.*?)(/)?$");
    public static String trimSlashes(String value) {
        return trim.matcher(value).replaceAll("$2");
    }

    public boolean matches(final String uri) {
        return templateRegex.matches(trimSlashes(uri));
    }

    public PathParameters extract(String uri) {
        List<String> values = groupValues(templateRegex.findMatches(UrlEncodedMessage.decode(uri)).head());
        return pathParameters(names.zip(values));
    }

    private List<String> groupValues(MatchResult matchResult) {
        List<String> result = new ArrayList<String>();
        for (int i = 1; i < matchResult.groupCount(); i++) {
            result.add(matchResult.group(i));
        }
        return result;
    }

    public String generate(final PathParameters parameters) {
        return matches.replace(new Callable1<MatchResult, CharSequence>() {
            public CharSequence call(MatchResult matchResult) throws Exception {
                String paramValue = parameters.getValue(matchResult.group(1));
                if(paramValue==null)return null;
                if(paramValue.contains("/")) return paramValue;
                return UrlEncodedMessage.encode(paramValue);
            }
        });
    }

    @Override
    public String toString() {
        return template;
    }

    @Override
    public int hashCode() {
        return template.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UriTemplate && template.equals(((UriTemplate) obj).template);
    }

    public int segments() {
        return template.split("\\/").length;
    }
}