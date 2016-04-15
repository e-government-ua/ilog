package org.igov.io.log;

import com.github.javaparser.ast.CompilationUnit;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import static org.apache.commons.io.IOUtils.write;
import static org.igov.io.log.SourceUtil.LOG_CALL_PATTERN;

/**
 * @author  Serhiy Bogoslavsky
 * @since   04.04.16
 */

public class Replacer {

    void replaceLogCalls(JavaSrcFile src) throws IOException {
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

    private boolean isCallPresent(String line) {
        return LOG_CALL_PATTERN.matcher(line).find();
    }

    static boolean annotationFoundInSourceCode(CompilationUnit compUnit) {
        return compUnit.getTypes()
                .stream()
                .filter(annotationDeclaration -> annotationDeclaration.getAnnotations().toString().contains("@DoNotReplaceLogs"))
                .count() > 0;
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