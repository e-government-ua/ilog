package org.igov.io.log;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.igov.io.log.SourceUtil.findUsageOfIgovLogger;
import static org.igov.io.log.SourceUtil.replaceLogCalls;

/**
 * @author  dgroup
 * @since   26.03.16
 */
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
    public void execute() {
        srcFiles = findUsageOfIgovLogger(root, encoding);

        if (srcFiles.isEmpty())
            return;

        for (JavaSrcFile file : srcFiles) {
            try {

                getLog().info("Processing: " + file.getFile());
                replaceLogCalls(file);

            } catch (IOException e) {
                getLog().error("Unable to process a file: "+file.getFile(), e);
            }
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