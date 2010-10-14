package com.googlecode.utterlyidle

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
        regex(matches.replace(new Callable1<CharSequence, CharSequence>() {
            public CharSequence call(CharSequence notMatched) throws Exception {
                return Pattern.quote(notMatched.toString());
            }
        }, new Callable1<MatchResult, CharSequence>() {
            public CharSequence call(MatchResult matched) throws Exception {
                return matched.group(2) == null ? "([^/]+)" : "(" + matched.group(2) + ")";
            }
        }));
    }

    public boolean isMatch(String uri) {
        return templateRegex.isMatch(uri);
    }

    public PathParameters extract(String uri) {
        List<String> values = groupValues(templateRegex.matches(uri).head());
        names.zip(values).foldLeft(new PathParameters(), new Callable2<PathParameters, Pair<String, String>, Object>() {
            public PathParameters call(PathParameters params, Pair<String, String> pair) throws Exception {
                return params.add(pair.first(), pair.second());
            }
        });
    }

    private List<String> groupValues(MatchResult matchResult) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < matchResult.groupCount(); i++) {
            result.add(matchResult.group(i));
        }
        return result;
    }

    public String generate(final PathParameters parameters)
    {
        matches.replace(new Callable1<MatchResult, CharSequence>() {
            public CharSequence call(MatchResult matchResult) throws Exception {
                return parameters.getValue(matchResult.group(1));
            }
        })
    }


    override public toString=template
}

