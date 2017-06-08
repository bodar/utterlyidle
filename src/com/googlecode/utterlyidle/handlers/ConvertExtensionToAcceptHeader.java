package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.functions.Function2;
import com.googlecode.totallylazy.functions.Functions;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.io.HierarchicalPath;

import java.util.Iterator;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.Parameters.Builder.remove;
import static com.googlecode.utterlyidle.Request.Builder.uri;
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

    private Function2<? super Response, ? super Pair<String, String>, Response> reapplyExtension() {
        return new Function2<Response, Pair<String, String>, Response>() {
            public Response call(Response response, Pair<String, String> extensionAndReplacementMimeType) throws Exception {
                if (response.headers().contains(LOCATION)) {
                    return response.
                            headers(remove(LOCATION)).
                            header(LOCATION, addExtension(response.headers().getValue(LOCATION), extensionAndReplacementMimeType.first()));
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
        return extensionAndReplacementMimeType -> {
            Option<String> actual = fileExtension(request);
            String expected = extensionAndReplacementMimeType.first();
            return !actual.isEmpty() && ("." + expected).equals(actual.get());
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
        return uri.path(hierarchicalPath(uri.path()).segments().init().append(fileWithoutExtension).toString("/"));
    }

    private Function2<? super Request, ? super Pair<String, String>, Request> applyReplacement() {
        return (request, extensionAndReplacementMimeType) -> Functions.modify(request,
                HttpMessage.Builder.header(HttpHeaders.ACCEPT, extensionAndReplacementMimeType.second()),
                uri(removeExtension(request.uri())));
    }

    public static class Replacements implements Iterable<Pair<String, String>> {
        private final Sequence<Pair<String, String>> replacements;

        @SafeVarargs
        private Replacements(Pair<String, String>... replacements) {
            this.replacements = sequence(replacements);
        }

        @SafeVarargs
        public static Replacements replacements(Pair<String, String>... replacements) {
            return new Replacements(replacements);
        }

        public Iterator<Pair<String, String>> iterator() {
            return replacements.iterator();
        }
    }
}
