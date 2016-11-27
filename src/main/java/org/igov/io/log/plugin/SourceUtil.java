package org.igov.io.log.plugin;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.IOUtils.write;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

class SourceUtil {

    private static final Log LOG = new SystemStreamLog();
    private static final String LOG_CALL_REGEXP =
            ".*(LOG|LOGGER|log|logger)\\s*\\.\\s*(debug|info|error|trace|warn)\\s*\\(.*\".*\".*\\)\\;.*\n?";

    private static final Pattern LOG_CALL_PATTERN = Pattern.compile(LOG_CALL_REGEXP);
    private static final Pattern LOG_CALL_REPLACE_PATTERN = Pattern.compile("\",.*.\\);$");

    private static final String LOG_PACKAGE = "org.igov.io.log";


    private static JavaSrcFile toSourceFile(File file, String encoding) {
        try {
            return new JavaSrcFile(file, JavaParser.parse(file, encoding));
        } catch (ParseException | IOException e) {
            throw new ProcessingFailureException("Unable to parse file: " + file.getPath(), e);
        }
    }


    /**
     * @param root is a directory which contains *.java files for recursive search.
     *             The sources with package starting with `org.igov.io.log.plugin` will be ignored
     *
     * @return `pre-compiled` collection of all *.java files in `root` directory
     **/
    static Collection<JavaSrcFile> loadSources(File root, String encoding) {
        notNull(root, "Root directory can't be a null");
        notBlank(encoding, "Encoding should be isn't blank");

        Collection<File> originalSrcFiles = listFiles(root, new String[]{"java"}, true);
        LOG.debug("  Found "+originalSrcFiles.size() + " files");
        originalSrcFiles.forEach(file -> LOG.debug("   "+file));

        List<JavaSrcFile> parsedSrcFiles = originalSrcFiles.stream()
                .map(file -> toSourceFile(file, encoding))
                .collect(toList());
        LOG.debug("  Parsed "+parsedSrcFiles.size()+" files");

        return parsedSrcFiles.stream()
                .filter(SourceUtil::leaveClassIfHisPackageDoesNotFromPluginPackage)
                .collect(toList());
    }

    /**
     * @return true if class doesn't relate to igov log plugin classes
     **/
    @SuppressWarnings({"PMD.AvoidCatchingNPE", "PMD.AvoidCatchingGenericException"})
    private static boolean leaveClassIfHisPackageDoesNotFromPluginPackage(JavaSrcFile src) {
        try {
            boolean leave = startsWith(src.getCompUnit().getPackage().getName().toString(), LOG_PACKAGE);
            if (leave)
                LOG.debug("   skip " + src);
            return !leave;
        } catch (NullPointerException npe){
            LOG.warn("Unable to proceed "+src, npe);
            return false;
        }
    }


    static Collection<JavaSrcFile> findUsageOfIgovLogger(File root, String encoding) {
        return loadSources(root, encoding)
                .stream()
                .filter(JavaSrcFile::hasIgovLogger)
                .peek(file -> LOG.debug("  ready "+file))
                .collect(toList());
    }

    static boolean logCallPresent(BlockStmt code) {
        return logCallPresent(code.toString());
    }

    static boolean logCallPresent(String code) {
        return LOG_CALL_PATTERN.matcher(code).find();
    }

    static void replaceLogCalls(JavaSrcFile src) throws IOException {
        if (annotationFoundInSourceCode(src.getCompUnit()))
            return;

        StringBuilder code = new StringBuilder();

        for (String line : Files.readAllLines(src.getFile().toPath()))
            if (isCallPresent(line))
                code.append(replaceCall(line));
            else
                code.append(line).append('\n');

        try (FileWriter file = new FileWriter(src.getFile())) {
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

    static String replaceCall(String code) {
        Matcher matcher = LOG_CALL_REPLACE_PATTERN.matcher(code);
        if (matcher.find()) {
            String tmp = " " + matcher.group(0).substring(3).replaceAll("\\);$", "").replaceAll(",", "={},") + "={}\",";
            int pos = code.lastIndexOf("\",");
            return new StringBuffer(code).replace(pos, pos + 2, tmp.replace(" \"={},", "")).toString() + "\n";
        } else {
            return code;
        }
    }
}