package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

class CompilerUtil {


    static JavaSrcFile toSourceFile(File file, String encoding) {
        try {
            return new JavaSrcFile(file, JavaParser.parse(file, encoding));
        } catch (ParseException|IOException e) {
            throw new ProceccingFailureException("Unable to parse file: " + file.getPath(), e);
        }
    }


    /**
     * @param root is a directory which contains *.java files for recursive search
     * @return `pre-compiled` collection of all *.java files in `root` directory
     **/
    static Collection<JavaSrcFile> loadSources(File root, String encoding) {
        notNull(root, "Root directory can't be a null");
        notBlank(encoding, "Encoding should be isn't blank");

        return listFiles(root, new String[]{"java"}, true)
                .stream ()
                .map    (file -> toSourceFile(file, encoding))
                .collect(toList());
    }


    static Collection<JavaSrcFile> findUsageOfIgovLogger(File root, String encoding) {
        return loadSources(root, encoding)
                .stream ()
                .filter (JavaSrcFile::hasIgovLogger)
                .collect(toList());
    }

    static boolean logCallPresent(BlockStmt code){
        return logCallPresent(code.toString());
    }

    static boolean logCallPresent(String code){
        Pattern pattern = Pattern.compile(".*(LOG|LOGGER|log|logger)\\.(debug|info|error|trace|warn)\\(.*\\)\\;.*");
        Matcher matcher = pattern.matcher(code);
        return matcher.matches();
    }
}