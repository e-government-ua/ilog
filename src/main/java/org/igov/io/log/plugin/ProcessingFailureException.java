package org.igov.io.log.plugin;

/**
 * @author  dgroup
 * @since   26.03.16
 */
class ProcessingFailureException extends RuntimeException {
    ProcessingFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}