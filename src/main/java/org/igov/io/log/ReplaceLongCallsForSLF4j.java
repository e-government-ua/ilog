package org.igov.io.log;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Says "Hi" to the user.
 */
@Mojo(name = "replace-long-calls")
public class ReplaceLongCallsForSLF4j extends AbstractMojo
{
    public void execute()
    {
        getLog().info( "It works." );
    }
}