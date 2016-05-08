### How to build & run 
[![Java version](https://img.shields.io/badge/java-8+-brightgreen.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html) [![Build Status](https://semaphoreci.com/api/v1/dgroup/ilog/branches/master/shields_badge.svg)](https://semaphoreci.com/dgroup/ilog) [![Coverage Status](https://coveralls.io/repos/github/e-government-ua/ilog/badge.svg?branch=master)](https://coveralls.io/github/e-government-ua/ilog?branch=master) [![PMD](https://img.shields.io/badge/PMD-OK-brightgreen.svg)](https://github.com/e-government-ua/ilog/blob/master/java-code-rules.xml) [![Codacy Badge](https://api.codacy.com/project/badge/grade/30241f2b19c34937961ad3a6abe7f39e)](https://www.codacy.com/app/dgroup/ilog) [![HitCount](https://hitt.herokuapp.com/e-government-ua/ilog.svg)](https://github.com/e-government-ua/ilog)
```cmd
mvn clean pmd:check jacoco:prepare-agent package jacoco:report jacoco:check coveralls:report
mvn org.igov:log-plugin:replace-long-calls
```

### Usage
To include replacing logs you can  execute the ilog by setting the plugin in the ```<build>``` section of your POM as shown below:
```xml
<project>
    ...
    <plugins>
        <plugin>
            <groupId>org.igov</groupId>
            <artifactId>log-plugin</artifactId>
            <version>1.0.1</version>
            <executions>
                <execution>
                    <phase>process-sources</phase>
                    <goals>
                        <goal>replace-long-calls</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
    ...
</project>
```



### Development links
- [Maven plugin dev guide](https://maven.apache.org/guides/plugin/guide-java-plugin-development.html)
- [New plugin, habr](https://habrahabr.ru/post/205118/)
- [Simple plugin](http://www.avajava.com/tutorials/lessons/how-do-i-create-a-hello-world-goal-for-a-maven-plugin.html?page=1)
