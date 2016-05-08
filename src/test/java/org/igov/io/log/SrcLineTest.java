package org.igov.io.log;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.testng.Assert.*;

/**
 * @since 5/7/2016
 */
public class SrcLineTest {
    @Test
    public void core() {
        SrcLine line = new SrcLine("log.info(\"\", id, name);");

        assertEquals("info", line.getCategory(), "Category is wrong");
        assertEquals("", line.getMessage(), "Message is wrong");
        assertEquals(asList("id", "name"), line.getArgs(), "Arguments is wrong");
        assertTrue(line.isLogCallPresent());
    }


    @Test
    public void isCallPresent() {
        assertTrue(new SrcLine("log.     warn(\"the id is {}\", id);").isLogCallPresent());
        assertTrue(new SrcLine("LOG.     debug(\"the id is {}\", id);").isLogCallPresent());
        assertTrue(new SrcLine("logger.  info(\"the id is {}\", id);").isLogCallPresent());
        assertTrue(new SrcLine("LOGGER.  trace(\"the id is {}\", id);").isLogCallPresent());
        assertFalse(new SrcLine("lg. warn(\"the id is {}\", id);").isLogCallPresent());
    }

    @Test(dataProvider = "for isLogCallPresent")
    public void isLogCallPresent(boolean found, String code) {
        assertEquals(found, SrcLine.isLogCallPresent(code));
    }

    @DataProvider(name = "for isLogCallPresent")
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

    @Test(dataProvider = "forReplace")
    public void replaceTest(String expected, String actual) {
        SrcLine line = new SrcLine(actual);
        assertEquals(line.replaceCall(), expected);
    }


    @DataProvider(name = "logs for replace")
    public Object[][] forReplace() {
        return new Object[][]{
                {"log.trace(\"Got  name={}, id={}\", name, id);\n", "log.trace(\"Got \", name, id);"},
                {"log.debug(\"Got  name={}, id={}\", name, id);\n", "log.debug(\"Got \", name, id);"},
                {"log.info (\"Got  name={}, id={}\", name, id);\n", "log.info (\"Got \", name, id);"},
                {"log.warn (\"Got  name={}, id={}\", name, id);\n", "log.warn (\"Got \", name, id);"},
                {"log.warn (\"Got  name={}\", name);\n", "log.warn (\"Got \", name);"}};
    }

    @Test(dataProvider = "for replaceRequired")
    public void replaceRequired(boolean expected, String srcLine) {
        SrcLine line = new SrcLine(srcLine);
        assertEquals(line.replaceRequired(), expected);
    }

    @DataProvider(name = "for replaceRequired")
    public Object[][] forReplaceRequired() {
        return new Object[][]{
                {true,  "log.info(\"\", id, name);"},
                {false, "log.info(\"id={}, name={}\", id, name);"}};
    }

    @Test
    public void smokeSplit(){

        SrcLine line = new SrcLine("       log.info(\" ,\"2\", \"3\" \"4\", \", id, name, \"arg\")");

        System.out.println(line.replaceCall());
    }
}