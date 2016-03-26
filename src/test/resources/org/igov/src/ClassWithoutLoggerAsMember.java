package org.igov.service.business.access;

import org.igov.io.log.Logger;
import org.igov.io.log.LoggerImpl;

public class ClassWithoutLoggerAsMember {

    private String user = "Tom";

    @Override
    public boolean removeAccessData(String sKey) {
        LOG.info("(sKey={})", sKey);
        return true;
    }
}
