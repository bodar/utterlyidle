package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Xml;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import org.w3c.dom.Element;

import static java.lang.String.format;

public class Form {
    private final Element form;

    public Form(Element form) {
        this.form = form;
    }

    public Request submit(String submitXpath) throws IllegalStateException {
        if (new Input(Xml.selectElement(form, submitXpath).get()).disabled()){
            throw new IllegalStateException(format("Attempt to invoke disabled input for [%s]", submitXpath));
        }
        String action = Xml.selectContents(form, "@action");
        String method = Xml.selectContents(form, "@method");
        Sequence<NameValue> inputs = nameValuePairs("descendant::input[not(@type='submit')]|descendant::textarea|descendant::select|" + submitXpath );
        return inputs.fold(new RequestBuilder(method, action),
                method.equalsIgnoreCase(HttpMethod.POST) ? addFormParams() : addQueryParams()).
                build();
    }

    private Sequence<NameValue> nameValuePairs(String xpath) {
        return Xml.selectElements(form, xpath).flatMap(toNameAndValue());
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
}