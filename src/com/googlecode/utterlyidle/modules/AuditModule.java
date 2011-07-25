package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.handlers.Auditors;

public interface AuditModule extends Module{
    Auditors addAuditors(Auditors auditors) throws Exception;
}
