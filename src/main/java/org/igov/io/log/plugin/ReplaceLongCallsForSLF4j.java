package org.igov.io.log.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.igov.io.log.plugin.SourceUtil.findUsageOfIgovLogger;
import static org.igov.io.log.plugin.SourceUtil.replaceLogCalls;

/**
 * @author  dgroup
 * @since   26.03.16
 */
@Mojo(name = "replace-long-calls")
public class ReplaceLongCallsForSLF4j extends AbstractMojo {

    private Collection<JavaSrcFile> srcFiles;

    @Parameter(property = "encoding", defaultValue = "UTF-8")
    private String encoding = "UTF-8";

    @Parameter(property = "sourcesPath", required = true)
    private String sourcesPath;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Started at " + LocalDateTime.now());
        getLog().debug("  Root directory `" + sourcesPath + "`");
        getLog().debug("  Encoding `" + encoding + "`");

        srcFiles = findUsageOfIgovLogger(getSourcesPath(), encoding);

        if (srcFiles.isEmpty()) {
            getLog().warn(" No source files for replace procedure");
            return;
        }

        for (JavaSrcFile file : srcFiles) {
            try {

                getLog().debug("  Processing: " + file.getFile());
                replaceLogCalls(file);

            } catch (IOException e) {
                getLog().error("Unable to process a file: "+file.getFile(), e);
            }
        }

        getLog().info("Done at " + LocalDateTime.now());
    }


    Collection<JavaSrcFile> getSrcFiles() {
        return srcFiles;
    }

    public final File getSourcesPath() throws MojoExecutionException {
        if (isBlank(sourcesPath))
            throw new MojoExecutionException("Required parameter `sourcesPath` wasn't specified");
        return new File(sourcesPath);
    }

    public final void setSourcesPath(String sourcesPath) {
        this.sourcesPath = sourcesPath;
    }


    public final void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}