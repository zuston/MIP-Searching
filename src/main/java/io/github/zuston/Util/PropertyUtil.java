package io.github.zuston.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by zuston on 17/5/10.
 */
public class PropertyUtil {
    public final static Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);
    public static Properties loadProperty(String filename) {
        String outPath = "/opt/";
        Properties prop = new Properties();
        InputStream ins =null;
        ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (ins==null){
            try {
                ins = new FileInputStream(outPath+filename);
                LOGGER.info("加载/opt/下的mongo配置文件");
                prop.load(ins);
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return prop;
            }
        } else{
            try {
                prop.load(ins);
                LOGGER.info("加载项目下的mongo配置文件");
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return prop;
            }

        }
    }

    public static String getProperty(Properties prop,String key){
        return prop.getProperty(key);
    }

    public static String getString(Properties prop,String key,String defaultValue){
        String value = defaultValue;
        if(prop.containsKey(key)){
            value = prop.getProperty(key);
        }
        return value;
    }

    public static String getString(Properties prop,String key){
        return getString(prop,key,"");
    }

    public static int getInt(Properties prop,String key){
        return getInt(prop,key,0);
    }
    public static int getInt(Properties prop,String key,int defaultValue){
        int value = defaultValue;
        if(prop.containsKey(key)){
            value = Integer.valueOf(prop.getProperty(key));
        }
        return value;
    }

    public static boolean getBoolean(Properties prop,String key){
        return getBoolean(prop,key,false);
    }

    public static boolean getBoolean(Properties prop,String key,boolean defaultValue){
        boolean value = defaultValue;
        if(prop.containsKey(key)){
            value = Boolean.valueOf(prop.getProperty(key));
        }
        return value;
    }
}
