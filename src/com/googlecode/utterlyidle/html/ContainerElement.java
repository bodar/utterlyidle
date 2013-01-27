package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Sequence;
import org.w3c.dom.Node;

import static com.googlecode.utterlyidle.html.Form.fromElement;

public abstract class ContainerElement extends AbstractElement {
    public ContainerElement(Node node) {
        super(node);
    }

    public Form form(String xpath) {
        return new Form(expectElement(xpath));
    }

    public Sequence<Form> forms() {
        return selectElements("//form").map(fromElement());
    }

    public Input input(String xpath) {
        return new Input(expectElement(xpath));
    }

    public TextArea textarea(String xpath) {
        return new TextArea(expectElement(xpath));
    }

    public Select select(String xpath) {
        return new Select(expectElement(xpath));
    }

    /**
     *
     * @param inputsExpression
     * For example: "input[@type='radio' and @name='my_radio']"
     *  @return
     */
    public Radio radio(String inputsExpression) {
        return new Radio(this, inputsExpression);
    }

    public Checkbox checkbox(String xpath) {
        return new Checkbox(expectElement(xpath));
    }

    public Link link(String xpath) {
        return new Link(expectElement(xpath));
    }

    public Table table(String xpath) {
        return new Table(expectElement(xpath));
    }

    public Table table(String xpath, Iterable<String> columnNames) {
        return new Table(expectElement(xpath), columnNames);
    }

    public Table table(String xpath, String headerColumnNameExpression) {
        return new Table(expectElement(xpath), headerColumnNameExpression);
    }

}
