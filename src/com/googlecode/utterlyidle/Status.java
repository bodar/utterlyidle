package com.googlecode.utterlyidle;

public class Status {
    public static Status CONTINUE = status(100, "Continue");
    public static Status SWITCHING_PROTOCOLS = status(101, "Switching Protocols");

    public static Status OK = status(200, "OK");
    public static Status CREATED = status(201, "Created");
    public static Status ACCEPTED = status(202, "Accepted");
    public static Status NON_AUTHORITATIVE_INFORMATION = status(203, "Non-Authoritative Information");
    public static Status NO_CONTENT = status(204, "No Content");
    public static Status RESET_CONTENT = status(204, "Reset Content");
    public static Status PARTIAL_CONTENT = status(206, "Partial Content");

    public static Status MULTIPLE_CHOICES = status(300, "Multiple Choices");
    public static Status MOVED_PERMANENTLY = status(301, "Moved Permanently");
    public static Status FOUND = status(302, "Found");
    public static Status SEE_OTHER = status(303, "See Other");
    public static Status NOT_MODIFIED = status(304, "Not Modified");
    public static Status USE_PROXY = status(305, "Use Proxy");
    public static Status TEMPORARY_REDIRECT = status(307, "Temporary Redirect");

    public static Status BAD_REQUEST = status(400, "Bad Request");
    public static Status UNSATISFIABLE_PARAMETERS = BAD_REQUEST.description("Unsatisfiable Parameters");
    public static Status UNAUTHORIZED = status(401, "Unauthorized");
    public static Status PAYMENT_REQUIRED = status(402, "Payment Required");
    public static Status FORBIDDEN = status(403, "Forbidden");
    public static Status NOT_FOUND = status(404, "Not Found");
    public static Status METHOD_NOT_ALLOWED = status(405, "Method Not Allowed");
    public static Status NOT_ACCEPTABLE = status(406, "Not Acceptable");
    public static Status PROXY_AUTHENTICATION_REQUIRED = status(407, "Proxy Authentication Required");
    public static Status REQUEST_TIMEOUT = status(408, "Request Timeout");
    public static Status CONFLICT = status(409, "Conflict");
    public static Status GONE = status(410, "Gone");
    public static Status LENGTH_REQUIRED = status(411, "Length Required");
    public static Status PRECONDITION_FAILED = status(412, "Precondition Failed");
    public static Status REQUEST_ENTITY_TOO_LARGE = status(413, "Request Entity Too Large");
    public static Status REQUEST_URI_TOO_LONG = status(414, "Request-URI Too Long");
    public static Status UNSUPPORTED_MEDIA_TYPE = status(415, "Unsupported Media Type");
    public static Status REQUESTED_RANGE_NOT_SATISFIABLE = status(416, "Requested Range Not Satisfiable");
    public static Status EXPECTATION_FAILED = status(417, "Expectation Failed");

    public static Status INTERNAL_SERVER_ERROR = status(500, "Internal Server Error");
    public static Status NOT_IMPLEMENTED = status(501, "Not Implemented");
    public static Status BAD_GATEWAY = status(502, "Bad Gateway");
    public static Status SERVICE_UNAVAILABLE = status(503, "Service Unavailable");
    public static Status GATEWAY_TIMEOUT = status(504, "Gateway Timeout");
    public static Status HTTP_VERSION_NOT_SUPPORTED = status(505, "HTTP Version Not Supported");

    private final int code;
    private final String description;

    private Status(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Status status(int code, String description) {
        return new Status(code, description);
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
        return String.format("Status: %s %s", code, description);
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
}
