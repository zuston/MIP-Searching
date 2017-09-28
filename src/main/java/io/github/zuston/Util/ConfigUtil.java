package io.github.zuston.Util;

import io.github.zuston.Helper.PropertyHelper;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created by zuston on 17/5/10.
 */

public class ConfigUtil {
    public static HashMap<String,String> getDbConfig() {
        String filename = "mongo.properties";
        Properties prop = PropertyHelper.loadProperty(filename);
        String port = PropertyHelper.getString(prop,"port");
        String dbName = PropertyHelper.getString(prop,"dbName");
        String host = PropertyHelper.getString(prop,"host");
        String username = PropertyHelper.getString(prop,"username");
        String pwd = PropertyHelper.getProperty(prop,"pwd");

        HashMap<String,String> container = new HashMap<String, String>();
        container.put("host",host);
        container.put("port",port);
        container.put("dbName",dbName);
        container.put("username",username);
        container.put("pwd",pwd);
        return container;
    }
}
