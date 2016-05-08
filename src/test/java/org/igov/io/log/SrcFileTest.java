package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static java.io.File.separator;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.IOUtils.write;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.igov.io.log.MavenJoinPointTest.TEST_SRC_ROOT;
import static org.igov.io.log.MavenJoinPointTest.assertThatFileExists;
import static org.testng.Assert.*;

/**
 * @author  dgroup
 * @since   5/7/2016
 */
public class SrcFileTest {

    @Test
    public void hasIgovLogger() {
        SrcFile srcFile = getTestResource("ClassWithIgovLogger.java");

        assertTrue(srcFile.loggerFoundInImportSection(), "igov logger wasn't found in `import` section");
        assertTrue(srcFile.loggerFoundInBodySection(), "igov logger wasn't found in `body` section");
        assertTrue(srcFile.notIgnored(),    "igov logger should not be ignored");
        assertTrue(srcFile.hasIgovLogger(), "igov logger wasn't found as a member of class`");
    }

    @Test
    public void classWithoutLoggerAsMemberOfClass() {
        SrcFile srcFile = getTestResource("ClassWithoutLoggerAsMember.java");

        assertTrue(srcFile.loggerFoundInImportSection(), "igov logger wasn't found in `import` section");
        assertFalse(srcFile.loggerFoundInBodySection(), "igov logger wasn't found in as a member in class");
    }


    @Test
    public void logCallPresent() throws IOException, ParseException {
        SrcFile srcFile = getTestResource("ClassWithIgovLogger.java");
        assertTrue(srcFile.hasIgovLogger(), "Logger should be present in that file");

        List<BlockStmt> methods = srcFile.getBlockStatements();
        assertEquals(methods.size(), 3, "There is 3 methods in the .java class");

        List<BlockStmt> methodsWithIgovLogger = methods.stream()
                .filter (SrcLine::isLogCallPresent)
                .collect(toList());

        assertEquals(methodsWithIgovLogger.size(), 1, "There is 1 method with igov log");
    }


    private SrcFile getTestResource(String name)  {
        File file = new File(TEST_SRC_ROOT.getAbsolutePath() + separator + name);
        assertThatFileExists(file);
        return parseSource(file);
    }

    private SrcFile parseSource(File file) {
        try {
            // Create a new source file and parse it
            return new SrcFile(file, JavaParser.parse(file));

        } catch (ParseException|IOException e) {
            throw new AssertionError("Unable to find resource: "+file, e);
        }
    }


    @Test
    public void lines(){
        File file = new File("src/test/resources/org/igov/io/log/SrcFile/lines/ClassWithLogger.java");
        assertThatFileExists(file);

        SrcFile src = parseSource(file);
        assertEquals(src.toString(),     "Java source file. Path "+file);
        assertTrue  (src.hasIgovLogger(),"Logger is missing");

        List<SrcLine> lines = src.lines()
            .stream()
            .filter(SrcLine::isLogCallPresent)
            .collect(toList());
        assertThat(lines, hasSize(2));
    }

    @Test
    public void replaceLogCalls() throws IOException {
        File file = new File("src/test/resources/org/igov/io/log/SrcFile/replaceLogCalls/ClassWithLogger.java");
        assertThatFileExists(file);

        // Check the original file
        String originalSource =
            "import org.igov.io.log.Logger;\n" +
            "\n" +
            "public class ClassWithLogger {\n" +
            "\n" +
            "    Logger log = new Logger();\n" +
            "\n" +
            "    public void sayHello(){\n" +
            "        String smile = \"smile\";\n" +
            "        log.debug(\"This is igov approach\", smile);\n" +
            "    }\n" +
            "}\n";
        assertEquals(readFileToString(file), originalSource, "Original source were modified by someone");

        // Parse source file
        SrcFile src = parseSource(file);
        assertTrue(src.hasIgovLogger(), "Logger is missing");

        // Replace igov log calls
        src.replaceLogCalls();

        // Check the result
        String expectedSource =
            "import org.igov.io.log.Logger;\n" +
            "\n" +
            "public class ClassWithLogger {\n" +
            "\n" +
            "    Logger log = new Logger();\n" +
            "\n" +
            "    public void sayHello(){\n" +
            "        String smile = \"smile\";\n" +
            "        log.debug(\"This is igov approach smile={}\", smile);\n" +
            "    }\n" +
            "}\n";
        assertEquals(readFileToString(file), expectedSource, "Files aren't equal");

        // Restore original file
        try(FileWriter out = new FileWriter(file)) {
            write(originalSource, out);
        }
    }
}