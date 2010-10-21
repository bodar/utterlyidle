package com.googlecode.utterlyidle;

public class ResponseBody {
    private final String mimeType;
    private final Object value;

    private ResponseBody(String mimeType, Object value) {
        this.mimeType = mimeType;
        this.value = value;
    }

    public String mimeType() {
        return mimeType;
    }

    public Object value() {
        return value;
    }

    public static ResponseBody responseBody(String mimeType, Object value) {
        return new ResponseBody(mimeType, value);
    }

    public static ResponseBody ignoreContent() {
        return new ResponseBody("", "");
    }
}
