package io.github.zuston.Helper;

import io.github.zuston.Util.PropertyUtil;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created by zuston on 17/5/10.
 */

public class ConfigHelper {
    public static HashMap<String,String> getDbConfig(){
        String filename = "mongo.properties";
        Properties prop = PropertyUtil.loadProperty(filename);
        String port = PropertyUtil.getString(prop,"port");
        String dbName = PropertyUtil.getString(prop,"dbName");
        String host = PropertyUtil.getString(prop,"host");
        String username = PropertyUtil.getString(prop,"username");
        String pwd = PropertyUtil.getProperty(prop,"pwd");

        HashMap<String,String> container = new HashMap<String, String>();
        container.put("host",host);
        container.put("port",port);
        container.put("dbName",dbName);
        container.put("username",username);
        container.put("pwd",pwd);
        return container;

    }
}
