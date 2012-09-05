package com.googlecode.utterlyidle;

import static com.googlecode.utterlyidle.PathParameters.pathParameters;

public class PathParametersTest extends ParametersContract<PathParameters> {
    @Override
    protected PathParameters parameters() {
        return pathParameters();
    }
}
