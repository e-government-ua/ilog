package org.igov.io.log;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Says "Hi" to the user.
 */
@Mojo(name = "sayhi")
public class GreetingMojo extends AbstractMojo
{
    public void execute()
    {
        getLog().info( "Hello, world." );
    }
}