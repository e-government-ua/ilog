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
public class MavenJoinPointTest {
    static final File TEST_SRC_ROOT = new File("src/test/resources/org/igov/src");

    @Test
    public void recursiveLoadOfSources() throws MojoExecutionException, MojoFailureException {
        assertThatDirectoryExists(TEST_SRC_ROOT);

        MavenJoinPoint mvnPlugin = new MavenJoinPoint(TEST_SRC_ROOT, "UTF-8");

        Collection<SrcFile> srcFiles = mvnPlugin.recursiveLoadOfSources();
        assertTrue(srcFiles.size() == 1, "At least 'ClassWithIgovLogger.java' contains logger");
    }



    @Test(  expectedExceptions              = LogReplacingFailedException.class,
            expectedExceptionsMessageRegExp = "Unable to parse file: .*")
    public void toSourceFile(){
        File srcDir = new File("src/test/resources/org/igov/io/log/MavenJoinPoint/toSourceFile");
        assertThatDirectoryExists(srcDir);
        new MavenJoinPoint(srcDir, "UTF-8")
            .recursiveLoadOfSources();
    }


    static void assertThatDirectoryExists(File dir){
        assertTrue(dir.exists(), "Directory " + dir + " doesn't exists");
        assertTrue(dir.isDirectory(), "File " + dir + " isn't a directory");
    }

    static void assertThatFileExists(File file) {
        assertTrue(file.exists(), "File not exists: "+ file.getPath());
        assertTrue(file.isFile(), "Entity isn't a file: "+ file.getPath());
    }
}