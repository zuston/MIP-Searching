package io.github.zuston.MipCore;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;
import io.github.zuston.Util.Mapper.KeyMapper;
import io.github.zuston.Util.Mapper.RaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.zuston.MipCore.CoreExpressionDecoder.indexArr;

/**
 * 核心解析算法
 * Created by zuston on 17/5/2.
 */
public class CoreConditionGenerator {
    public final static Logger LOGGER = LoggerFactory.getLogger(CoreConditionGenerator.class);
    public static BasicDBObject coreContionGenertor(String formula,int flag){

        // 去除 搜索表达式 中所有的空格
        formula = formula.replace(" ","");

        // ^ 符号代表 是否按照 只包含 还是 包含 的条件来搜索
        // 此处是上行的条件 tag
        int inFlag = formula.indexOf("^")>=0?1:0;
        formula = formula.replace("^","");

        List<String> formualList = new ArrayList<>();

        // 针对 {Si,S,Se} 这种全组合的情况表达式解析
        // 生成全组合的例子
        if (formula.contains("{")&&formula.contains("}")&&formula.indexOf("{")==0&&formula.indexOf("}")==formula.length()-1){
            String tempFormula = generatorAllComponent(formula);
            formualList = CoreExpressionDecoder.complexAnaly(tempFormula);
            inFlag = 1;
        }else{
            formualList = CoreExpressionDecoder.simpleAnaly(formula);
        }

        LOGGER.info("解析之后的表达式列表:{}",formualList);
        BasicDBObject base = new BasicDBObject();
        if (formualList.size()>1){
            BasicDBList list = new BasicDBList();
            for (String oneFormula:formualList){
                list.add(simpleConditionGenertor(oneFormula,flag,inFlag));
            }
            base.put("$or",list);
        }else{
            base = simpleConditionGenertor(formualList.get(0),flag,inFlag);
        }
        return base;
    }

    private static String generatorAllComponent(String formula) {
        String strFormula = formula.substring(1,formula.length()).substring(0,formula.length()-2);
        String [] splitString = strFormula.split(",");
        List<String> componentArr = new ArrayList<>();
        List<String> tempArr = new ArrayList<>();
        allComponentFunction(splitString,tempArr,0,componentArr);

        return String.join("|",componentArr);
    }

    private static void allComponentFunction(String[] splitString, List<String> tempArr, int count, List<String> componentArr) {
        if (count == splitString.length){
            if (tempArr.size()!=0){
                componentArr.add("("+String.join("&", tempArr)+")");
            }
            return;
        }

        allComponentFunction(splitString,tempArr,count+1,componentArr);
        tempArr.add(splitString[count]);
        allComponentFunction(splitString,tempArr,count+1,componentArr);
        tempArr.remove(tempArr.size()-1);
    }

    /**
     * @param expression  CoreExpressionDecoder 生成之后的 list 中的一个检索条件
     * @param flag  计算，未计算，全部的 tag
     * @param inFlag    包含，只包含的 tag
     * @return
     */
    private static BasicDBObject simpleConditionGenertor(String expression,int flag,int inFlag) {
        ArrayList<String> race = new ArrayList<String>();
        BasicDBObject condition = new BasicDBObject();

        race.addAll(RaceMapper.race);


        // 和的列表，其中又分bandGAP 单元素 族系元素
        ArrayList<String> andList = indexArr(expression,'&');
        LOGGER.info("& 的组合列表:{}",andList);

        // 否的列表，其中分为族系元素
        ArrayList<String> notList = indexArr(expression,'~');
        if (andList.size()<=0){
            return null;
        }

        // 指定元素 list, 意指具体元素
        ArrayList<String> andShackList = new ArrayList<String>();
        // 族系元素 list， 意指族系元素的代称 1A etc
        ArrayList<String> andWaitList = new ArrayList<String>();

        for (String temp:andList){
            // andList 中存在的级别加入
            if (race.indexOf(temp)>-1 && !temp.contains("~")){
                andWaitList.add(temp);
                continue;
            }

            // 子查询条件确定，是否在其中存在 < > = etc
            if (temp.indexOf("=")>=0||temp.indexOf(">")>=0||temp.indexOf("<")>=0){
                conditionFormulaComponent(temp,condition);
                continue;
            }

            // TODO: 8/5/17 需要做一个 残余元素的验证，未通过则提示
            // 暂且剔除存在 ~1A 类似元素
            if (!temp.contains("~")){
                andShackList.add(temp);
            }
        }


        BasicDBObject conditionChildren = new BasicDBObject();
        if (andShackList.size()>0){
            conditionChildren.append("$all",andShackList);
        }

        /**
         * TODO: 17/3/27 多组in,修改为多组
         */
        if (andWaitList.size()>0){
            ArrayList<String> elements = new ArrayList<String>();
            for (String key:andWaitList){
                elements.addAll(RaceMapper.hm.get(key));
            }
            conditionChildren.append("$in",elements);
        }

        // 针对可能 ~1A or ~O or ~Cu 这种情况，做不同的判断
        if (notList.size()>0){
            List<String> elementsNotInList = new ArrayList<>();
            for (String key:notList){
                if (RaceMapper.hm.containsKey(key)){
                    elementsNotInList.addAll(RaceMapper.hm.get(key));
                    continue;
                }
                List<String> allElements = RaceMapper.getAllElements();
                if (allElements.indexOf(key)>-1)
                    elementsNotInList.add(key);
            }
            conditionChildren.append(QueryOperators.NIN,elementsNotInList);
        }
        // 查询表达式中 没有not in的选项,并且是族系查找
        if (notList.size()<=0&&andWaitList.size()>0){
            ArrayList<String> sb = (ArrayList<String>) RaceMapper.race.clone();
            for (String key:andWaitList){
                sb.remove(key);
            }
            ArrayList<String> elements = new ArrayList<String>();
            for (String value:sb){
                elements.addAll(RaceMapper.hm.get(value));
            }
            for (String value:andShackList){
                if (elements.indexOf(value)>-1){
                    elements.remove(value);
                }
            }
            conditionChildren.append(QueryOperators.NIN,elements);
        }
        // 查询表达式中没有not in选项，并且不是族系查找，且inFlag=1

        if (notList.size()<=0&&andWaitList.size()<=0&&andShackList.size()>0&&inFlag==1){
            ArrayList<String> allElements = RaceMapper.getAllElements();
            ArrayList<String> notInList = new ArrayList<String>();
            for (String element:allElements){
                if (andShackList.indexOf(element)<0){
                    notInList.add(element);
                }
            }
            conditionChildren.append(QueryOperators.NIN,notInList);
        }
        if (conditionChildren.size()>0){
//            condition.put("pymatgen_poscar.structure.sites.label",conditionChildren);
            condition.put("compound_components.element_name",conditionChildren);
        }
        if (flag==1){
            condition.put("is_computed",1);
        }
        LOGGER.info("生成的condition:{}",condition);
        return condition;
    }


    private static void conditionFormulaComponent(String temp,BasicDBObject condition){
        int equalFlag = temp.split("=").length;
        int gtFlag = temp.split(">").length;
        int ltFlag = temp.split("<").length;
        int egtFlag = temp.split(">=").length;
        int lgtFlag = temp.split("<=").length;

        // 此条件确定子查询中只有 = 符号
        if (equalFlag>1&&egtFlag<2&&lgtFlag<2){
            String kk = temp.split("=")[0];
            String vv = temp.split("=")[1];


            // TODO: 8/5/17 这边的 key ,value 均需要校验，是否符合规则
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            // 判定是否是区间范围, 以符号 - 来表示
            if (value.indexOf("-")>=0){
                String left = value.split("-")[0];
                String right = value.split("-")[1];
                // mapper 为子查询中的缩略代称
                key = KeyMapper.mapper.get(key);
                if (KeyMapper.DoubleList.contains(key)){
                    condition.put(key,new BasicDBObject("$gt",Integer.valueOf(left)).append("$lt",Integer.valueOf(right)));
                    return;
                }else{
                    return;
                }
            }

            // 等号且直接为值生成 condition ，例如 sg = 216 etc
            if (KeyMapper.DoubleList.contains(KeyMapper.mapper.get(key))){
                condition.put(KeyMapper.mapper.get(key),Integer.valueOf(value));
            }else if(KeyMapper.StringList.contains(KeyMapper.mapper.get(key))){
                // TODO: 2017/12/2 special_tag 检索增加
                String conditionKey = KeyMapper.mapper.get(key);
                BasicDBObject conditionArrays = new BasicDBObject();
                conditionArrays.append("$in",Arrays.asList(String.valueOf(value)));
                condition.put(conditionKey,conditionArrays);
            }else{
                // 此为 atomic_numbers_ratio 元素比例的情况 es=1:1 etc
                // 此处按需求增加 es=1:2 转化为 es=1:2 or es=2:1 的检索情况, 标记为 *
                boolean ratioAllTag = value.contains("*");
                if (ratioAllTag)
                    value = value.substring(0,value.length()-1);
                BasicDBList esConditionTempList = new BasicDBList();
                List<String> ratioAllChangeList = new ArrayList<>(new HashSet<>(generateRatioAll(value)));
                for (String ratioValue : ratioAllChangeList)
                    esConditionTempList.add(new BasicDBObject(KeyMapper.mapper.get(key),ratioValue));
                condition.put("$or",esConditionTempList);
            }
            return;
        }

        // 此条件确定子查询中只有 > 符号
        if (gtFlag>1&&egtFlag<=1){
            String kk = temp.split(">")[0];
            String vv = temp.split(">")[1];
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            key = KeyMapper.mapper.get(key);

            if (KeyMapper.DoubleList.contains(key)){
                condition.put(key,new BasicDBObject("$gt",Integer.valueOf(value)));
            }
            return;
        }
        // 此条件确定子查询中只有 < 符号
        if (ltFlag>1&&lgtFlag<=1){
            String kk = temp.split("<")[0];
            String vv = temp.split("<")[1];
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            key = KeyMapper.mapper.get(key);
            System.out.println(key);
            if (KeyMapper.DoubleList.contains(key)){
                condition.put(key,new BasicDBObject("$lt",Integer.valueOf(value)));
            }
            return;
        }

        // 此条件确定子查询中只有 >= 符号
        if (egtFlag>1){
            String kk = temp.split(">=")[0];
            String vv = temp.split(">=")[1];
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            key = KeyMapper.mapper.get(key);
            if (KeyMapper.DoubleList.contains(key)){
                condition.put(key,new BasicDBObject("$gte",Integer.valueOf(value)));
            }
            return;
        }

        // 此条件确定子查询中只有 <= 符号
        if (lgtFlag>1){
            String kk = temp.split("<=")[0];
            String vv = temp.split("<=")[1];
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            if (KeyMapper.DoubleList.contains(key)){
                condition.put(KeyMapper.mapper.get(key),new BasicDBObject("$lte",Integer.valueOf(value)));
            }
            return;
        }
    }

    // 根据 1：2：3 生成 1：2：3 & 1：3：2 & 2：1：3 & 2：3：1 & 3：1：2 & 3：2：1 全排列组合
    private static List<String> generateRatioAll(String value) {
        List<String> resList = new ArrayList<>();
        List<String> needRatioAll = new ArrayList<String>(Arrays.asList(value.split(":")))
                .stream().collect(Collectors.toList());
        Integer count = 0;
        generateRatioLoop(resList,needRatioAll, count);
        return resList;
    }

    // 全排列算法
    private static void generateRatioLoop(List<String> resList, List<String> needRatioAll, Integer count) {
        if (count == needRatioAll.size()){
            resList.add(needRatioAll.stream().reduce("",(t1,t2)->t1+":"+t2).substring(1));
            return;
        }
        for (int i=count;i<needRatioAll.size();i++){
            swap(needRatioAll,count,i);
            generateRatioLoop(resList,needRatioAll,count+1);
            swap(needRatioAll,count,i);
        }
    }

    private static void swap(List<String> list, Integer start, Integer changeIndex){
        String startV = list.get(start);
        list.set(start,list.get(changeIndex));
        list.set(changeIndex,startV);
    }
}
