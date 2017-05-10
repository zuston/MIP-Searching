package io.github.zuston.Util;

import redis.clients.jedis.Jedis;

/**
 * Created by zuston on 17/4/19.
 */
public class RedisUtil {

    public static Jedis jedis = new Jedis("localhost");

    public static int getSearchCount(String id){
        if (jedis.exists(id)){
            return Integer.parseInt(jedis.get(id));
        }else{
            return -1;
        }
    }

    public static boolean setSearchCount(String id, String count){
        return jedis.set(id,count).equals("OK");
    }

    public static String getSearchJson(String id){
        if (jedis.exists(id)){
            return jedis.get(id);
        }else{
            return "error";
        }
    }

    public static boolean setSearchJson(String id, String json){
        return jedis.set(id,json).equals("OK");
    }

    public static String getValue(String key){
        String value = jedis.get(key);
        return value==null?"nullzero":value;
    }

}
