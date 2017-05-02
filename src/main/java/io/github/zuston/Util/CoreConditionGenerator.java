package io.github.zuston.Util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;

import java.util.ArrayList;

import static io.github.zuston.Util.AnalyExpression.indexArr;

/**
 * Created by zuston on 17/5/2.
 */
public class CoreConditionGenerator {
    public static BasicDBObject coreContionGenertor(String formula){
        ArrayList<String> formualList = AnalyExpression.simpleAnaly(formula);
        BasicDBObject base = new BasicDBObject();
        if (formualList.size()>1){
            BasicDBList list = new BasicDBList();
            for (String oneFormula:formualList){
                list.add(simpleConditionGenertor(oneFormula));
            }
            base.put("$or",list);
        }else{
            base = simpleConditionGenertor(formualList.get(0));
        }
        return base;
    }

    private static BasicDBObject simpleConditionGenertor(String expression) {
        ArrayList<String> race = new ArrayList<String>();
        race.addAll(RaceMapper.race);
        /**
         * 和的列表，其中又分bandGAP 单元素 族系元素
         * TODO: 17/3/27 单元素可能会报错
         */
        ArrayList<String> andList = indexArr(expression,'&');
        /**
         * 否的列表，其中分为族系元素
         */
        ArrayList<String> notList = indexArr(expression,'~');
        System.out.println(andList);
        if (andList.size()<=0){
            return null;
        }

        ArrayList<String> andShackList = new ArrayList<String>();
        ArrayList<String> andWaitList = new ArrayList<String>();

        Integer spaceGroup = null;
        String ElementScale = null;
        Integer ValenceElectrons = null;

        for (String temp:andList){
            if (race.indexOf(temp)>-1){
                andWaitList.add(temp);
                continue;
            }

            if (temp.indexOf("spacegroup")>=0){
                String t = temp.split("=")[1];
                spaceGroup = Integer.valueOf(t.substring(0,t.length()-1));
                continue;
            }

            if(temp.indexOf("es")>=0){
                String esValue = temp.split("=")[1];
                esValue = String.valueOf(esValue.substring(0, esValue.length()-1));
                ElementScale = esValue;
                continue;
            }
            if(temp.indexOf("ve")>=0){
                String veValue = temp.split("=")[1];
                veValue = String.valueOf(veValue.substring(0,veValue.length()-1));
                ValenceElectrons = Integer.valueOf(veValue);
                continue;
            }

            andShackList.add(temp);

        }



        BasicDBObject condition = new BasicDBObject();
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

        if (spaceGroup!=null){
            condition.put("space_group_type_number",Integer.valueOf(spaceGroup));
        }
        if (ElementScale!=null){
            condition.put("atomic_numbers_ratio",ElementScale);
        }
        if (ValenceElectrons!=null){
            condition.put("valence_electrons_sum",ValenceElectrons);
        }

        if (notList.size()>0){
            for (String key:notList){
                conditionChildren.append(QueryOperators.NIN,RaceMapper.hm.get(key));
            }
        }
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
        if (conditionChildren.size()>0){
            condition.put("pymatgen_poscar.structure.sites.label",conditionChildren);
        }
        System.out.println("筛选条件语句:");
        System.out.println(condition);
        System.out.println();
        return condition;
    }
}
