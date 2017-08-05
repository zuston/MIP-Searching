package Test;

import java.util.ArrayList;
import java.util.Stack;

import static io.github.zuston.MipCore.CoreExpressionDecoder.indexArr;

/**
 * Created by zuston on 17/3/27.
 */
public class AnalysisExpressionTest {
    public static void main(String[] args) {
        String testStr1 = "Si";
//        System.out.println(BaseServiceV2.basicInfoFunction(testStr1,1,1));
//        System.out.println(CoreConditionGenerator.coreContionGenertor(testStr1,0));
        System.out.println(indexArr("1A",'&'));
//        System.out.println();
//        BaseService.getInfo(testStr1,1,1);
    }


    public static boolean isTag(char c){
        if (c=='&'||c=='~'||c=='|'){
            return true;
        }
        return false;
    }

    public static ArrayList<String> analyExpression(String str){
        Stack<Character> stack = new Stack<Character>();
        char [] strChar = str.toCharArray();
        for (char c:strChar){
            if (c==')'){
                int flag = 0;
                StringBuilder sb = new StringBuilder();
                while (!stack.isEmpty()){
                    char value = stack.peek();
                    if (value!='('){
                        sb.append(value);
                    }else{
                        char tag = stack.pop();
                        if(tag=='&'||tag=='|'||tag=='~'){
                            if (tag=='&'){
                                StringBuilder s = sb.reverse();
                                System.out.println(s.toString());
                            }
                        }
                        flag = 1;
                    }
                    if (flag==1){
                        break;
                    }
                }
            }else {
                stack.push(c);
            }
        }
        return null;
    }

}
