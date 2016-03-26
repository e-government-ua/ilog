package org.igov.io.log;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("PMD")
//@RunWith(value = Parameterized.class)
public class UtilsTest {
    private String expected;
    private String input;

//    @Parameterized.Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                {   "log.debug(\"Got userId={}\", userId);",
                    "log.debug(\"Got \", userId);"},
                {   "log.debug(\"Some text userId={}, name={}\", userId, name);",
                    "log.debug(\"Some text \", userId, name);"},
                {   "log.debug(\"Life is good:) userId={}, name={}, someVar={}\", userId, name, someVar);",
                    "log.debug(\"Life is good:) \", userId, name, someVar);"},
                { "log.debug(\"Got userId={}, name={}, someVar={}, secondSomeVar={}\", userId, name, someVar, secondSomeVar);",
                        "log.debug(\"Got \", userId, name, someVar, secondSomeVar);"}

        });
    }

    public UtilsTest(String expected, String input) {
        this.expected = expected;
        this.input = input;
    }

//    @Test @Ignore
    public void testReplace() {
//        assertEquals(expected, CompilerUtil.replace(input));
    }
}
