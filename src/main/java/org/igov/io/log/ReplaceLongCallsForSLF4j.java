package org.igov.io.log;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.igov.io.log.CompilerUtil.findUsageOfIgovLogger;


@Mojo(name = "replace-long-calls")
public class ReplaceLongCallsForSLF4j extends AbstractMojo {

    private static final File HOME = new File(".");
    private File root;
    private Collection<JavaSrcFile> srcFiles;

    @Parameter(property = "encoding", defaultValue = "UTF-8")
    private String encoding;


    public ReplaceLongCallsForSLF4j() {
        this(HOME);
    }

    // for testing purposes
    public ReplaceLongCallsForSLF4j(File root) {
        this.root = root;
        this.encoding = "UTF-8";
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        srcFiles = findUsageOfIgovLogger(root, encoding);
        if (srcFiles.isEmpty())
            return;

        for (JavaSrcFile file : srcFiles) {
            getLog().info("Processing > " + file);

            CompilationUnit cu = file.getCompUnit();
            getLog().info("CU > " + cu);

            List<TypeDeclaration> types = cu.getTypes();

            for(TypeDeclaration type : types) {
                getLog().info("Type > " + type);
                for(BodyDeclaration body : type.getMembers()) {
                    getLog().info("body > " + body);
                }
            }
        }
    }


    public Collection<JavaSrcFile> getSrcFiles() {
        return srcFiles;
    }
}