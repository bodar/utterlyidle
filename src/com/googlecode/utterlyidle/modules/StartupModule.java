package com.googlecode.utterlyidle.modules;

import com.googlecode.yadic.Container;

@Deprecated // use ServicesModule -- will be removed after 650
public interface StartupModule extends Module{
     Container start(Container requestScope);
}