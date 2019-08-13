package com.harry.demo.jco;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.util.HashMap;
import java.util.Properties;

/**
 * The custom destination data provider implements DestinationDataProvider and
 * provides an implementation for at least getDestinationProperties(String).
 * Whenever possible the implementation should support events and notify the JCo runtime
 * if a destination is being created, changed, or deleted. Otherwise JCo runtime
 * will check regularly if a cached destination configuration is still valid which incurs
 * a performance penalty.
 *
 * @author zhouhong
 * @version 1.0
 * @title: JcoDestinationDataProvider
 * @description: TODO
 * @date 2019/8/7 10:30
 */
public class JcoDestinationDataProvider implements DestinationDataProvider {

    private DestinationDataEventListener eL;
    private HashMap<String, Properties> secureDBStorage = new HashMap<String, Properties>();

    public Properties getDestinationProperties(String destinationName) {
        try {
            //read the destination from DB
            Properties p = secureDBStorage.get(destinationName);

            if (p != null) {
                //check if all is correct, for example
                if (p.isEmpty())
                    throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION, "destination configuration is incorrect", null);

                return p;
            }

            return null;
        } catch (RuntimeException re) {
            throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
        }
    }

    //An implementation supporting events has to retain the eventListener instance provided
    //by the JCo runtime. This listener instance shall be used to notify the JCo runtime
    //about all changes in destination configurations.
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
        this.eL = eventListener;
    }

    public boolean supportsEvents() {
        return true;
    }

    //implementation that saves the properties in a very secure way
    public void addDestinationProperties(String destName, Properties properties) {
        synchronized (secureDBStorage) {
            if (properties == null) {
                if (secureDBStorage.remove(destName) != null)
                    eL.deleted(destName);
            } else {
                secureDBStorage.put(destName, properties);
                eL.updated(destName); // create or updated
            }
        }
    }
}
