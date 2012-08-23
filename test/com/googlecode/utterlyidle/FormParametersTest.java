package com.googlecode.utterlyidle;

import static com.googlecode.utterlyidle.FormParameters.formParameters;

public class FormParametersTest extends ParametersContract<FormParameters> {
    @Override
    protected FormParameters parameters() {
        return formParameters();
    }
}
