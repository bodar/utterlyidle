package com.googlecode.utterlyidle;

public class Status {
    public static Status OK = status(200, "OK");
    public static Status NO_CONTENT = status(204, "No Content");
    
    public static Status SEE_OTHER = status(303, "See Other");

    public static Status BAD_REQUEST = status(400, "Bad Request");
    public static Status NOT_FOUND = status(404, "Not Found");
    public static Status METHOD_NOT_ALLOWED = status(405, "Method Not Allowed");
    public static Status NOT_ACCEPTABLE = status(406, "Not Acceptable");
    public static Status UNSUPPORTED_MEDIA_TYPE = status(415, "Unsupported Media Type");



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

    @Override
    public String toString() {
        return String.format("Status: %s %s", code, description);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Status && code == ((Status) other).code;
    }

    @Override
    public int hashCode() {
        return code;
    }
}
