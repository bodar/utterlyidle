package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.MemoryResponse;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SiteMeshResponseTest {
    @Test
    public void shouldNotDecorateIfContentTypeIsNull() throws Exception {
        SiteMeshResponse siteMeshResponse = new SiteMeshResponse(null, MemoryResponse.response(), null);
        assertThat(siteMeshResponse.shouldDecorate(), is(false));
    }
}
