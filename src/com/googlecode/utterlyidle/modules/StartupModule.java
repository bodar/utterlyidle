package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

public interface StartupModule extends Module{
     Container start(Container requestScope);
}
