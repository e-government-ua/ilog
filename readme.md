### How to build & run [![Java version](https://img.shields.io/badge/java-8+-brightgreen.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
```cmd
mvn clean pmd:check jacoco:prepare-agent package jacoco:check
mvn org.igov:log-plugin:replace-long-calls
```



### Development links
- [Maven plugin dev guide](https://maven.apache.org/guides/plugin/guide-java-plugin-development.html)
- [New plugin, habr](https://habrahabr.ru/post/205118/)
- [Simple plugin](http://www.avajava.com/tutorials/lessons/how-do-i-create-a-hello-world-goal-for-a-maven-plugin.html?page=1)
