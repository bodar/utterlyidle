package com.googlecode.utterlyidle.lazyrecords;

import com.googlecode.lazyrecords.sql.ReadOnlyConnection;
import com.googlecode.totallylazy.Closeables;
import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Request;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Predicates.in;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.utterlyidle.Requests.method;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.annotations.HttpMethod.HEAD;

public class ConnectionActivator implements Callable<Connection>, Closeable {
    private final DataSource dataSource;
    private final Option<Request> request;
    private Connection connection;

    private ConnectionActivator(DataSource dataSource, Option<Request> request) {
        this.dataSource = dataSource;
        this.request = request;
    }

    public static ConnectionActivator connectionActivator(DataSource dataSource) {
        return new ConnectionActivator(dataSource, Option.<Request>none());
    }

    public static ConnectionActivator connectionActivator(DataSource dataSource, Request request) {
        return new ConnectionActivator(dataSource, Option.option(request));
    }

    @Override
    public Connection call() throws Exception {
        return connection = connection();
    }

    @Override
    public void close() throws IOException {
        Closeables.close(connection);
    }

    private Connection connection() throws SQLException {
        return request.is(where(method(), in(GET, HEAD))) ? new ReadOnlyConnection(dataSource) : dataSource.getConnection();
    }
}