package com.googlecode.utterlyidle.cookies;

import com.googlecode.utterlyidle.Rfc2616;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.characters;
import static com.googlecode.utterlyidle.cookies.CookieName.cookieName;
import static org.junit.Assert.fail;

public class CookieNameTest {
    @Test
    public void shouldValidateAgainstRfc2616TokenSpecification() throws Exception {
        for (Character illegalCharacter : characters(Rfc2616.CTLs).join(characters(Rfc2616.SEPARATORS))) {
            try {
                cookieName("xxx" + illegalCharacter + "xxx");
                fail("Expected character \"" + illegalCharacter + "\" ASCII(" + (int) illegalCharacter + ") to produce an illegal cookie name");
            } catch (IllegalArgumentException e) {
            }
        }
    }

}
