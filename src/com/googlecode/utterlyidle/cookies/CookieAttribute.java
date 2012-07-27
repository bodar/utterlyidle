package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.Rfc2616;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CookieAttribute {
    public static final SimpleDateFormat RFC822_DATE_FORMAT = new SimpleDateFormat("EE, dd-MMM-yyyy HH:mm:ss");

    public static final String COMMENT = "Comment";
    public static final String DOMAIN = "Domain";
    public static final String MAX_AGE = "Max-Age";
    public static final String PATH = "Path";
    public static final String SECURE = "Secure";
    public static final String EXPIRES = "Expires";

    private final String name;
    private final String value;

    public static CookieAttribute cookieAttribute(String name, String value) {
        return new CookieAttribute(name, value);
    }

    public CookieAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static CookieAttribute comment(String value) {
        return cookieAttribute(COMMENT, value);
    }

    public static CookieAttribute domain(String value) {
        return cookieAttribute(DOMAIN, value);
    }

    public static CookieAttribute maxAge(long seconds) {
        return cookieAttribute(MAX_AGE, String.valueOf(seconds));
    }

    public static CookieAttribute path(String value) {
        return cookieAttribute(PATH, value);
    }

    public static CookieAttribute secure() {
        return cookieAttribute(SECURE, "");
    }

    public static CookieAttribute expires(Date date) {
        return cookieAttribute(EXPIRES, RFC822_DATE_FORMAT.format(gmt(date)) + " GMT");
    }

    @Override
    public String toString() {
        return String.format("%s=%s", name, Rfc2616.toQuotedString(value));
    }

    private static Date gmt(Date date) {
        TimeZone tz = TimeZone.getDefault();
        Date ret = new Date(date.getTime() - tz.getRawOffset());

        // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
        if (tz.inDaylightTime(ret)) {
            Date dstDate = new Date(ret.getTime() - tz.getDSTSavings());

            // check to make sure we have not crossed back into standard time
            // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
            if (tz.inDaylightTime(dstDate)) {
                ret = dstDate;
            }
        }

        return ret;
    }
}
