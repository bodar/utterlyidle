package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Either;

/**
 * Created by IntelliJ IDEA.
 * User: dan
 * Date: 20/12/10
 * Time: 07:14
 * To change this template use File | Settings | File Templates.
 */
public interface ActivatorFinder {
    Either<MatchFailure, HttpMethodActivator> findActivator(Request request);
}
