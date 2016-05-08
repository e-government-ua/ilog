package org.igov.io.log;

import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.Validate.notBlank;

/**
 * @since 5/7/2016
 */
class SrcLine {
    private static final Pattern LOG_CALL_PATTERN = Pattern.compile(
        ".*(LOG|LOGGER|log|logger)\\s*\\.\\s*(debug|info|error|trace|warn)\\s*\\(.*\".*\".*\\)\\;.*\n?");

    private final String src;
    private final String logVariable;
    private final String category;
    private final String msg;
    private final List<String> args;

    SrcLine(String src) {
        notBlank("Source line can't be a blank");

        this.src    = src;
        logVariable = substringBefore(src, ".");
        category    = substringBetween(this.src, ".", "(");

        String slf4jArgs = substring(src, src.indexOf("\""), src.lastIndexOf(");"));
        msg  = parseMessage(slf4jArgs);
        args = parseArgs(slf4jArgs);
    }

    final String parseMessage(String slf4jArgs) {
        throw new UnsupportedOperationException();
    }

    final List<String> parseArgs(String slf4jArgs) {
        throw new UnsupportedOperationException();
    }




    String getCategory() {
        return category;
    }

    String getMessage() {
        return msg;
    }

    List<String> getArgs() {
        return args;
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
        throw new UnsupportedOperationException();
    }


    String replaceCall() {
        String newMsg = msg;
        return logVariable + "." + category + "(" + newMsg + "," + args + ");";
    }

    public String getOriginalLine() {
        return src;
    }
}
