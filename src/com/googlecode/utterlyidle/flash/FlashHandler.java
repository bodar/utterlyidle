package com.googlecode.utterlyidle.flash;

import com.googlecode.funclate.Model;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.cookies.CookieParameters;

import static com.googlecode.funclate.json.Json.toJson;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.Requests.cookies;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.path;

public class FlashHandler implements HttpHandler {
	public static final String FLASH_COOKIE = "f";
	private static final String EMPTY_FLASH_COOKIE_JSON = "{}";
	private final HttpHandler decorated;
	private final Flash flash;
	private final ClearFlashPredicate clearFlashPredicate;
    private final BasePath basePath;

    /**
	 * Flash is request-scoped storage for values
	 * that will only be present for the next request.
     *
     * Our current use case is to display messages to the user after
     * a redirect.
	 *
	 * By default, FlashHandler will clear the flash on each
	 * successful HTML response. Override ClearFlashPredicate
     * to change this behaviour.
     *
     * It is expected that this behaviour will be superseded by a
     * more fully-featured implementation in the future.
     *
	 */
	public FlashHandler(Flash flash, HttpHandler decorated, ClearFlashPredicate clearFlashPredicate, BasePath basePath) {
		this.flash = flash;
		this.decorated = decorated;
		this.clearFlashPredicate = clearFlashPredicate;
        this.basePath = basePath;
    }

	@Override
	public Response handle(Request request) throws Exception {
		setIncomingFlashValues(request, flash);
		return setFlashCookie(request, decorated.handle(request));
	}

	private static void setIncomingFlashValues(Request request, Flash flash) {
		CookieParameters requestCookies = cookies(request);

		if(!requestCookies.contains(FLASH_COOKIE) || isEmptyJson(requestCookies.getValue(FLASH_COOKIE))) return;

		flash.merge(Model.persistent.parse(requestCookies.getValue(FLASH_COOKIE)));
	}

	private Response setFlashCookie(Request request, Response response) {
		return set(request, response, shouldClearFlash(request, response) ? clearCookie() : flashCookie());
	}

	private Response set(final Request request, final Response response, final Cookie cookie) {
		if (cookieAlreadyHasSameValue(request, cookie)  || (isEmptyJson(cookie.value()) && !cookies(request).contains(FLASH_COOKIE))) {
			return response;
		}
		return modify(response).cookie(cookie).build();
	}

	private boolean shouldClearFlash(Request request, Response response) {
		return clearFlashPredicate.matches(pair(request, response));
	}

	private boolean cookieAlreadyHasSameValue(final Request request, final Cookie cookie) {
		return cookie.value().equals(cookies(request).getValue(FLASH_COOKIE));
	}

	private static boolean isEmptyJson(final String value) {
		return EMPTY_FLASH_COOKIE_JSON.equals(value);
	}

	private Cookie clearCookie() {
		return flashCookie(EMPTY_FLASH_COOKIE_JSON);
	}

	private Cookie flashCookie() {
		return flashCookie(toJson(flash.state()));
	}

	private Cookie flashCookie(String json) {
        return new Cookie(FLASH_COOKIE, json, path(basePath.toString()));
    }
}
