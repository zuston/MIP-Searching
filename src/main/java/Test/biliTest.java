package Test;

import io.github.zuston.Service.BaseService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zuston on 17/4/16.
 */
public class biliTest {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        System.out.println(BaseService.downloadBiliInfo("1:1:1","18"));
    }
}
