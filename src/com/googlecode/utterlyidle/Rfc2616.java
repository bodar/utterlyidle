package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;

import static com.googlecode.totallylazy.Sequences.characters;
import static com.googlecode.totallylazy.Sequences.range;
import static com.googlecode.totallylazy.numbers.Numbers.toCharacter;
import static java.util.Arrays.asList;

public class Rfc2616 {
    public static final String SEPARATORS = "()<>@,;:\\\"/[]?={} \t";
    public static final String CTLs = range(0,32).join(asList(127)).map(toCharacter()).toString("");

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

    public static String toQuotedString(String value) {
        return String.format("\"%s\"", value.replace("\"", "\\\""));
    }

    public static String toUnquotedString(String value) {
        return value.replaceFirst("^\"", "").replaceFirst("\"$", "");
    }
}
