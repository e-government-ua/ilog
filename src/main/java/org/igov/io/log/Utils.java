package org.igov.io.log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.indexOf;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Utils {
    public static String replace(String str) {
        StringBuilder result = new StringBuilder();
        List<String> varList = new ArrayList<>();

        int endIndex = indexOf(str, ",");
        result.append(str.substring(0, endIndex - 1));
        str = substring(str, endIndex + 2);

        while (!isEmpty(str)) {
            endIndex = indexOf(str, ",");
            if (endIndex == -1) {
                // end of string
                varList.add(substring(str, 0, str.length() - 2));
                str = "";
            } else {
                varList.add(substring(str, 0, endIndex));
                str = str.substring(endIndex + 1);
            }
        }

        Iterator<String> iter = varList.iterator();
        while (iter.hasNext()) {
            result.append(iter.next() + "={},");
        }
        result.replace(0, result.length(), substring(result.toString(), 0, result.toString().length() - 1) + "\", ");

        iter = varList.iterator();
        while (iter.hasNext()) {
            result.append(iter.next() + ",");
        }
        result.replace(0, result.length(), substring(result.toString(), 0, result.toString().length() - 1) + ");");

        return result.toString();
    }
}
