package org.igov.io.log.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  Serhiy Bogoslavsky
 * @since   07.04.16
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DoNotReplaceLogs {}
