package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.IOUtils.write;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

class SourceUtil {

    private static final String LOG_CALL_REGEXP =
        ".*(LOG|LOGGER|log|logger)\\s*\\.\\s*(debug|info|error|trace|warn)\\s*\\(.*\".*\".*\\)\\;.*\n?";

    static final Pattern LOG_CALL_PATTERN = Pattern.compile(LOG_CALL_REGEXP);


    static JavaSrcFile toSourceFile(File file, String encoding) {
        try {
            return new JavaSrcFile(file, JavaParser.parse(file, encoding));
        } catch (ParseException|IOException e) {
            throw new ProcessingFailureException("Unable to parse file: " + file.getPath(), e);
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
        return LOG_CALL_PATTERN.matcher(code).find();
    }

    static void replaceLogCalls(JavaSrcFile src) throws IOException {
        if (annotationFoundInSourceCode(src.getCompUnit()))
            return;

        StringBuilder code = new StringBuilder();

        for(String line : Files.readAllLines(src.getFile().toPath()))
            if (isCallPresent(line))
                code.append(replaceCall(line));
            else
                code.append(line).append('\n');

        try(FileWriter file = new FileWriter(src.getFile())) {
            write(code, file);
        }
    }


    static boolean annotationFoundInSourceCode(CompilationUnit compUnit) {
        return compUnit.getTypes()
                .stream()
                .filter(annotationDeclaration -> annotationDeclaration.getAnnotations().toString().contains("@DoNotReplaceLogs"))
                .count() > 0;
    }


    static boolean isCallPresent(String line) {
        return LOG_CALL_PATTERN.matcher(line).find();
    }

    // fucking hell! TODO clean it or replace via regexp replace
    static String replaceCall(String code) {
        StringTokenizer tokenizer = new StringTokenizer(code, ",");
        String result = "";
        List<String> varList = new ArrayList<>();

        String temp = tokenizer.nextToken();
        result += (temp.substring(0, temp.length() - 1));

        if (tokenizer.countTokens() == 0) {
            return result + ";\n";
        }

        while (tokenizer.hasMoreTokens()) {
            varList.add(tokenizer.nextToken());
        }
        temp = varList.remove(varList.size() - 1);
        varList.add(temp.substring(0, temp.length() - 2));

        Iterator<String> iterator = varList.iterator();
        while (iterator.hasNext()) {
            result += iterator.next() + "={},";
        }
        result = result.substring(0, result.length() - 1) + "\",";

        iterator = varList.iterator();
        while (iterator.hasNext()) {
            result += iterator.next() + ",";
        }
        result = result.substring(0, result.length() - 1) + ");\n";

        return result;
    }
}