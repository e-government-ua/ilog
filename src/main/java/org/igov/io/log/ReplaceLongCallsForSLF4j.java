package org.igov.io.log;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.*;
import java.util.Iterator;
import java.util.List;


@Mojo(name = "replace-long-calls")
public class ReplaceLongCallsForSLF4j extends AbstractMojo
{
    public void execute()
    {
        List<File> files = FilePath.scanDir(".", ".*\\.java").files;
        Iterator<File> iter = files.iterator();

        while (iter.hasNext()) {
            String file = iter.next().toString();
            getLog().info("Current file -> " + file);

            try {
                FileReader fileReader = new FileReader(file);
                String s;
                StringBuilder totalStr = new StringBuilder();

                try (BufferedReader br = new BufferedReader(fileReader)) {
                    while ((s = br.readLine()) != null) {
                        if (s.contains(".debug")) {
                            totalStr.append(Utils.replace(s) + "\n");
                        } else {
                            totalStr.append(s + "\n");
                        }
                    }

                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(totalStr.toString());
                    fileWriter.close();
                }
            } catch (IOException exc) {
                throw new RuntimeException("Generating file failed", exc);
            }
        }
    }
}