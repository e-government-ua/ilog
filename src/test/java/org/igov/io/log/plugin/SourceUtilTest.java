package org.igov.io.log.plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.io.File.separator;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.igov.io.log.plugin.ReplaceLongCallsForSLF4jTest.TEST_SRC_ROOT;
import static org.igov.io.log.plugin.SourceUtil.annotationFoundInSourceCode;
import static org.igov.io.log.plugin.SourceUtil.replaceCall;
import static org.testng.Assert.*;

/**
 * @author dgroup
 * @since 26.03.16
 */
public class SourceUtilTest {

    @Test
    public void loadSources() {
        Collection<JavaSrcFile> srcFiles = SourceUtil.loadSources(TEST_SRC_ROOT, "UTF-8");
        assertThat("At least 5 source *.java are located in the test directory", srcFiles, hasSize(5));
    }

    @Test
    public void findUsageOfIgovLogger() {
        Collection<JavaSrcFile> srcFiles = SourceUtil.findUsageOfIgovLogger(TEST_SRC_ROOT, "UTF-8");
        assertThat("At least 1 java file contains logger", srcFiles, hasSize(1));
    }

    @Test
    public void hasIgovLogger() throws IOException, ParseException {
        JavaSrcFile srcFile = getTestResource("ClassWithIgovLogger.java");

        assertTrue(srcFile.loggerFoundInImportSection(), "igov logger wasn't found in `import` section");
        assertTrue(srcFile.loggerFoundInBodySection(), "igov logger wasn't found in as a member in class");
        assertTrue(srcFile.hasIgovLogger(), "igov logger wasn't found as a member of class`");
    }

    @Test
    public void classWithoutLoggerAsMemberOfClass() throws IOException, ParseException {
        JavaSrcFile srcFile = getTestResource("ClassWithoutLoggerAsMember.java");

        assertTrue(srcFile.loggerFoundInImportSection(), "igov logger wasn't found in `import` section");
        assertFalse(srcFile.loggerFoundInBodySection(), "igov logger wasn't found in as a member in class");
    }


    static JavaSrcFile getTestResource(String name) throws ParseException, IOException {
        File file = new File(TEST_SRC_ROOT.getAbsolutePath() + separator + name);
        assertTrue(file.exists(), "File not exists: " + file.getPath());
        assertTrue(file.isFile(), "Entity isn't a file: " + file.getPath());

        // Create a new source file and parse it
        return new JavaSrcFile(file, JavaParser.parse(file));
    }

    @Test
    public void logCallPresent() throws IOException, ParseException {
        JavaSrcFile srcFile = getTestResource("ClassWithIgovLogger.java");
        assertTrue(srcFile.hasIgovLogger(), "Logger should be present in that file");

        List<BlockStmt> methods = srcFile.getBlockStatements();
        assertThat("There are 3 methods in the .java class", methods, hasSize(3));

        List<BlockStmt> methodsWithIgovLogger = methods.stream()
                .filter(SourceUtil::logCallPresent)
                .collect(toList());

        assertThat("There is 1 method with igov log", methodsWithIgovLogger, hasSize(1));
    }

    @Test(dataProvider = "logs for call search")
    public void logCallPresent(boolean found, String code) {
        assertEquals(found, SourceUtil.logCallPresent(code));
    }

    @DataProvider(name = "logs for call search")
    public Object[][] produce() {
        return new Object[][]{
                {true, "public void containsLog() {\n" +
                        "  int id = 33358;\n" +
                        "  String name = \"someName\";\n" +
                        "  log.info(\"Got name={}, id={} \", name, id);\n" +
                        "}"},

                {false, "public void notContainsAnyLog() {\n" +
                        "  System.out.println(\"Sorry, but I don't contain any logger:)\");\n" +
                        "}"},

                {true, "public void withBigLogger() {\n" +
                        "  int id = 33358;\n" +
                        "  String name = \"someName\";\n" +
                        "  LOG.trace(\"Got name={}, id={} \", name, id);\n" +
                        "}"},

                {true, "public void withSmallLongLogger() {\n" +
                        "  int id = 33358;\n" +
                        "  String name = \"someName\";\n" +
                        "  logger.error(\"\\ncontext info one two three: {} {} {}\", " +
                        "new Object[] {\"1\", \"2\", \"3\"}," +
                        "new Exception(\"something went wrong\"));\n" +
                        "}"}};
    }

    @Test
    public void annotationFoundInSourceCodeTest() {
        assertTrue(annotationFoundInSourceCode(parse(TEST_SRC_ROOT + "/skip/TestSourceWithAnno.java")));
        assertFalse(annotationFoundInSourceCode(parse(TEST_SRC_ROOT + "/skip/SourceWithoutAnno.java")));
    }

    static CompilationUnit parse(String path) {
        try {
            return JavaParser.parse(new File(path));
        } catch (IOException | ParseException e) {
            throw new AssertionError("Unable to parse a file: " + path, e);
        }
    }

    @Test(dataProvider = "logs for replace")
    public void replaceTest(String expected, String actual) {
        assertEquals(replaceCall(actual), expected);
    }


    @DataProvider(name = "logs for replace")
    public Object[][] produceLogs() {
        return new Object[][]{
                {"log.trace(\"Got  name={}, id={}\", name, id);\n", "log.trace(\"Got \", name, id);"},
                {"log.debug(\"Got  name={}, id={}\", name, id);\n", "log.debug(\"Got \", name, id);"},
                {"log.info (\"Got  name={}, id={}\", name, id);\n", "log.info (\"Got \", name, id);"},
                {"log.warn (\"Got  name={}, id={}\", name, id);\n", "log.warn (\"Got \", name, id);"},
                {"log.warn (\"Got  name={}\", name);\n", "log.warn (\"Got \", name);"},

                {
                        "log.info (\"Got \"John smith\"  name={}, id={}\", name, id);\n",
                        "log.info (\"Got \"John smith\" \", name, id);"
                },
                {
                        "log.info (\"Got \"John smith\",  name={}, id={}\", name, id);\n",
                        "log.info (\"Got \"John smith\", \", name, id);"
                },
                {
                        "log.info (\"Got  name={}, 253={}\", name, 253);\n",
                        "log.info (\"Got \", name, 253);"
                },
                {
                        "log.warn (\"Got  new Date()={}, id={}\", new Date(), id);\n",
                        "log.warn (\"Got \", new Date(), id);"
                },

                {"log.warn (\"Got name={}, id={}\", name, id);\n", "log.warn (\"Got name={}\", name, id);"},
                {"log.warn (\"Got  name={}, id={}\", name, id);\n", "log.warn (\"Got  name={}, id={}\", name, id);"},
                {
                        "LOG.info(\"   sContent={}\", sContent);\n",
                        "LOG.info(\"   sContent={}\", sContent);\n"
                }
        };
    }

    @Test
    public void isCallPresent() {
        assertTrue(SourceUtil.isCallPresent("log.     warn(\"the id is {}\", id);"));
        assertTrue(SourceUtil.isCallPresent("LOG.     debug(\"the id is {}\", id);"));
        assertTrue(SourceUtil.isCallPresent("logger.  info(\"the id is {}\", id);"));
        assertTrue(SourceUtil.isCallPresent("LOGGER.  trace(\"the id is {}\", id);"));
        assertFalse(SourceUtil.isCallPresent("lg. warn(\"the id is {}\", id);"));
    }
}