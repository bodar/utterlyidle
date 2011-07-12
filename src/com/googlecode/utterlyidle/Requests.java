package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.utterlyidle.io.HierarchicalPath;
import com.googlecode.utterlyidle.io.Url;

public class Requests {
    public static MemoryRequest request(String method, Url requestUri, HeaderParameters headers, byte[] input) {
        return new MemoryRequest(method, requestUri, headers, input);
    }

    public static Request request(String method, String path, QueryParameters query, HeaderParameters headers, byte[] input) {
        return request(method, Url.url(path + query.toString()), headers, input);
    }

    public static Callable1<Request, String> method() {
        return new Callable1<Request, String>() {
            public String call(Request request) throws Exception {
                return request.method();
            }
        };
    }

    public static Callable1<Request, QueryParameters> query() {
        return new Callable1<Request, QueryParameters>() {
            public QueryParameters call(Request request) throws Exception {
                return request.query();
            }
        };
    }

    public static Callable1<Request, HierarchicalPath> path() {
        return new Callable1<Request, HierarchicalPath>() {
            public HierarchicalPath call(Request request) throws Exception {
                return request.url().path();
            }
        };
    }
}
