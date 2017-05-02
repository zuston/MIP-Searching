package io.github.zuston.Util;

import java.util.ArrayList;
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
                if (stack.size()>0){
                    stack.pop();
                }
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
        System.out.println("搜索表达式:"+str);
        // arr 代表括号里面的元素
        System.out.println("括号里面的元素："+arr);
        // arr 里面含有spaceGroup等筛选信息
        ArrayList<String> filterInfo = new ArrayList<String>();
        // arr 里面进行表达式组合的信息
        ArrayList<String> componentInfo = new ArrayList<String>();
        // arr 里面类似(po=10|89)这种，还得要拆分
        ArrayList<String> conditionSplitComponent = new ArrayList<String>();
        for (String i:arr){
            if (i.split("\\|").length<=1&&(i.indexOf("=")>=0||i.indexOf(">")>=0||i.indexOf("<")>=0||i.indexOf("-")>=0)){
                filterInfo.add(i);
            }else if(i.split("\\|").length>1&&i.indexOf("=")>=0){
                conditionSplitComponent.add(i);
            }else{
                componentInfo.add(i);
            }
        }
        System.out.println("括号内筛选元素:"+filterInfo);
        System.out.println("括号外面的元素："+sb);
        System.out.println("筛选拆分的组合:"+conditionSplitComponent);

        ArrayList<String> gres = new ArrayList<String>();

        if (componentInfo.isEmpty()){
            gres.add(String.valueOf(sb));
        }else{
            for (String splitStr:componentInfo.get(0).split("\\|")){
                if (sb.length()==0){
                    gres.add(String.valueOf(splitStr));
                    continue;
                }
                StringBuilder res = new StringBuilder();
                res.append(sb).append("&").append(splitStr);
                gres.add(String.valueOf(res));
            }
        }



        ArrayList<String> finalRes = new ArrayList<String>();
        for (String gresOne:gres){
            StringBuilder temp = new StringBuilder();
            temp.append(gresOne);
            for (String filterCondition:filterInfo){
                temp.append("&(").append(filterCondition).append(")");
            }
            finalRes.add(temp.toString());
        }

        // 如果没有类似于 ve=10|9 这种条件，直接返回原先结果集
        if (conditionSplitComponent.size()==0){
            return finalRes;
        }


        // 补充拆分
        ArrayList<String> conditionRes = new ArrayList<String>();
        for (String originRes:finalRes){
            for (String condition:conditionSplitComponent){
                String conditionKeyName = condition.split("=")[0];
                for (String co:condition.split("=")[1].split("\\|")){
                    StringBuilder temp = new StringBuilder(originRes);
                    temp.append("&(").append(conditionKeyName).append("=").append(co).append(")");
                    conditionRes.add(temp.toString());
                }
            }
        }

        System.out.println("最后的分析语句结果集:"+conditionRes);
        return conditionRes;

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
