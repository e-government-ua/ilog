package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author  Serhiy Bogoslavsky
 * @since   04.04.16
 */

public class ReplacerTest {


    @Test
    public void annotationFoundInSourceCodeTest() {
        assertTrue (Replacer.annotationFoundInSourceCode(parse("src/test/resources/test/src/TestSourceWithAnno.java")));
        assertFalse(Replacer.annotationFoundInSourceCode(parse("src/test/resources/test/src/SourceWithoutAnno.java")));
    }

    CompilationUnit parse(String path){
        try {
            return JavaParser.parse(new File(path));
        } catch (IOException| ParseException e) {
            throw new ShitHappensException("Unable to parse a file: "+path, e);
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
