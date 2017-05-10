package Test;

import io.github.zuston.Service.BaseService;
import io.github.zuston.Service.BaseServiceV2;

import java.io.IOException;

/**
 * Created by zuston on 17/4/16.
 */
public class info {
    public static void main(String[] args) throws IOException {

//        getInfoV2();
        testFromMongo();
    }


    public static void getInfo(){
        String a = BaseService.getComplexInfo("58a3f4f20095896a600fccda");
        System.out.println(a);
    }

    public static void getInfoV2(){
        String value = BaseServiceV2.basicDetailInfoFunction("5900b6b174009c740ac5d710");
        System.out.println(value);
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
        String value = BaseService.getInfo("S&Se",1,1);
        System.out.println(value);
    }

    public static void testInfo(){
        System.out.println(BaseServiceV2.basicJsmolFunctionFromMongoDb(""));
    }

    public static void testFromMongo(){
        System.out.println(BaseServiceV2.basicJsmolFunctionFromMongoDb("590b2b34d3566a122b5be6c0"));
    }
}
