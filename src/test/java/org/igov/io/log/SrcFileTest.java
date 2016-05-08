package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.io.File.separator;
import static java.util.stream.Collectors.toList;
import static org.igov.io.log.MavenJoinPointTest.TEST_SRC_ROOT;
import static org.testng.Assert.*;

/**
 * @since 5/7/2016
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
        assertTrue(file.exists(), "File not exists: "+ file.getPath());
        assertTrue(file.isFile(), "Entity isn't a file: "+ file.getPath());

        try {
            // Create a new source file and parse it
            return new SrcFile(file, JavaParser.parse(file));

        } catch (ParseException|IOException e) {
            throw new AssertionError("Unable to find resource: "+name, e);
        }
    }
}