package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.handlers.Auditor;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.yadic.Container;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AuditTest {

    @Test
    public void recordsRequestAndResponse() throws Exception {

        RestApplication testApplication = new RestApplication();
        TestAuditor auditor = new TestAuditor();
        testApplication.add(in(auditor));

        Request request = get("").build();
        Response response = testApplication.handle(request);

        assertThat(auditor.receivedRequest, is(request));
        assertThat(auditor.receivedResponse, is(response));
    }

    private RequestScopedModule in(final TestAuditor auditor) {
        return new RequestScopedModule() {
            public Module addPerRequestObjects(Container container) {
                container.remove(Auditor.class);
                container.addInstance(Auditor.class, auditor);
                return this;
            }
        };
    }

    private class TestAuditor implements Auditor {

        public Request receivedRequest;
        public Response receivedResponse;

        public void audit(Pair<Request, Date> request, Pair<Response, Date> response) {
            receivedRequest = request.first();
            receivedResponse = response.first();
        }

    }
}
