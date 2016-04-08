package test.src;

import org.igov.io.log.Logger;

@NotAllowedReplaceLog
public class TestSourceWithAnno {
    void someMethod() {
        LOG.info("(sContent={})", sContent);
    }
}