package io.github.zuston.Util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zuston on 17/4/5.
 */
public class SessionUtil {
    public static void setRedisSession(String value){

    }

    public static void delRedisSession(String value){

    }

    public static String generateMd5(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(value.getBytes());
        return new BigInteger(1,md.digest()).toString(16);
    }
}
