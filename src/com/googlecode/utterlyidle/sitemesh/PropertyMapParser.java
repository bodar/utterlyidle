package com.googlecode.utterlyidle.sitemesh;

import org.sitemesh.content.Content;
import org.sitemesh.content.ContentProcessor;
import org.sitemesh.content.ContentProperty;
import org.sitemesh.content.tagrules.TagBasedContentProcessor;
import org.sitemesh.content.tagrules.html.CoreHtmlTagRuleBundle;
import org.sitemesh.content.tagrules.html.DivExtractingTagRuleBundle;

import java.io.IOException;
import java.nio.CharBuffer;

public class PropertyMapParser {
    public PropertyMap parse(CharBuffer buffer) throws IOException {
        ContentProcessor contentProcessor = new TagBasedContentProcessor(new CoreHtmlTagRuleBundle(), new DivExtractingTagRuleBundle());
        Content content = contentProcessor.build(buffer, null);
        ContentProperty extractedProperties = content.getExtractedProperties();
        return new PropertyMap(extractedProperties);
    }

    public PropertyMap parse(String value) throws IOException {
        CharBuffer buffer = CharBuffer.wrap(value);
        return parse(buffer);
    }
}
