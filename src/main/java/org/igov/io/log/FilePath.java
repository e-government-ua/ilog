package org.igov.io.log;

import java.io.File;
import java.util.*;

public class FilePath {
    public static class TreeInfo implements Iterable<File> {
        public List<File> files = new ArrayList<>();
        public List<File> dirs = new ArrayList<>();

        public Iterator<File> iterator() {
            return files.iterator();
        }

        void addAll(TreeInfo other) {
            files.addAll(other.files);
            dirs.addAll(other.dirs);
        }
    }

    public static TreeInfo scanDir(String start, String regex) {
        return recurseDirs(new File(start), regex);
    }

    private static TreeInfo recurseDirs(File startDir, String regex) {
        TreeInfo result = new TreeInfo();

        for (File item : startDir.listFiles()) {
            if (item.isDirectory()) {
                result.dirs.add(item);
                result.addAll(recurseDirs(item, regex));
            } else {
                if (item.getName().matches(regex)) {
                    result.files.add(item);
                }
            }
        }

        return result;
    }
}
