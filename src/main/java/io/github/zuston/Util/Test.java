package io.github.zuston.Util;

import redis.clients.jedis.Jedis;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zuston on 17/4/5.
 */
public class Test {
    private boolean checkParams(String[] params) {
        for (String param : params) {
            if (param == "" || param == null || param.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static String generateMd5(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(value.getBytes());
        return new BigInteger(1,md.digest()).toString(16);
    }

    public static void setRedisSession(String name) throws NoSuchAlgorithmException {
        String bg = generateMd5(name);
        Jedis jedis = new Jedis("localhost");
        System.out.println(bg+"Login");
        if (!jedis.exists(bg)){
            jedis.set(bg+"Login","1");
        }else{
            jedis.persist(bg+"Login");
        }
        jedis.expire(bg+"Login",86400);
    }

    public static void delRedisSession(String name) throws NoSuchAlgorithmException {
        Jedis jedis = new Jedis("localhost");
        String bg = generateMd5(name);
        if (jedis.exists(bg+"Login")){
            jedis.set(bg+"Login","0");
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        delRedisSession("9999");

    }
}
