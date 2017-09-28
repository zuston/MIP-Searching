package Test;

import io.github.zuston.Helper.RedisHelper;
import io.github.zuston.Util.SessionUtil;

import java.security.NoSuchAlgorithmException;

/**
 * Created by zuston on 17/5/4.
 */
public class redisTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(RedisHelper.getInt("hello"));
        System.out.println(SessionUtil.generateMd5("admin"));
        System.out.println(SessionUtil.generateMd5("admin"));
    }
}
