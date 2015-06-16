package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.UrlEncodedMessage;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.cookies.CookieBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.replace;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.blank;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.COOKIE;
import static com.googlecode.utterlyidle.QueryParameters.queryParameters;
import static com.googlecode.utterlyidle.Requests.request;
import static com.googlecode.utterlyidle.cookies.CookieCutter.parseRequestHeader;
import static java.lang.String.format;

public class RequestBuilder implements Callable<Request> {
    private String method;
    private Uri uri;
    private final List<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
    private Entity entity = Entity.empty();

    public RequestBuilder(String method, Uri uri) {
        this.method = method;
        this.uri = uri;
    }

    public RequestBuilder(String method, String uri) {
        this(method, Uri.uri(uri));
    }

    public static RequestBuilder modify(Request request) {
        return new RequestBuilder(request);
    }

    public RequestBuilder(Request request) {
        this(request.method(), request.uri());

        sequence(request.headers()).fold(this, new Callable2<RequestBuilder, Pair<String, String>, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, Pair<String, String> nameAndValue) throws Exception {
                requestBuilder.header(nameAndValue.first(), nameAndValue.second());
                return requestBuilder;
            }
        });
        this.entity = request.entity();
    }

    public RequestBuilder method(String value) {
        this.method = value;
        return this;
    }

    public RequestBuilder uri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public RequestBuilder accepting(String value) {
        return header(HttpHeaders.ACCEPT, value);
    }

    public RequestBuilder headers(Iterable<? extends Pair<String, ?>> newHeaders) {
        for (Pair<String, ?> pair : newHeaders) header(pair.first(), pair.second());
        return this;
    }

    public RequestBuilder header(String name, Object value) {
        if (value == null) {
            return this;
        }

        headers.add(pair(name, value.toString()));
        return this;
    }

    public RequestBuilder cookie(Cookie cookie) {
        cookie(cookie.name(), cookie.value());
        return this;
    }

    public RequestBuilder cookie(String name, String value) {
        headers.add(pair(COOKIE, Cookie.cookie(name, value).toString()));
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestBuilder query(String name, Object value) {
        if (value != null) {
            QueryParameters parse = QueryParameters.parse(uri.query());
            uri = uri.query(UrlEncodedMessage.toString(parse.add(name, value.toString())));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestBuilder removeQuery(String name) {
        QueryParameters parse = QueryParameters.parse(uri.query());
        uri = uri.query(UrlEncodedMessage.toString(parse.remove(name)));
        return this;
    }

    public RequestBuilder replaceQuery(String name, Object value) {
        return removeQuery(name).query(name, value);
    }

    public RequestBuilder forms(FormParameters formParameters) {
        String body = formParameters.toString();
        replaceHeader(HttpHeaders.CONTENT_TYPE, format("%s; charset=%s", MediaType.APPLICATION_FORM_URLENCODED, Entity.DEFAULT_CHARACTER_SET));
        entity = Entity.entity(body);
        return this;
    }

    public RequestBuilder form(String name, Object value) {
        if (value == null) {
            return this;
        }

        return forms(FormParameters.parse(entity).add(name, value.toString()));
    }

    public RequestBuilder entity(Object entity) {
        this.entity = Entity.entity(entity);
        return this;
    }

    public Request call() throws Exception {
        return build();
    }

    public Request build() {
        return request(method, uri, headerParameters(headers), entity);
    }

    public static RequestBuilder get(Uri uri) {
        return new RequestBuilder(HttpMethod.GET, uri);
    }

    public static RequestBuilder get(String path) {
        return new RequestBuilder(HttpMethod.GET, path);
    }

    public static RequestBuilder post(Uri uri) {
        return new RequestBuilder(HttpMethod.POST, uri);
    }

    public static RequestBuilder post(String path) {
        return new RequestBuilder(HttpMethod.POST, path);
    }

    public static RequestBuilder put(Uri uri) {
        return new RequestBuilder(HttpMethod.PUT, uri);
    }

    public static RequestBuilder put(String path) {
        return new RequestBuilder(HttpMethod.PUT, path);
    }

    public static RequestBuilder patch(Uri uri) {
        return new RequestBuilder(HttpMethod.PATCH, uri);
    }

    public static RequestBuilder patch(String path) {
        return new RequestBuilder(HttpMethod.PATCH, path);
    }

    public static RequestBuilder delete(Uri uri) {
        return new RequestBuilder(HttpMethod.DELETE, uri);
    }

    public static RequestBuilder delete(String path) {
        return new RequestBuilder(HttpMethod.DELETE, path);
    }

    public static RequestBuilder head(String path) {
        return new RequestBuilder(HttpMethod.HEAD, path);
    }

    public static RequestBuilder head(Uri uri) {
        return new RequestBuilder(HttpMethod.HEAD, uri);
    }

    public static RequestBuilder options(String path) {
        return new RequestBuilder(HttpMethod.OPTIONS, path);
    }

    public static RequestBuilder options(Uri uri) {
        return new RequestBuilder(HttpMethod.OPTIONS, uri);
    }

    public Uri uri() {
        return uri;
    }

    public RequestBuilder replaceHeaders(String name, Object value) {
        return replaceHeader(name, value);
    }

    public RequestBuilder replaceHeader(String name, Object value) {
        removeHeaders(name);
        headers.add(pair(name, value.toString()));
        return this;
    }

    public RequestBuilder removeHeaders(String name) {
        removeHeaders(headers, name);
        return this;
    }

    public static void removeHeaders(List<Pair<String, String>> headers, String name) {
        for (Pair<String, String> header : sequence(headers).filter(where(first(String.class), equalIgnoringCase(name))).realise()) {
            headers.remove(header);
        }
    }

    public RequestBuilder contentType(String contentType) {
        return header(HttpHeaders.CONTENT_TYPE, contentType);
    }

    public RequestBuilder replaceCookie(Cookie cookie) {
        return replaceCookie(cookie.name(), cookie.value());
    }

    public RequestBuilder replaceCookie(final String name, final String value) {
        return mapCookies(setCookie(name, value));
    }

    public RequestBuilder removeCookie(final String cookieName) {
        return mapCookies(filterOutCookie(cookieName));
    }

    private RequestBuilder mapCookies(Function<String, String> mapper) {
        final LogicalPredicate<Pair<String, String>> isCookieHeader = where(first(String.class), equalIgnoringCase(COOKIE));
        final Predicate<Pair<String, String>> hasNoValue = where(second(String.class), blank);
        Sequence<Pair<String, String>> newHeaders = sequence(headers).
                map(replace(isCookieHeader, Callables.<String, String, String>second(mapper))).
                filter(not(isCookieHeader.and(hasNoValue))).realise();
        headers.clear();
        headers.addAll(newHeaders.toList());
        return this;
    }

    private static Function<String, String> filterOutCookie(final String cookieName) {
        return new Function<String, String>() {
            @Override
            public String call(String cookieValue) throws Exception {
                return parseRequestHeader(cookieValue).filter(where(cookieName(), not(cookieName))).map(toCookieString()).toString("; ");
            }
        };
    }

    private static Function<String, String> setCookie(final String name, final String value) {
        return new Function<String, String>() {
            @Override
            public String call(String headerValue) throws Exception {
                return parseRequestHeader(headerValue)
                        .map(replace(where(cookieName(), equalIgnoringCase(name)), setValue(value)))
                        .map(toCookieString()).toString("; ");
            }
        };
    }

    private static Function<Cookie, String> toCookieString() {
        return new Function<Cookie, String>() {
            @Override
            public String call(Cookie cookie) throws Exception {
                return format("%s=%s", cookie.name(), Rfc2616.toQuotedString(cookie.value()));
            }
        };
    }

    public RequestBuilder copyFormParamsToQuery() {
        return sequence(queryParameters(FormParameters.parse(entity))).fold(this, addQuery());
    }

    private Callable2<RequestBuilder, Pair<String, String>, RequestBuilder> addQuery() {
        return new Callable2<RequestBuilder, Pair<String, String>, RequestBuilder>() {
            @Override
            public RequestBuilder call(RequestBuilder requestBuilder, Pair<String, String> pair) throws Exception {
                return requestBuilder.query(pair.first(), pair.second());
            }
        };
    }

    private static Callable1<Cookie, String> cookieName() {
        return new Callable1<Cookie, String>() {
            @Override
            public String call(Cookie cookie) throws Exception {
                return cookie.name();
            }
        };
    }

    private static Callable1<Cookie, Cookie> setValue(final String value) {
        return new Callable1<Cookie, Cookie>() {
            @Override
            public Cookie call(Cookie cookie) throws Exception {
                return CookieBuilder.modify(cookie).value(value).build();
            }
        };
    }


}