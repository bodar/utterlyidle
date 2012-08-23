package com.googlecode.utterlyidle.modules;

import com.googlecode.utterlyidle.handlers.Auditors;
import com.googlecode.utterlyidle.handlers.PrintAuditor;

public class BasicAuditing implements AuditModule{
    @Override
    public Auditors addAuditors(Auditors auditors) throws Exception {
        return auditors.add(new PrintAuditor(System.out));
    }
}
