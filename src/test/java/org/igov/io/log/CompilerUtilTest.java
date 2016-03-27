package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.io.File.separator;
import static java.util.stream.Collectors.toList;
import static org.igov.io.log.ReplaceLongCallsForSLF4jTest.TEST_SRC_ROOT;
import static org.testng.Assert.*;

/**
 * @author  dgroup
 * @since   26.03.16
 */
public class CompilerUtilTest {

    @Test
    public void loadSources(){
        Collection<JavaSrcFile> srcFiles = CompilerUtil.loadSources(TEST_SRC_ROOT, "UTF-8");
        assertEquals(srcFiles.size(), 3, "Some *.java file wasn't found");
    }

    @Test
    public void findUsageOfIgovLogger(){
        Collection<JavaSrcFile> srcFiles = CompilerUtil.findUsageOfIgovLogger(TEST_SRC_ROOT, "UTF-8");
        assertEquals(srcFiles.size(), 1, "At least 1 java file contains logger");
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



    private JavaSrcFile getTestResource(String name) throws ParseException, IOException {
        File file = new File(TEST_SRC_ROOT.getAbsolutePath() + separator + name);
        assertTrue(file.exists(), "File not exists: "+ file.getPath());
        assertTrue(file.isFile(), "Entity isn't a file: "+ file.getPath());

        // Create a new source file and parse it
        return new JavaSrcFile(file, JavaParser.parse(file));
    }

    @Test
    public void logCallPresent() throws IOException, ParseException {
        JavaSrcFile srcFile = getTestResource("ClassWithIgovLogger.java");
        assertTrue(srcFile.hasIgovLogger(), "Logger should be present in that file");

        List<BlockStmt> methods = srcFile.getBlockStatements();
        assertEquals(methods.size(), 3, "There is 3 methods in the .java class");

        List<BlockStmt> methodsWithIgovLogger = methods.stream()
            .filter (CompilerUtil::logCallPresent)
            .collect(toList());

        assertEquals(methodsWithIgovLogger.size(), 1, "There is 1 method with igov log");
    }
}
