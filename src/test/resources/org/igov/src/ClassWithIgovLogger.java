package org.igov.service.business.access;

import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.log.Logger;
import org.igov.io.log.LoggerImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class AccessDataServiceImpl implements AccessDataService {

    private static final String contentMock = "No content!!!";
    private final static Logger LOG = LoggerFactory.getLogger(AccessDataServiceImpl.class);

    @Autowired
    private IBytesDataStorage durableBytesDataStorage;

    @Override
    public String setAccessData(String sContent) {
        LOG.info("   sContent={} sContent={} sContent={} sContent={} sContent={}", sContent);
        LOG. info ("   sContent={} sContent={} sContent={} sContent={} sContent={}", sContent);
        LOG.trace ("   sContent={} sContent={} sContent={} sContent={} sContent={}", sContent);
        LOG.    debug("   sContent={} sContent={} sContent={} sContent={} sContent={}", sContent);
        LOGGER.warn("(   sContent={} sContent={} sContent={} sContent={} sContent={}", sContent);
        LOGGER .   error (   "   sContent={} sContent={} sContent={} sContent={} sContent={}", sContent);
        //String sKey=durableBytesDataStorage.saveData(Tool.contentStringToByte(sContent));
        //String sKey=durableBytesDataStorage.saveData(sContent.getBytes());
        String sKey = durableBytesDataStorage.saveData(Tool.aData(sContent));
        LOG.info("  sKey={} sKey={} sKey={} sKey={} sKey={} sKey={} sKey={}", sKey);
        //log.info("(sData(check)={}) getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={} getAccessData(sKey)={}", getAccessData(sKey));
        return sKey;
    }

    String defaultMethod(int param){
        return Integer.parseInt(param);
    }

    private String privateMethod(int param){
        return Integer.parseInt(param);
    }
}
