package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author  dgroup
 * @since   26.03.16
 */
@Mojo(name = "replace-long-calls")
public class MavenJoinPoint extends AbstractMojo {

    private static final File HOME = new File(".");

    /** Directory which contains *.java files for recursive search */
    private File root;

    @Parameter(property = "encoding", defaultValue = "UTF-8")
    private String encoding;

    public MavenJoinPoint() {
        this(HOME, "UTF-8");
    }

    public MavenJoinPoint(File root, String encoding) {
        this.root = root;
        this.encoding = encoding;
    }


    @Override
    public void execute() {
        recursiveLoadOfSources().forEach(SrcFile::replaceLogCalls);
    }

    Collection<SrcFile> recursiveLoadOfSources() {
        notNull(root, "Root directory can't be a null");
        notBlank(encoding, "Encoding should be isn't blank");

        return root.isDirectory() ?
            listFiles(root, new String[]{"java"}, true)
                .stream ()
                .map    (file -> toSourceFile(file, encoding))
                .filter (SrcFile::hasIgovLogger)
                .collect(toList())
            : Collections.emptyList();
    }

    SrcFile toSourceFile(File file, String encoding) {
        try {
            return new SrcFile(file, JavaParser.parse(file, encoding), getLog());
        } catch (ParseException |IOException e) {
            throw new ProcessingFailureException("Unable to parse file: " + file.getPath(), e);
        }
    }
}