package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.regex.Matches;
import com.googlecode.totallylazy.regex.Regex;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.io.Url;

import javax.ws.rs.core.HttpHeaders;
import java.util.Iterator;
import java.util.regex.MatchResult;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static com.googlecode.utterlyidle.io.Url.url;

public class ConvertExtensionToAcceptHeader implements RequestHandler {
    public static final Regex FILE_EXTENSION = regex("(\\.[^?/.]+?)(?:\\?.*)?$");
    private final Sequence<Pair<String, String>> replacements;
    private final RequestHandler decorated;

    public ConvertExtensionToAcceptHeader(Replacements replacements, RequestHandler decorated) {
        this.replacements = sequence(replacements);
        this.decorated = decorated;
    }

    public void handle(Request request, Response response) throws Exception {
        Matches fileExtension = FILE_EXTENSION.findMatches(request.url().toString());
        decorated.handle(
                replacements.fold(request, applyReplacement(fileExtension)),
                response);
    }

    private Callable2<? super Request, ? super Pair<String, String>, Request> applyReplacement(final Matches fileExtension) {
        return new Callable2<Request, Pair<String, String>, Request>() {
            public Request call(Request request, Pair<String, String> extensionAndReplacementMimeType) throws Exception {
                if (hasExtension(fileExtension, extensionAndReplacementMimeType.first())) {
                    request.headers().remove(HttpHeaders.ACCEPT);
                    request.headers().add(HttpHeaders.ACCEPT, extensionAndReplacementMimeType.second());
                    request.url(removeExtension(request.url(), fileExtension));
                }
                return request;
            }
        };
    }

    private Url removeExtension(Url originalUrl, Matches fileExtension) {
        MatchResult match = fileExtension.head();
        String original = originalUrl.toString();
        return url(original.substring(0, match.start(1)) + original.substring(match.start(1) + match.group(1).length()));
    }

    private static boolean hasExtension(Matches actual, String expected) {
        return !actual.isEmpty() && ("." + expected).equals(actual.head().group(1));
    }

    public static class Replacements implements Iterable<Pair<String, String>> {
        private final Sequence<Pair<String, String>> replacements;

        private Replacements(Pair<String, String>... replacements) {
            this.replacements = sequence(replacements);
        }

        public static Replacements replacements(Pair<String, String>... replacements) {
            return new Replacements(replacements);
        }

        public Iterator<Pair<String, String>> iterator() {
            return replacements.iterator();
        }
    }
}
