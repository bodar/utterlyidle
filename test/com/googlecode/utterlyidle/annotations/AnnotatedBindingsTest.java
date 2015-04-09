package com.googlecode.utterlyidle.annotations;

import org.junit.Test;

import java.lang.reflect.Method;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.hidden;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnnotatedBindingsTest {
    @Test
    public void addingHiddenToClassMakesMethodHidden() {
        @Hidden
        class HiddenClass{
            Method get() {
                return new Enclosing(){}.method;
            }
        }

        assertTrue(hidden(new HiddenClass().get()));
    }

    @Test
    public void addingHiddenToMethodMakesMethodHidden() {
        class HiddenMethod {
            @Hidden
            Method hidden() {
                return new Enclosing(){}.method;
            }

            Method visible() {
                return new Enclosing(){}.method;
            }
        }

        assertTrue(hidden(new HiddenMethod().hidden()));
        assertFalse(hidden(new HiddenMethod().visible()));
    }
}

abstract class Enclosing {
    public final Method method = getClass().getEnclosingMethod();
}