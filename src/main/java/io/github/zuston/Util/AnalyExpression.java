package io.github.zuston.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

/**
 * Created by zuston on 17-3-28.
 */
public class AnalyExpression {
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
            return new ArrayList<String>(Arrays.asList(str.split("\\|")));
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

            }
            System.out.println("前缀"+front);
            Collections.reverse(end);
            System.out.println("后缀"+end);

            ArrayList<String> allStr = new ArrayList<String>();
            for (String splitStr:arr){
                if (splitStr.indexOf("\\|")>0){
                    allStr.add(splitStr);
                }else if (splitStr.indexOf("&")>0){
                    sb.append(splitStr);
                }
            }
            // 针对于左单括号
            if (front.size()==1&&end.size()==0){
                if (front.get(0).equals("&")){
                    ArrayList<String> resS = new ArrayList<String>();
                    for (String splitStr:arr.get(0).split("\\|")){
                        System.out.println(splitStr);
                        StringBuilder res = new StringBuilder();
                        res.append(splitStr).append(sb);
                        resS.add(String.valueOf(res));
                    }
                    return resS;
                }
            }

            // 针对于右单括号
            if (front.size()==0&&end.size()==1){
                if (end.get(0).equals("&")){
                    ArrayList<String> resS = new ArrayList<String>();
                    for (String splitStr:arr.get(0).split("\\|")){
                        StringBuilder res = new StringBuilder();
                        res.append(sb).append(splitStr);
                        resS.add(String.valueOf(res));
                    }
                    return resS;
                }
            }
        }

        return null;
    }

    public static boolean isTag(char c){
        if (c=='&'||c=='~'||c=='|'){
            return true;
        }
        return false;
    }

    // 根据符号筛选出条件
    public static ArrayList<String> indexArr(String str,char tag){
        ArrayList<String> res = new ArrayList<String>();
        char [] s = str.toCharArray();
        int flag = -1;
        for(int i=0;i<s.length;i++){
            if (s[i]==tag){
                if (i==1 || i==2){
                    flag = i;
                }
                String sb = "";
                for (int j=i+1;j<s.length;j++){
                    if (s[j]!='&'&&s[j]!='|'&&s[j]!='~'){
                        sb+=s[j];
                    }else{
                        break;
                    }
                }
                if (!sb.equals("")){
                    res.add(sb);
                }
            }
        }
        if (flag!=-1){
            System.out.println(flag);
            String sb = "";
            for (int i=0;i<flag;i++){
                sb+=s[i];
            }
            res.add(sb);
        }
        return res;
    }
}
