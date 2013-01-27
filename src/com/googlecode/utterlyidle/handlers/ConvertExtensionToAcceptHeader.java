package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.io.HierarchicalPath;

import java.util.Iterator;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
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
                    return ResponseBuilder.modify(response).
                            removeHeaders(LOCATION).
                            header(LOCATION, addExtension(response.headers().getValue(LOCATION), extensionAndReplacementMimeType.first())).
                            build();
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
        return fileExtension(request.uri());
    }

    public static Option<String> fileExtension(Uri url) {
        HierarchicalPath path = hierarchicalPath(url.path());
        if (path.segments().isEmpty()) return none();
        String file = path.file();
        return file.indexOf(".") < 0 ? none(String.class) : some(file.substring(file.lastIndexOf(".")));
    }

    private Uri removeExtension(Uri uri) {
        if (fileExtension(uri).isEmpty()) return uri;
        String file = hierarchicalPath(uri.path()).file();
        String fileWithoutExtension = file.substring(0, file.lastIndexOf("."));
        return uri.path(hierarchicalPath(uri.path()).segments().init().add(fileWithoutExtension).toString("/"));
    }

    private Callable2<? super Request, ? super Pair<String, String>, Request> applyReplacement() {
        return new Callable2<Request, Pair<String, String>, Request>() {
            public Request call(Request request, Pair<String, String> extensionAndReplacementMimeType) throws Exception {
                return modify(request).
                        replaceHeader(HttpHeaders.ACCEPT, extensionAndReplacementMimeType.second()).
                        uri(removeExtension(request.uri())).
                        build();
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
