package test.src;

import org.igov.io.log.DoNotReplaceTheLogs;
import org.igov.io.log.Logger;

@DoNotReplaceLogs
public class TestSourceWithAnno {
    Logger log = new Logger(TestSourceWithAnno.class);

    void someMethod() {
        LOG.info("(sContent={})", sContent);
    }
}