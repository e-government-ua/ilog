package org.igov.io.log;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File;

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
     * Check that logger is present in the import section
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
        for(TypeDeclaration type : compUnit.getTypes())
            if (type.getMembers().stream().filter(mbr -> mbr.toString().contains("Logger LOG")).count() > 0)
                return true;
        return false;
    }
}
