package com.googlecode.utterlyidle.flash;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;

public class FlashMessagesModule implements RequestScopedModule {
	@Override
	public Container addPerRequestObjects(Container container) throws Exception {
		return container.
				add(ClearFlashPredicate.class, ClearFlashOnSuccessfulNonInternalHtmlResponse.class).
				add(FlashCookieAttributes.class).
				add(Flash.class).
				decorate(HttpHandler.class, FlashHandler.class);
	}
}
