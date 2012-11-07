package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static com.googlecode.totallylazy.LazyException.lazyException;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Xml.contents;
import static com.googlecode.totallylazy.Xml.selectNode;
import static java.lang.String.format;

public class Form {
    public static final String DESCENDANT = "descendant::";
    private final Element form;

    public Form(Element form) {
        this.form = form;
    }

    public Request submit(String submitXpath) throws IllegalStateException {
        if (new Input(Xml.selectElement(form, submitXpath).get()).disabled()){
            throw new IllegalStateException(format("Attempt to invoke disabled input for [%s]", submitXpath));
        }
        return submitXpath(fieldExpressions().add(sanitise(submitXpath)));
    }

    public Request submit() throws IllegalStateException {
        return submitXpath(fieldExpressions());
    }

    public String action() {
        return attribute("@action");
    }

    public String method() {
        return attribute("@method");
    }

    private String attribute(String xpath) {
        return selectNode(form, xpath).map(contents()).getOrElse((String) null);
    }

    @Override
    public String toString() {
        try {
            return Xml.asString(form);
        } catch (Exception e) {
            throw lazyException(e);
        }
    }

    private Request submitXpath(Sequence<String> fieldExpressions) {
        String action = Xml.selectContents(form, "@action");
        String method = Xml.selectContents(form, "@method");

        Sequence<NameValue> inputs = nameValuePairs(fieldExpressions);
        return inputs.fold(new RequestBuilder(method, action),
                method.equalsIgnoreCase(HttpMethod.POST) ? addFormParams() : addQueryParams()).
                build();
    }

    private Sequence<String> fieldExpressions() {
        return sequence("input[not(@type='submit')]", "textarea", "select");
    }

    private String sanitise(String submitXpath) {
        return submitXpath.startsWith(DESCENDANT) ? submitXpath.substring(DESCENDANT.length()) : submitXpath;
    }

    private Sequence<NameValue> nameValuePairs(Sequence<String> xpath) {
        return Xml.selectElements(form, xpath.toString(DESCENDANT, "|"+DESCENDANT, "")).flatMap(toNameAndValue());
    }

    private Callable1<? super Element, Sequence<NameValue>> toNameAndValue() {
        return new Callable1<Element, Sequence<NameValue>>() {
            public Sequence<NameValue> call(Element element) throws Exception {
                String type = type(element);
                if (type.equals("select")) {
                    return Sequences.<NameValue>sequence(new Select(element));
                }
                if (type.equals("checkbox")) {
                    Checkbox checkbox = new Checkbox(element);
                    if (checkbox.checked()) {
                        return Sequences.<NameValue>sequence(checkbox);
                    }
                    return Sequences.empty();
                }
                if(type.equals("textarea")) {
                    return Sequences.<NameValue>sequence(new TextArea(element));
                }
                return Sequences.<NameValue>sequence(new Input(element));
            }
        };
    }

    private String type(Element element) {
        String tagName = element.getTagName();
        if (tagName.equals("input")) {
            return Xml.selectContents(element, "@type");
        }
        return tagName;
    }

    private Callable2<RequestBuilder, NameValue, RequestBuilder> addQueryParams() {
        return new Callable2<RequestBuilder, NameValue, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, NameValue nameValue) throws Exception {
                return requestBuilder.query(nameValue.name(), nameValue.value());
            }
        };
    }

    private Callable2<RequestBuilder, NameValue, RequestBuilder> addFormParams() {
        return new Callable2<RequestBuilder, NameValue, RequestBuilder>() {
            public RequestBuilder call(RequestBuilder requestBuilder, NameValue nameValue) throws Exception {
                return requestBuilder.form(nameValue.name(), nameValue.value());
            }
        };
    }

    public static Callable1<? super Element, ? extends Form> fromElement() {
        return new Callable1<Element, Form>() {
            @Override
            public Form call(Element element) throws Exception {
                return new Form(element);
            }
        };
    }
}