package org.igov.io.log;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.io.IOUtils.write;

/**
 * @author  dgroup
 * @since   26.03.16
 */
class SrcFile {

    private final Logger log = LoggerFactory.getLogger(SrcFile.class);
    private final File file;
    private final CompilationUnit compUnit;

    SrcFile(File file, CompilationUnit compilationUnit) {
        this.file = file;
        this.compUnit = compilationUnit;
    }

    public String toString() {
        return "Java source file. Path " + file.getPath();
    }


    boolean hasIgovLogger() {
        return loggerFoundInImportSection() && loggerFoundInBodySection() && notIgnored();
    }

    /**
     * Check that igov logger is present in the import section
     **/
    boolean loggerFoundInImportSection() {
        return logged(
            compUnit.getImports()
                .stream ()
                .filter (importDeclaration -> importDeclaration.toString().contains("org.igov.io.log.Logger"))
                .count  () > 0,
            "It was {} in import section", "found", "not found");
    }

    /**
     * Check that igov logger was defined as class member
     **/
    boolean loggerFoundInBodySection() {
        return logged(
            compUnit.getTypes()
                .stream()
                .filter(body ->
                    body.toString().contains("Logger") &&
                    body.getMembers().stream().filter(member ->{
                        Node log = member.getChildrenNodes().iterator().next();
                        return "Logger".equals(log.toString()) && log instanceof ReferenceType;
                    })
                    .count() > 0
                ).count() > 0,
            "It was {} in body section", "found", "not found");
    }

    boolean notIgnored() {
        return logged(!(
            compUnit.getTypes()
                .stream()
                .filter(annotationDeclaration -> annotationDeclaration.getAnnotations().toString().contains("@DoNotReplaceLogs"))
                .count() > 0),
            "Ignored: {}", "no", "yes");
    }

    private boolean logged(boolean status, String msg, String ...args) {
        log.trace(msg, status? args[0]: args[1]);
        return status;
    }


    List<BlockStmt> getBlockStatements() {
        List<BlockStmt> blocks = new ArrayList<>();
        for(TypeDeclaration type : compUnit.getTypes())
            for(BodyDeclaration body : type.getMembers())
                body.getChildrenNodes()
                    .stream ()
                    .filter (node -> node instanceof BlockStmt)
                    .forEach(node -> blocks.add((BlockStmt) node));
        return blocks;
    }


    void replaceLogCalls() {
        try {
            log.info("Processing: " + file);
            StringBuilder code = new StringBuilder();

            for(SrcLine line : lines())
                if (line.isLogCallPresent() && line.replaceRequired())
                    code.append(line.replaceCall());
                else
                    code.append(line.getOriginalLine()).append('\n');

            try(FileWriter file = new FileWriter(this.file)) {
                write(code, file);
            }

        } catch (IOException e) {
            log.error("Unable to process a file: "+file, e);
        }
    }

    List<SrcLine> lines() {
        try {
            return Files.readAllLines( file.toPath() )
                    .stream ()
                    .map    (SrcLine::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ProcessingFailureException("Unable to parse file: "+ file, e);
        }
    }
}