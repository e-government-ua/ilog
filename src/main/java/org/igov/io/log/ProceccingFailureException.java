package org.igov.io.log;

/**
 * @author  dgroup
 * @since   26.03.16
 */
class ProceccingFailureException extends RuntimeException {

    ProceccingFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
