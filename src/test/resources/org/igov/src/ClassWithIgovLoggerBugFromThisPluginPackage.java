package org.igov.io.log.plugin;

import org.apache.maven.plugin.AbstractMojo;

import org.igov.io.log.Logger;
import org.igov.io.log.LoggerImpl;

public class ClassWithIgovLoggerBugFromThisPluginPackage extends AbstractMojo {

    private final static Logger LOG = LoggerFactory.getLogger(ClassWithIgovLoggerBugFromThisPluginPackage.class);

    public void execute() {
        LOG.debug("It started {}", ":)");
    }
}
