package com.googlecode.utterlyidle;

public class Status {
    public static Status CONTINUE = status(100, "Continue");
    public static Status SWITCHING_PROTOCOLS = status(101, "Switching Protocols");

    public static Status OK = status(200, "OK");
    public static Status NO_CONTENT = status(204, "No Content");

    public static Status SEE_OTHER = status(303, "See Other");

    public static Status BAD_REQUEST = status(400, "Bad Request");
    public static Status UNSATISFIABLE_PARAMETERS = BAD_REQUEST.description("Unsatisfiable Parameters");
    public static Status UNAUTHORIZED = status(401, "Unauthorized");
    public static Status PAYMENT_REQUIRED = status(402, "Payment Required");
    public static Status FORBIDDEN = status(403, "Forbidden");
    public static Status NOT_FOUND = status(404, "Not Found");
    public static Status METHOD_NOT_ALLOWED = status(405, "Method Not Allowed");
    public static Status NOT_ACCEPTABLE = status(406, "Not Acceptable");
    public static Status UNSUPPORTED_MEDIA_TYPE = status(415, "Unsupported Media Type");

    public static Status INTERNAL_SERVER_ERROR = status(500, "Internal Server Error");
    public static Status NOT_IMPLEMENTED = status(501, "Not Implemented");


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
