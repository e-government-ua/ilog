package org.igov.io.log;

import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.substring;

/**
 * @since 5/7/2016
 */
public class SrcLine {
    private static final Pattern LOG_CALL_PATTERN = Pattern.compile(
        ".*(LOG|LOGGER|log|logger)\\s*\\.\\s*(debug|info|error|trace|warn)\\s*\\(.*\".*\".*\\)\\;.*\n?");

    private final String src;

    SrcLine(String src) {
        this.src = src;
    }


    String getCategory() {
        return null;
    }

    String getMessage() {
        return null;
    }

    List<String> getArgs() {
        return null;
    }

    boolean isLogCallPresent() {
        return isLogCallPresent(src);
    }

    static boolean isLogCallPresent(String line){
        return LOG_CALL_PATTERN.matcher(line).find();
    }

    static boolean isLogCallPresent(BlockStmt line){
        return isLogCallPresent(line.toString());
    }

    public boolean replaceRequired() {
        return false;
    }

    // fucking hell! TODO clean it or replace via regexp replace
    String replaceCall() {
        StringTokenizer tokenizer = new StringTokenizer(src, ",");
        String token = tokenizer.nextToken();
        String result = substring(token, 0, token.length() - 1);

        if (tokenizer.countTokens() == 0) {
            return result + ";\n";
        }

        List<String> varList = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            varList.add(tokenizer.nextToken());
        }
        token = varList.remove(varList.size() - 1);
        varList.add(substring(token, 0, token.length() - 2));

        result = writeVarList(varList.iterator(), result, "={},", "\",");
        result = writeVarList(varList.iterator(), result, ",", ");\n");

        return result;
    }

    private static String writeVarList(Iterator<String> iterator, String result, String insertStr, String endStr) {
        String code = result;
        while (iterator.hasNext()) {
            code += iterator.next() + insertStr;
        }
        return substring(code, 0, code.length() - 1) + endStr;
    }

    public String getLine() {
        return src;
    }
}
