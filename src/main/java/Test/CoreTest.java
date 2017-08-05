package Test;

import io.github.zuston.MipCore.CoreExpressionDecoder;

import java.util.List;

/**
 * Created by zuston on 8/5/17.
 */
public class CoreTest {
    public static void main(String[] args) {
        CoreExpressionDecoder("H&(1A|2A)");

        indexArr("(sg=216)&(en=2)~1A");
    }


    public static void CoreExpressionDecoder(String expression){
        List<String> expressionList = CoreExpressionDecoder.simpleAnaly(expression);
        expressionList.stream().forEach(System.out::println);
    }

    public static void indexArr(String expression){
        List<String> indexList = CoreExpressionDecoder.indexArr(expression,'&');
        indexList.stream().forEach(System.out::println);
    }
}
