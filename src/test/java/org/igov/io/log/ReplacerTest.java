package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author  Serhiy Bogoslavsky
 * @since   04.04.16
 */

public class ReplacerTest {
    @Test
    public void annotationFoundInSourceCodeTest() {
        try {
            assertEquals(Replacer.annotationFoundInSourceCode(JavaParser
                    .parse(new File("src/test/resources/test/src/TestSourceWithAnno.java"))), true);
        } catch (ParseException exc) {
            exc.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        try {
            assertEquals(Replacer.annotationFoundInSourceCode(JavaParser
                    .parse(new File("src/test/resources/test/src/SourceWithoutAnno.java"))), false);
        } catch (ParseException exc) {
            exc.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    @Test(dataProvider = "logs")
    public void replaceTest(String expected, String actual) {
        assertEquals(Replacer.replaceLog(actual), expected);
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
