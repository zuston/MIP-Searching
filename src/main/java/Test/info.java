package Test;

import io.github.zuston.Service.BaseService;

import java.io.IOException;

/**
 * Created by zuston on 17/4/16.
 */
public class info {
    public static void main(String[] args) throws IOException {

        getInfo();

    }


    public static void getInfo(){
        String a = BaseService.getComplexInfo("58a3f4f20095896a600fccda");
        System.out.println(a);
    }

    /**
     * 比例信息查询结果
     */
    public static void testBiliInfo(){
        String c = BaseService.getBiliInfo("1:1","8",1,1);
        System.out.println(c);
    }

    /**
     * jsmol的文件输出
     */
    public static void testJsmolText(){
        String a = BaseService.getJSmolInfo("58a3f4e80095896a600fcb9e");
        System.out.println(a);
    }

    public static void testExpression(){
        String value = BaseService.getInfo("Se&Au&Rb",1,1);
    }
}
