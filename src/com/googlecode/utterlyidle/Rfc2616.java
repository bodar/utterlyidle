package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Predicate;

import static com.googlecode.totallylazy.Sequences.characters;
import static com.googlecode.totallylazy.Sequences.range;
import static java.util.Arrays.asList;

public class Rfc2616 {
    public static final String SEPARATORS = "()<>@,;:\\\"/[]?={} \t";
    public static final String CTLs = range(0,32).join(asList(127)).fold("", charactersToString());

    public static boolean isValidToken(String value) {
        if(value == null || value.length()==0)return false;
        return characters(value).forAll(isValidTokenCharacter());
    }

    public static Predicate<? super Character> isValidTokenCharacter() {
        return new Predicate<Character>() {
            public boolean matches(Character character) {
                return SEPARATORS.indexOf(character) < 0 && CTLs.indexOf(character) < 0;
            }
        };
    }

    private static Callable2<? super String,? super Number, String> charactersToString() {
        return new Callable2<String, Number, String>() {
            public String call(String s, Number number) throws Exception {
                return s + (char)number.intValue();
            }
        };
    }

    public static String toQuotedString(String value) {
        return String.format("\"%s\"", value.replace("\"", "\\\""));
    }
}
