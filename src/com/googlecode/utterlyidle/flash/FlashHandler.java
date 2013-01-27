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
import static com.googlecode.totallylazy.Strings.isBlank;
import static com.googlecode.utterlyidle.Requests.cookies;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.path;

public class FlashHandler implements HttpHandler {
	public static final String FLASH_COOKIE = "f";
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
	 * @param flash
	 * @param decorated
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

		if(!requestCookies.contains(FLASH_COOKIE) || isBlank(requestCookies.getValue(FLASH_COOKIE))) return;

		flash.merge(Model.persistent.parse(requestCookies.getValue(FLASH_COOKIE)));
	}

	private Response setFlashCookie(Request request, Response response) {
		if(shouldClearFlash(request,  response)){
			return clearCookie(response);
		}
		return modify(response).cookie(flashCookie()).build();
	}

	private boolean shouldClearFlash(Request request, Response response) {
		return clearFlashPredicate.matches(pair(request, response));
	}

	private Response clearCookie(Response response) {
		return modify(response).cookie(flashCookie("")).build();
	}

	private Cookie flashCookie() {
        String json = toJson(flash.state());
		return flashCookie(json);
	}

    private Cookie flashCookie(String json) {
        return new Cookie(FLASH_COOKIE, json, path(basePath.toString()));
    }
}
