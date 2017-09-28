package io.github.zuston.MipCore;

import io.github.zuston.Util.Mapper.RaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by zuston on 17-3-28.
 */
public class CoreExpressionDecoder {
    public final static Logger logger = LoggerFactory.getLogger(CoreExpressionDecoder.class);

    // TODO: 9/26/17
    // 针对 (Si&S)|(H&He)|(G&Y) 这种情况
    public static List<String> complexAnaly(String str){
        boolean tag = false;
        if (!str.contains("|")) return simpleAnaly(str);
        List<Integer> indexList = new ArrayList<>();
        int c = 0;
        // 查找 | 的位置,
        for (char value : str.toCharArray()){
            if (value=='|'){
                indexList.add(c);
            }
            c++;
        }
        char [] strArr = str.toCharArray();
        int tagValue = 0;
        for (Integer value : indexList){
            if (value-1>=0 && value+1<=str.length()-1){
                if (strArr[value-1]==')'&&strArr[value+1]=='('){
                    tagValue ++;
                }
            }
        }
        if (tagValue == indexList.size())
            tag = true;

        if (!tag)   return simpleAnaly(str);

        // 复杂表达式时候的解析
        List<String> analyArr = new ArrayList<>();
        for (int i=0;i<=indexList.size();i++){
            int start = 0;
            int end = 0;
            if (i==0){
                start = 0;
                end = indexList.get(i);
            }
            else if (i==indexList.size()){
                start = indexList.get(i-1)+1;
                end = str.length();
            }
            else {
                start = indexList.get(i-1)+1;
                end = indexList.get(i);
            }
            String tempStr = str.substring(start,end);
            analyArr.addAll(simpleAnaly(tempStr));
        }
        return analyArr;
    }

    // 解析检索表达式核心算法
    // 暂时不支持 | 的多表达式解析
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

        logger.info("搜索表达式:{}",str);
        // arr 代表括号里面的元素
        logger.info("括号内元素:{}",arr);
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
        logger.info("括号内筛选元素:{}",filterInfo);
        logger.info("括号外面的元素:{}",sb);
        logger.info("筛选拆分的组合:{}",conditionSplitComponent);

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

        logger.info("搜索语句分析结果集:{}",conditionRes);
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
                if (i==1 || i==2||i==3){
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
        if (res.size()==0&&tag=='&'){
            HashMap<String,ArrayList<String>> hm = RaceMapper.hm;
            ArrayList<String> raceList = RaceMapper.race;
            if (raceList.contains(str)){
                res.add(str);
                return res;
            }
            for (Map.Entry<String,ArrayList<String>> value:hm.entrySet() ){
                for (String v:value.getValue()){
                    if (v.equals(str)){
                        res.add(str);
                        return res;
                    }
                }
            }
        }
        return res;
    }
}
