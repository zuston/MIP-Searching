package Test;

import io.github.zuston.MipCore.CoreConditionGenerator;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by zuston on 17/3/27.
 */
public class AnalysisExpressionTest {
    public static void main(String[] args) {
        String testStr1 = "{Si,S,H}";
//        System.out.println(CoreExpressionDecoder.complexAnaly(testStr1));
        System.out.println(CoreConditionGenerator.coreContionGenertor(testStr1,0));
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
