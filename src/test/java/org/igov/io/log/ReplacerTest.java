package org.igov.io.log;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * @author  Serhiy Bogoslavsky
 * @since   04.04.16
 */

public class ReplacerTest {
    @Test
    public void replaceTest() {
        String expected = "log.info(\"Got  name={}, id={}\", name, id);\n";
        String actual = "log.info(\"Got \", name, id);";
        assertEquals(Replacer.replace(actual), expected);
    }
}
