package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.io.HierarchicalPath;

import static com.googlecode.totallylazy.functions.Callables.first;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.where;

public class Requests {
    public static Request request(String method, Uri requestUri, HeaderParameters headers, Object input) {
        return MemoryRequest.memoryRequest(method, requestUri, headers, Entity.entity(input));
    }

    public static Request request(String method, String path, QueryParameters query, HeaderParameters headers, Object input) {
        return request(method, Uri.uri(path + query.toString()), headers, input);
    }

    public static Function1<Request, String> pathAsString() {
        return request -> request.uri().path();
    }

    public static Function1<Request, Uri> uri() {
        return Request::uri;
    }

    public static Function1<Request, String> method() {
        return Request::method;
    }

    public static Function1<Request, QueryParameters> query() {
        return Requests::query;
    }

    public static Function1<Request, String> queryParameter(final String name) {
        return request -> query(request).getValue(name);
    }

    public static LogicalPredicate<Request> hasQueryParameter(final String parameter) {
        return new LogicalPredicate<Request>() {
            @Override
            public boolean matches(Request request) {
                return query(request).contains(parameter);
            }
        };
    }

    public static Function1<Request, Accept> accept() {
        return Accept::accept;
    }

    public static QueryParameters query(Request request) {
        return QueryParameters.parse(request.uri().query());
    }

    public static Function1<Request, FormParameters> form() {
        return Requests::form;
    }

    public static FormParameters form(Request request) {
        String contentType = request.headers().getValue(HttpHeaders.CONTENT_TYPE);
        if (contentType != null && contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            return FormParameters.parse(request.entity());
        } else {
            return FormParameters.formParameters();
        }
    }

    public static CookieParameters cookies(Request request) {
        return CookieParameters.cookies(request);
    }

    public static Function1<Request, HierarchicalPath> path() {
        return request -> HierarchicalPath.hierarchicalPath(request.uri().path());
    }

    public static Function1<Request, Entity> input() {
        return Request::entity;
    }

    public static LogicalPredicate<Pair<Request, Response>> method(final String method) {
        return where(first(Request.class), where(Requests.method(), is(method)));
    }
}
