package org.igov.io.log;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Collection;

import static org.testng.Assert.assertTrue;


/**
 * @author  dgroup
 * @since   26.03.16
 */
public class ReplaceLongCallsForSLF4jTest {
    static final File TEST_SRC_ROOT = new File("src/test/resources/org/igov/src");

    @Test
    public void replaceLongCalls() throws MojoExecutionException, MojoFailureException {
        assertTrue(TEST_SRC_ROOT.exists(), "Source root directory doesn't exists");
        assertTrue(TEST_SRC_ROOT.isDirectory(), "Source root file isn't a directory");

        ReplaceLongCallsForSLF4j mvnPlugin = new ReplaceLongCallsForSLF4j();
        mvnPlugin.setRoot(TEST_SRC_ROOT);
        mvnPlugin.setEncoding("UTF-8");
        mvnPlugin.execute();

        Collection<JavaSrcFile> srcFiles = mvnPlugin.getSrcFiles();
        assertTrue(srcFiles.size() == 1, "At least 'ClassWithIgovLogger.java' contains logger");
    }
}