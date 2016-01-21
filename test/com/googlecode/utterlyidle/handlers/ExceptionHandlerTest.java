package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.StringPrintStream;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.yadic.SimpleContainer;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.utterlyidle.Request.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;

public class ExceptionHandlerTest {

    private final StringPrintStream stdErr = new StringPrintStream();

    private ExceptionHandler underTest;

    @Before
    public void setUp() {
        underTest = new ExceptionHandler(exceptionThrowingHandler(),
                new ResponseHandlersFinder(new ResponseHandlers(), new SimpleContainer()));
        System.setErr(stdErr);
    }

    @Test
    public void exceptionsAreNotPrintedToSystemErrorWhenJvmDebuggingIsDisabled() throws Exception {
        assumeThat(jvmArguments(), not(containsDebuggingAgent()));

        underTest.handle(get("/"));

        assertThat(stdErr.toString(), not(containsString("aTestException")));
    }

    @Test
    public void exceptionsArePrintedToSystemErrorWhenJvmDebuggingIsEnabled() throws Exception {
        assumeThat(jvmArguments(), containsDebuggingAgent());

        underTest.handle(get("/"));

        assertThat(stdErr.toString(), containsString("aTestException"));
    }

    @Test
    public void exceptionsAreNotPrintedToSystemErrorWhenJvmDebuggingIsEnabledAndSuppressionArgumentIsPresent() throws Exception {
        assumeThat(jvmArguments(), containsDebuggingAgent());
        System.setProperty("com.googlecode.utterlyidle.exceptions.log", "false");

        underTest.handle(get("/"));

        assertThat(stdErr.toString(), not(containsString("aTestException")));
    }

    private Matcher<String> containsDebuggingAgent() {
        return containsString("-agentlib:jdwp");
    }

    private String jvmArguments() {
        return getRuntimeMXBean().getInputArguments().toString();
    }

    private HttpHandler exceptionThrowingHandler() {
        return request -> {
            throw new RuntimeException("aTestException");
        };
    }


}