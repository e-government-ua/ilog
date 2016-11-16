package org.igov.io.log;

/**
 * @author  dgroup
 * @since   26.03.16
 */
class LogReplacingFailedException extends RuntimeException {
    LogReplacingFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}