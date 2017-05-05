package Test;

import io.github.zuston.Util.RedisSession;
import io.github.zuston.Util.RedisUtil;

import java.security.NoSuchAlgorithmException;

/**
 * Created by zuston on 17/5/4.
 */
public class redisTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(RedisUtil.getValue("hello"));
        System.out.println(RedisSession.generateMd5("admin"));
        System.out.println(RedisSession.generateMd5("admin"));
    }
}
