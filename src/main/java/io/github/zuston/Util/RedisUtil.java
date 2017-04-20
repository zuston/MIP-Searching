package io.github.zuston.Util;

import redis.clients.jedis.Jedis;

/**
 * Created by zuston on 17/4/19.
 */
public class RedisUtil {

    public static int getSearchCount(String id){
        Jedis jedis = new Jedis("localhost");
        if (jedis.exists(id)){
            return Integer.parseInt(jedis.get(id));
        }else{
            return -1;
        }
    }

    public static boolean setSearchCount(String id, String count){
        Jedis jedis = new Jedis("localhost");
        return jedis.set(id,count).equals("OK");
    }

    public static String getSearchJson(String id){
        Jedis jedis = new Jedis("localhost");
        if (jedis.exists(id)){
            return jedis.get(id);
        }else{
            return "error";
        }
    }

    public static boolean setSearchJson(String id, String json){
        Jedis jedis = new Jedis("localhost");
        return jedis.set(id,json).equals("OK");
    }

    public static void main(String[] args) {
        System.out.println(getSearchCount("1002"));
    }
}
