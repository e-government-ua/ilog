package org.igov.io.log;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author  Serhiy Bogoslavsky
 * @since   04.04.16
 */

public class Replacer {
    static boolean replaceLogCalls(File file, Pattern pattern) throws ParseException, IOException {
        if (annotationFoundInSourceCode(JavaParser.parse(file))) {
            return false;
        } else {
            checkFile(file, pattern);
            return true;
        }
    }

    static boolean annotationFoundInSourceCode(CompilationUnit compUnit) {
        return compUnit.getTypes()
                .stream()
                .filter(annotationDeclaration -> annotationDeclaration.getAnnotations().toString().contains("@NotAllowedReplaceLog"))
                .count() > 0;
    }

    static void checkFile(File file, Pattern pattern) {
        String tempCode, totalCode = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((tempCode = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(tempCode);
                totalCode += matcher.find()? replaceLog(tempCode) : tempCode + "\n";
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(totalCode);
            fileWriter.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    static String replaceLog(String code) {
        StringTokenizer tokenizer = new StringTokenizer(code, ",");
        String result = "";
        List<String> varList = new ArrayList<>();

        String temp = tokenizer.nextToken();
        result += (temp.substring(0, temp.length() - 1));

        if (tokenizer.countTokens() == 0) {
            return result;
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
