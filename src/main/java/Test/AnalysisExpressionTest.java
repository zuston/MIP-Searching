package Test;

import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import static io.github.zuston.Service.BaseService.analysisExpression;

/**
 * Created by zuston on 17/3/27.
 */
public class AnalysisExpressionTest {
    public static void main(String[] args) {
        String testStr1 = "(Se|S|Te)&1A&2A&(G|W)";
        System.out.println(simpleAnaly(testStr1));
    }

    public static ArrayList<String> simpleAnaly(String str){
        Stack<Character> stack = new Stack<Character>();
        char [] strChar = str.toCharArray();
        int flag = 0;
        StringBuilder sb = new StringBuilder();
        ArrayList<String> arr = new ArrayList<String>();
        for (char c:strChar){


            if (c==')'){
                sb = new StringBuilder();
                while (stack.peek()!='('){
                    sb.append(stack.pop());
                }
                stack.pop();
                if (sb.length()>0){
                    arr.add(String.valueOf(sb.reverse()));
                }
            }else{
                stack.push(c);
            }
        }
        sb = new StringBuilder();
        while (!stack.isEmpty()){
            sb.append(stack.pop());
        }
        sb = sb.reverse();
        System.out.println(arr);
        System.out.println(sb);
        if (arr.isEmpty()){
            return new ArrayList<String>(Arrays.asList(str));
        }
        if (sb.length()>=2){
            int len = arr.size();
            ArrayList<String> front = new ArrayList<String>();
            ArrayList<String> end = new ArrayList<String>();
            for (int i=0;i<len;i++){
                if (isTag(sb.charAt(i))){
                    front.add(String.valueOf(sb.charAt(i)));
                }
                if (isTag(sb.charAt(sb.length()-1-i))){
                    end.add(String.valueOf(sb.charAt(sb.length()-1-i)));
                }
//                System.out.println("头部"+sb.charAt(i));
//                System.out.println("尾部"+sb.charAt(sb.length()-1-i));
            }
            System.out.println(front);
            Collections.reverse(end);
            System.out.println(end);
            ArrayList<ArrayList<String>> allArr = new ArrayList<ArrayList<String>>();
            ArrayList<String> andArr = new ArrayList<String>();
            for (String splitStr:arr){
                if (splitStr.indexOf("|")>0){
                    ArrayList<String> pp = new ArrayList<String>();
                    for (String s:splitStr.split("|")){
                        pp.add(s);
                    }
                    allArr.add(pp);
                }else if (splitStr.indexOf("&")>0){
//                    StringBuilder vv = new StringBuilder();
//                    vv.append(splitStr);
//                    andArr.add(String.valueOf(vv));
                    sb.append(splitStr);
                }
            }
            System.out.println(sb);
            System.out.println(allArr);
        }

        return null;
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
