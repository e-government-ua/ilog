package org.igov.io.log;


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

    static void replaceLogCalls(File file, Pattern pattern) {
        try  {
            FileReader fileReader = new FileReader(file);
            String temp, total = "";

            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((temp = bufferedReader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(temp);
                    total += matcher.find()? replace(temp) : temp + "\n";
                }

                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(total);
                fileWriter.close();
            }
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }

    static String replace(String code) {
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

















