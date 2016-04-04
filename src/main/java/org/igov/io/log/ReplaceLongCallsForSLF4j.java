package org.igov.io.log;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Collection;

import static org.igov.io.log.CompilerUtil.findUsageOfIgovLogger;


@Mojo(name = "replace-long-calls")
public class ReplaceLongCallsForSLF4j extends AbstractMojo {

    private static final File HOME = new File(".");
    private File root;
    private Collection<JavaSrcFile> srcFiles;

    @Parameter(property = "encoding", defaultValue = "UTF-8")
    private String encoding;

    public ReplaceLongCallsForSLF4j() {
        setRoot(HOME);
        setEncoding("UTF-8");
    }


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        srcFiles = findUsageOfIgovLogger(root, encoding);

        if (srcFiles.isEmpty())
            return;

        for(JavaSrcFile file : srcFiles) {
            getLog().info("Processing: " + file);

            file.getBlockStatements()
                .stream()
                .filter(CompilerUtil::logCallPresent)
                .forEach(System.out::println);
        }
    }




    Collection<JavaSrcFile> getSrcFiles() {
        return srcFiles;
    }

    final void setRoot(File root) {
        this.root = root;
    }

    final void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}