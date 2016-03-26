package org.igov.io.log;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.testng.Assert.assertTrue;


/**
 * @author  dgroup
 * @since   26.03.16
 */
public class ReplaceLongCallsForSLF4jTest {
    static final File TEST_SRC_ROOT = new File("src/test/resources/org/igov/src");

    File ouputRoot = new File("src/test/resources/org/igov/src-translated");

    @BeforeTest
    public void setUp() throws IOException {
        FileUtils.cleanDirectory(ouputRoot);
    }

    @Test
    public void core() throws MojoExecutionException, MojoFailureException {
        assertTrue(TEST_SRC_ROOT.exists(), "Source root directory doesn't exists");
        assertTrue(TEST_SRC_ROOT.isDirectory(), "Source root file isn't a directory");

        ReplaceLongCallsForSLF4j mvnPlugin = new ReplaceLongCallsForSLF4j(TEST_SRC_ROOT);

        mvnPlugin.execute();

        Collection<JavaSrcFile> srcFiles = mvnPlugin.getSrcFiles();
        assertTrue(srcFiles.size() == 1, "At least 'AccessDataServiceImpl.java' contains logger");
    }
}