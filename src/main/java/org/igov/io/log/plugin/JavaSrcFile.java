package org.igov.io.log.plugin;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ReferenceType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  dgroup
 * @since   26.03.16
 */
class JavaSrcFile {
    private File file;
    private CompilationUnit compUnit;

    JavaSrcFile(File file, CompilationUnit compilationUnit) {
        setFile(file);
        setCompUnit(compilationUnit);
    }

    File getFile() {
        return file;
    }
    final void setFile(File file) {
        this.file = file;
    }

    CompilationUnit getCompUnit() {
        return compUnit;
    }
    final void setCompUnit(CompilationUnit compUnit) {
        this.compUnit = compUnit;
    }


    public String toString() {
        return "Java source file. Path " + file.getPath();
    }



    boolean hasIgovLogger() {
        return loggerFoundInImportSection() && loggerFoundInBodySection();
    }


    /**
     * Check that igov logger is present in the import section
     **/
    boolean loggerFoundInImportSection() {
        return compUnit.getImports()
                .stream ()
                .filter (importDeclaration -> importDeclaration.toString().contains("org.igov.io.log.Logger"))
                .count  () > 0;
    }


    /**
     * Check that igov logger was defined as class member
     **/
    boolean loggerFoundInBodySection() {
        return compUnit.getTypes()
                .stream()
                .filter(body ->
                    body.toString().contains("Logger") &&
                    body.getMembers().stream().filter(member ->{
                        Node log = member.getChildrenNodes().iterator().next();
                        return "Logger".equals(log.toString()) && log instanceof ReferenceType;
                    })
                    .count() > 0
                ).count() > 0;
    }

    public List<BlockStmt> getBlockStatements() {
        List<BlockStmt> blocks = new ArrayList<>();
        for(TypeDeclaration type : getCompUnit().getTypes())
            for(BodyDeclaration body : type.getMembers())
                body.getChildrenNodes()
                    .stream ()
                    .filter (node -> node instanceof BlockStmt)
                    .forEach(node -> blocks.add((BlockStmt) node));
        return blocks;
    }
}