package org.igov.io.log;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.collections.CollectionUtils.addAll;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.Validate.notBlank;

/**
 * @author  dgroup
 * @since   5/7/2016
 */
class SrcLine {
    private static final Pattern LOG_CALL_PATTERN = Pattern.compile(
        ".*(LOG|LOGGER|log|logger)\\s*\\.\\s*(debug|info|error|trace|warn)\\s*\\(.*\".*\".*\\)\\;.*\n?");

    private final Logger log = LoggerFactory.getLogger(SrcLine.class);

    final String src;
    private String category;
    private String msg;
    private List<String> args;

    SrcLine(String src) {
        notBlank("Source line can't be a blank");
        this.src = src;
    }



    String replaceCall() {
        log.debug("Replace started for line: `{}`", src);

        // Detect log variable and category
        String logVariable = substringBefore(src, ".");
        category = trim(substringBetween(src, ".", "("));

        // Detect log message and arguments
        List<String> slf4jArgs = parseSlf4jArgs(substring(src, src.indexOf('\"'), src.lastIndexOf(");")));
        msg  = isNotEmpty(slf4jArgs)? slf4jArgs.remove(0) : "";
        args = slf4jArgs;

        // Let's build a new message with log, category and message
        StringBuilder newMsg = new StringBuilder(logVariable)
                .append(".")
                .append(category)
                .append("(\"")
                .append(msg);

        for(Iterator<String> i = args.iterator(); i.hasNext();) {
            newMsg  .append(' ')
                    .append(i.next())
                    .append("={}");
            if (i.hasNext())
                newMsg.append(',');
        }

        // Remove brackets from args#toString (List)
        String args = this.args.toString();
        args = args.substring(1, args.length()-1);

        // Let's add the arguments to our new message
        newMsg  .append("\", ")
                .append(args)
                .append(");\n");

        log.debug("New line: `{}`", newMsg);
        return newMsg.toString();
    }

    List<String> parseSlf4jArgs(String src) {
        log.trace("Got `{}`", src);

        if (countMatches(src, "\"") > 2)
            throw new LogSyntaxCompilationFailedException("Too many quotes (\") in line: ["+src+"]." +
                    " Template is: `log.debug(\"Some `text`\", id, name);`");

        List<String> args = new LinkedList<>();

        // Find SLF4J message pattern
        int msgLastIndex = src.indexOf('\"', 1);
        args.add(src.substring(1, msgLastIndex));

        // Find SLF4J arguments for message pattern
        String slf4jArgs = substringAfter(src.substring(msgLastIndex), ",");
        addAll(args, deleteWhitespace(slf4jArgs).split(","));

        log.trace("Args: {}", args);
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



    boolean replaceRequired() {
        return !contains(src, "{}");
    }





    @Override
    public String toString() {
        return getOriginalLine();
    }

    String getOriginalLine() {
        return src;
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
}