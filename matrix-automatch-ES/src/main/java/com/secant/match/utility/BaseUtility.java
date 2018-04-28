package com.secant.match.utility;

import java.util.Properties;

public class BaseUtility {

    public static Properties prop;

//    public static int getCorePoolSize() {
//        return Integer.parseInt(prop.getProperty("dcm2json.threads.corepoolsize" , "1" ));
//    }
    
    public static String getESClusterName() {
        return prop.getProperty("es.cluster.name");
    }

    
//    public static String getDcmFileStoreLocation() {
//        return prop.getProperty("image.store.location");
//    }
//    
//    public static String getCleansingTags() {
//        return prop.getProperty("cleansing.fields");
//    }
//    
//    public static String getDcmImagePath() {
//        return prop.getProperty("dcm.path");
//    }
    public static String getIPAddress() {
        return prop.getProperty("ip.address");
    }
    public static String getIndexName() {
        return prop.getProperty("elastic.index.name");
    }
//    public static String getDicomPathTypeName() {
//        return prop.getProperty("elastic.dicompath.type.name");
//    }
}
