package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.io.Url;

import java.util.Iterator;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.io.HierarchicalPath.hierarchicalPath;

public class ConvertExtensionToAcceptHeader implements HttpHandler {
    private final Sequence<Pair<String, String>> replacements;
    private final HttpHandler decorated;

    public ConvertExtensionToAcceptHeader(Replacements replacements, HttpHandler decorated) {
        this.replacements = sequence(replacements);
        this.decorated = decorated;
    }

    public Response handle(Request request) throws Exception {
        Sequence<Pair<String, String>> replacementsThatApply = replacements.filter(appliesTo(request)).realise();
        Response response = decorated.handle(replacementsThatApply.fold(request, applyReplacement()));
        return replacementsThatApply.fold(response, reapplyExtension());
    }

    private Callable2<? super Response, ? super Pair<String, String>, Response> reapplyExtension() {
        return new Callable2<Response, Pair<String, String>, Response>() {
            public Response call(Response response, Pair<String, String> extensionAndReplacementMimeType) throws Exception {
                if (response.headers().contains(LOCATION)) {
                    String originalHeader = response.headers().getValue(LOCATION);
                    response.headers().remove(LOCATION);
                    response.header(LOCATION, addExtension(originalHeader, extensionAndReplacementMimeType.first()));
                }
                return response;
            }

            private String addExtension(String url, final String extension) {
                int queryStringStart = url.indexOf("?");
                queryStringStart = queryStringStart >= 0 ? queryStringStart : url.length();
                return url.substring(0, queryStringStart) + "." + extension + url.substring(queryStringStart);
            }
        };
    }

    private Predicate<? super Pair<String, String>> appliesTo(final Request request) {
        return new Predicate<Pair<String, String>>() {
            public boolean matches(Pair<String, String> extensionAndReplacementMimeType) {
                Option<String> actual = fileExtension(request);
                String expected = extensionAndReplacementMimeType.first();
                return !actual.isEmpty() && ("." + expected).equals(actual.get());
            }
        };
    }

    public static Option<String> fileExtension(Request request) {
        return fileExtension(request.url());
    }

    public static Option<String> fileExtension(Url url) {
        String file = url.path().file();
        return file.indexOf(".") < 0 ? none(String.class) : some(file.substring(file.lastIndexOf(".")));
    }

    private Url removeExtension(Url url) {
        if(fileExtension(url).isEmpty()) return url;
        String file = url.path().file();
        String fileWithoutExtension = file.substring(0, file.lastIndexOf("."));
        return url.replacePath(hierarchicalPath(url.path().segments().reverse().drop(1).reverse().add(fileWithoutExtension).toString("/")));
    }

    private Callable2<? super Request, ? super Pair<String, String>, Request> applyReplacement() {
        return new Callable2<Request, Pair<String, String>, Request>() {
            public Request call(Request request, Pair<String, String> extensionAndReplacementMimeType) throws Exception {
                request.headers().remove(HttpHeaders.ACCEPT);
                request.headers().add(HttpHeaders.ACCEPT, extensionAndReplacementMimeType.second());
                request.url(removeExtension(request.url()));
                return request;
            }
        };
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
