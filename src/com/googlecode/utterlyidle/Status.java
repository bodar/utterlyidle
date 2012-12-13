package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.collections.PersistentSet;
import com.googlecode.totallylazy.numbers.Numbers;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.googlecode.totallylazy.collections.PersistentSortedSet.constructors.sortedSet;
import static com.googlecode.totallylazy.numbers.Numbers.range;

public class Status {
    public static final PersistentSet<Integer> INFORMATIONAL = set(100, 199);
    public static final Status CONTINUE = status(100, "Continue");
    public static final Status SWITCHING_PROTOCOLS = status(101, "Switching Protocols");

    public static final PersistentSet<Integer> SUCCESSFUL = set(200, 299);
    public static final Status OK = status(200, "OK");
    public static final Status CREATED = status(201, "Created");
    public static final Status ACCEPTED = status(202, "Accepted");
    public static final Status NON_AUTHORITATIVE_INFORMATION = status(203, "Non-Authoritative Information");
    public static final Status NO_CONTENT = status(204, "No Content");
    public static final Status RESET_CONTENT = status(205, "Reset Content");
    public static final Status PARTIAL_CONTENT = status(206, "Partial Content");

    public static final PersistentSet<Integer> REDIRECTION = set(300, 399);
    public static final Status MULTIPLE_CHOICES = status(300, "Multiple Choices");
    public static final Status MOVED_PERMANENTLY = status(301, "Moved Permanently");
    public static final Status FOUND = status(302, "Found");
    public static final Status SEE_OTHER = status(303, "See Other");
    public static final Status NOT_MODIFIED = status(304, "Not Modified");
    public static final Status USE_PROXY = status(305, "Use Proxy");
    public static final Status TEMPORARY_REDIRECT = status(307, "Temporary Redirect");

    public static final PersistentSet<Integer> CLIENT_ERROR = set(400, 499);
    public static final Status BAD_REQUEST = status(400, "Bad Request");
    public static final Status UNSATISFIABLE_PARAMETERS = BAD_REQUEST.description("Unsatisfiable Parameters");
    public static final Status UNAUTHORIZED = status(401, "Unauthorized");
    public static final Status PAYMENT_REQUIRED = status(402, "Payment Required");
    public static final Status FORBIDDEN = status(403, "Forbidden");
    public static final Status NOT_FOUND = status(404, "Not Found");
    public static final Status METHOD_NOT_ALLOWED = status(405, "Method Not Allowed");
    public static final Status NOT_ACCEPTABLE = status(406, "Not Acceptable");
    public static final Status PROXY_AUTHENTICATION_REQUIRED = status(407, "Proxy Authentication Required");
    public static final Status REQUEST_TIMEOUT = status(408, "Request Timeout");
    public static final Status CONFLICT = status(409, "Conflict");
    public static final Status GONE = status(410, "Gone");
    public static final Status LENGTH_REQUIRED = status(411, "Length Required");
    public static final Status PRECONDITION_FAILED = status(412, "Precondition Failed");
    public static final Status REQUEST_ENTITY_TOO_LARGE = status(413, "Request Entity Too Large");
    public static final Status REQUEST_URI_TOO_LONG = status(414, "Request-URI Too Long");
    public static final Status UNSUPPORTED_MEDIA_TYPE = status(415, "Unsupported Media Type");
    public static final Status REQUESTED_RANGE_NOT_SATISFIABLE = status(416, "Requested Range Not Satisfiable");
    public static final Status EXPECTATION_FAILED = status(417, "Expectation Failed");

    public static final PersistentSet<Integer> SERVER_ERROR = set(500, 599);
    public static final Status INTERNAL_SERVER_ERROR = status(500, "Internal Server Error");
    public static final Status NOT_IMPLEMENTED = status(501, "Not Implemented");
    public static final Status BAD_GATEWAY = status(502, "Bad Gateway");
    public static final Status SERVICE_UNAVAILABLE = status(503, "Service Unavailable");
    public static final Status CONNECTION_REFUSED = SERVICE_UNAVAILABLE.description("Connection Refused");
    public static final Status GATEWAY_TIMEOUT = status(504, "Gateway Timeout");
    public static final Status CLIENT_TIMEOUT = GATEWAY_TIMEOUT.description("Client Timeout");
    public static final Status HTTP_VERSION_NOT_SUPPORTED = status(505, "HTTP Version Not Supported");

    private final int code;
    private final String description;

    private Status(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Status status(int code, String description) {
        return new Status(code, description);
    }

    public static Status status(final HttpURLConnection connection) throws IOException {
        return status(connection.getResponseCode(), connection.getResponseMessage());
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    public Status description(String description){
        return status(code, description);
    }

    @Override
    public String toString() {
        return String.format("%s %s", code, description);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Status)
        {
            final Status status = (Status) other;
            return code == status.code;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * code * description.hashCode();
    }

    public boolean isSuccessful() {
        return SUCCESSFUL.contains(code);
    }

    private static PersistentSet<Integer> set(int start, int end) {
        return sortedSet(range(start, end).map(Numbers.intValue));
    }
}
