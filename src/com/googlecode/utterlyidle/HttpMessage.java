package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.functions.Functions;
import com.googlecode.totallylazy.functions.Unary;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_TYPE;
import static com.googlecode.utterlyidle.Parameters.Builder.replace;
import static com.googlecode.utterlyidle.Rfc2616.HTTP_LINE_SEPARATOR;

public interface HttpMessage<T extends HttpMessage<T>> {
    default Option<String> header(String name) {
        return headers().valueOption(name);
    }

    default Sequence<String> headers(String name) {
        return headers().getValues(name);
    }

    default T header(String name, Object value) {
        return modify(cast(this), Builder.<T>header(replace(name, value)));
    }

    HeaderParameters headers();

    default T headers(HeaderParameters value) {
        return create(value, entity());
    }

    default Option<String> cookie(String name) {
        return cookies().valueOption(name);
    }

    default T cookie(String name, Object value) {
        return modify(cast(this), Builder.<T>cookie(replace(name, value)));
    }

    T cookie(Cookie cookie);

    CookieParameters cookies();

    T cookies(Iterable<? extends Pair<String, String>> parameters);

    Entity entity();

    default T entity(Entity value) {
        return create(headers(), value);
    }

    T create(HeaderParameters headers, Entity entity);

    default String version() {
        return "HTTP/1.1";
    }

    String startLine();

    static String toString(HttpMessage<?> message){
        return sequence(message.startLine(), message.headers(), message.entity()).toString(HTTP_LINE_SEPARATOR);
    }

    interface Builder {
        static <T extends HttpMessage<T>> Unary<T> header(String name, Object value) {
            return header(replace(name, value));
        }

        @SafeVarargs
        static <T extends HttpMessage<T>> Unary<T> header(Unary<Parameters<?>>... builders) {
            return request -> modify(request, header(modify(request.headers(), builders)));
        }

        static <T extends HttpMessage<T>> Unary<T> header(Iterable<? extends Pair<String, String>> parameters) {
            return message -> message.headers(headerParameters(parameters));
        }

        static <T extends HttpMessage<T>> Unary<T> cookie(Cookie cookie) {
            return message -> message.cookie(cookie);
        }

        static <T extends HttpMessage<T>> Unary<T> cookie(String name, Object value) {
            return cookie(replace(name, value));
        }

        @SafeVarargs
        static <T extends HttpMessage<T>> Unary<T> cookie(Unary<Parameters<?>>... builders) {
            return message -> modify(message, cookie(modify(message.cookies(), builders)));
        }

        static <T extends HttpMessage<T>> Unary<T> cookie(Iterable<? extends Pair<String,String>> parameters) {
            return message -> message.cookies(parameters);
        }

        static <T extends HttpMessage<T>> Unary<T> entity(Object value) {
            return message -> message.entity(Entity.entity(value));
        }

        static <T extends HttpMessage<T>> Unary<T> contentType(Object value) {
            return header(CONTENT_TYPE, value);
        }
    }
}