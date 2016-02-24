package com.googlecode.utterlyidle.flash;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.json.Json;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.cookies.CookieAttribute;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.isBlank;

public class FlashHandler implements HttpHandler {
    public static final String FLASH_COOKIE = "f";
    private static final String EMPTY_FLASH_COOKIE_JSON = "{}";
    private final HttpHandler decorated;
    private final Flash flash;
    private final ClearFlashPredicate clearFlashPredicate;
    private final FlashCookieAttributes flashCookieAttributes;

    /**
     * Flash is request-scoped storage for values
     * that will only be present for the next request.
     * <p>
     * Our current use case is to display messages to the user after
     * a redirect.
     * <p>
     * By default, FlashHandler will clear the flash on each
     * successful HTML response. Override ClearFlashPredicate
     * to change this behaviour.
     * <p>
     * It is expected that this behaviour will be superseded by a
     * more fully-featured implementation in the future.
     */
    public FlashHandler(Flash flash, HttpHandler decorated, ClearFlashPredicate clearFlashPredicate, FlashCookieAttributes flashCookieAttributes) {
        this.flash = flash;
        this.decorated = decorated;
        this.clearFlashPredicate = clearFlashPredicate;
        this.flashCookieAttributes = flashCookieAttributes;
    }

    @Override
    public Response handle(Request request) throws Exception {
        setIncomingFlashValues(request, flash);
        return setFlashCookie(request, decorated.handle(request));
    }

    private static void setIncomingFlashValues(Request request, Flash flash) {
        CookieParameters requestCookies = request.cookies();

        if (!requestCookies.contains(FLASH_COOKIE) || isEmptyJson(requestCookies.getValue(FLASH_COOKIE)) || isBlank(requestCookies.getValue(FLASH_COOKIE)))
            return;

        flash.merge(Json.parseMap(requestCookies.getValue(FLASH_COOKIE)).value());
    }

    private Response setFlashCookie(Request request, Response response) {
        return set(request, response, shouldClearFlash(request, response) ? clearCookie() : flashCookie());
    }

    private Response set(final Request request, final Response response, final Cookie cookie) {
        if (cookieAlreadyHasSameValue(request, cookie) || (isEmptyJson(cookie.value()) && !request.cookies().contains(FLASH_COOKIE))) {
            return response;
        }
        return response.cookie(cookie);
    }

    private boolean shouldClearFlash(Request request, Response response) {
        return clearFlashPredicate.matches(pair(request, response));
    }

    private boolean cookieAlreadyHasSameValue(final Request request, final Cookie cookie) {
        return cookie.value().equals(request.cookies().getValue(FLASH_COOKIE));
    }

    private static boolean isEmptyJson(final String value) {
        return EMPTY_FLASH_COOKIE_JSON.equals(value);
    }

    private Cookie clearCookie() {
        return flashCookie(EMPTY_FLASH_COOKIE_JSON);
    }

    private Cookie flashCookie() {
        return flashCookie(Json.json(flash.state()));
    }

    private Cookie flashCookie(String json) {
        return new Cookie(FLASH_COOKIE, json, cookieAttributes());
    }

    private CookieAttribute[] cookieAttributes() {
        final Sequence<CookieAttribute> attributes = sequence(flashCookieAttributes);
        return attributes.toArray(new CookieAttribute[attributes.size()]);
    }
}
