package org.igov.io.log;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * @author  Serhiy Bogoslavsky
 * @since   04.04.16
 */

public class ReplacerTest {

    @Test(dataProvider = "logs")
    public void replaceTest(String expected, String actual) {
        assertEquals(Replacer.replace(actual), expected);
    }


    @DataProvider(name = "logs")
    public Object[][] produce() {
        return new Object[][]{
            {"log.trace(\"Got  name={}, id={}\", name, id);\n", "log.trace(\"Got \", name, id);"},
            {"log.debug(\"Got  name={}, id={}\", name, id);\n", "log.debug(\"Got \", name, id);"},
            {"log.info (\"Got  name={}, id={}\", name, id);\n", "log.info (\"Got \", name, id);"},
            {"log.warn (\"Got  name={}, id={}\", name, id);\n", "log.warn (\"Got \", name, id);"},
            {"log.warn (\"Got  name={}\", name);\n", "log.warn (\"Got \", name);"}};
    }
}
