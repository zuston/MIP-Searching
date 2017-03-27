package io.github.zuston.Service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.tools.javah.Util;
import io.github.zuston.Bean.ConditionBean;
import io.github.zuston.Bean.ConditionsBean;
import io.github.zuston.Util.MongoDb;
import io.github.zuston.Util.RaceMapper;
import org.bson.Document;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Created by zuston on 17-2-22.
 */
public class BaseService {

    public static MongoDatabase mongoDataBase = MongoDb.getInstance();
    public static MongoCollection<Document> mongoColletion = mongoDataBase.getCollection("vasp_input");

    public static MongoCollection<Document> duplicateColletion = mongoDataBase.getCollection("dumplicate_conditions");

    public static String getInfo(ConditionsBean conditionsBean) {

        ArrayList<ConditionBean> conditionsList = conditionsBean.getCondition();


        BasicDBObject condition = new BasicDBObject();
        for(ConditionBean conditionBean:conditionsList){
            String key = conditionBean.getName();
            int tag = conditionBean.getTag();
            ArrayList<String> contentArray = conditionBean.getContentArray();

            Double content = null;
            if(tag<=2){
                content = Double.valueOf(contentArray.get(0).trim());
                System.out.println();
                System.out.println(content);
            }

            switch (tag){
                case 0:
                    condition.put(key,content);
                    break;
                case 1:
                    condition.put(key,new BasicDBObject("$lt",content));
                    break;
                case 2:
                    condition.put(key,new BasicDBObject("$bt",content));
                    break;
                case 3:
                    Integer content1 = Integer.valueOf(contentArray.get(0).trim());
                    Integer content2 = Integer.valueOf(contentArray.get(1).trim());
                    condition.put(key,new BasicDBObject("$bt",content1));
                    condition.put(key,new BasicDBObject("$lt",content2));
                    break;
                case 4:
                    condition.put(key,contentArray.get(0).trim());
                    break;
                case 5:
                    if (key.equals("poscar.comment")){
                        condition.put("poscar.structure.sites.label",new BasicDBObject("$all",contentArray));
                    }else{
                        Pattern pattern = Pattern.compile("^.*"+contentArray.get(0)+".*$", CASE_INSENSITIVE);
                        condition.put(key,pattern);
                    }
                    break;
                default:
                    break;
            }
        }


        long totalCount = mongoColletion.count(condition);

        int page = conditionsBean.page;

        StringBuilder jsonAppendString = new StringBuilder("{\"cpage\":");
        jsonAppendString.append(page);
        jsonAppendString.append(",\"count\":");
        jsonAppendString.append(totalCount);
        jsonAppendString.append(",\"c\":[");

        for(Document document:mongoColletion.find(condition).skip((page-1)*20).limit(20)){
//            JsonObject jsonObject = (JsonObject)r new JsonParser().parse(document.toJson());
//            System.out.println(document.toJson());
            jsonAppendString.append(document.toJson()).append(",");
        }
        String jsonStrTemp = jsonAppendString.substring(0,jsonAppendString.length()-1);
        return jsonStrTemp+"]}";
    }

    public static String getInfo(String expression, int page) {
        if (expression.split("\\|").length>1){
            BasicDBObject base = new BasicDBObject();
            BasicDBList co = new BasicDBList();
            for (String str:expression.split("\\|")){
                co.add(getInfoComplex(str,page));
            }

            base.put("$or",co);
            return BaseService.resAppend(base,page);
        }
        return BaseService.resAppend(BaseService.getInfoComplex(expression,page),page);
    }

    //增加族系复合搜索
    private static BasicDBObject getInfoComplex(String expression,int page){
        System.out.println(expression);
        ArrayList<String> race = new ArrayList<String>();
        race.addAll(RaceMapper.race);
        // 和的列表，其中又分bandGAP 单元素 族系元素
        // TODO: 17/3/27 单元素可能会报错
        ArrayList<String> andList = indexArr(expression,'&');
        // 否的列表，其中分为族系元素
        ArrayList<String> notList = indexArr(expression,'~');
        if (andList.size()<=0){
            return null;
        }

        ArrayList<String> andShackList = new ArrayList<String>();
        ArrayList<String> andWaitList = new ArrayList<String>();

        Double bandGap = null;
        String spaceGroup = null;

        for (String temp:andList){
            if (race.indexOf(temp)>-1){
                andWaitList.add(temp);
            }else if (temp.indexOf("bandgap")>=0){
                String t = temp.split("=")[1];
                bandGap = Double.valueOf(t.substring(0,t.length()-1));
            }else if (temp.indexOf("spacegroup")>=0){
                String t = temp.split("=")[1];
                spaceGroup = String.valueOf(t.substring(0,t.length()-1));
            }else{
                andShackList.add(temp);
            }
        }

        BasicDBObject condition = new BasicDBObject();
        BasicDBObject conditionChildren = new BasicDBObject();
        if (andShackList.size()>0){
            conditionChildren.append("$all",andShackList);
        }

        // TODO: 17/3/27 多组in,修改为多组
        if (andWaitList.size()>0){
            ArrayList<String> elements = new ArrayList<String>();
            for (String key:andWaitList){
                elements.addAll(RaceMapper.hm.get(key));
            }
            conditionChildren.append("$in",elements);
        }

        if (bandGap!=null){
            condition.put("caculate.Band_Gap",new BasicDBObject().append("$eq",bandGap));
        }
        if (spaceGroup!=null){
            condition.put("caculate.Spacegroup",spaceGroup);
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
        condition.put("poscar.structure.sites.label",conditionChildren);
        System.out.println("筛选条件语句:");
        System.out.println(condition);
        return condition;
    }

    private static BasicDBObject getInfoAnd(String expression, int page) {
        ArrayList<String> elementList = new ArrayList<String>();
        Double bandGap = null;
        String spaceGroup = null;
        for(String str:expression.split("&")){
            if(str.length()<3){
                elementList.add(str);
            }else{
                if (str.indexOf("bandgap")>=0){
                    String temp = str.split("=")[1];
                    bandGap = Double.valueOf(temp.substring(0,temp.length()-1));
                }
                if (str.indexOf("spacegroup")>=0){
                    String temp = str.split("=")[1];
                    spaceGroup = String.valueOf(temp.substring(0,temp.length()-1));
                }
            }
        }
        BasicDBObject condition = new BasicDBObject();

        if (elementList.size()>0){
            condition.put("poscar.structure.sites.label",new BasicDBObject("$all",elementList));
        }


        if (bandGap!=null){
            condition.put("caculate.Band_Gap",bandGap);
        }
        if (spaceGroup!=null){
            condition.put("caculate.Spacegroup",spaceGroup);
        }

        return condition;
    }

    private static String resAppend(BasicDBObject base,int page){
        long totalCount = mongoColletion.count(base);
//        int totalCount = 0;

        StringBuilder jsonAppendString = new StringBuilder("{\"cpage\":");
        jsonAppendString.append(page);

        jsonAppendString.append(",\"c\":[");

        boolean flag = false;
        for(Document document:mongoColletion.find(base).skip((page-1)*10).limit(10)){
//        for(Document document:mongoColletion.find(base)){

            String id = (String) document.get("m_id");

            Document dumplicate = null;
//             获取查重表中的数据
            for (Document dd:duplicateColletion.find(new BasicDBObject("source_folder_name",id)).limit(1)){
                dumplicate = dd;
            }

            if(dumplicate!=null){
                totalCount+=1;
                String spaceGroup = (String) dumplicate.get("space_group_type");
                String symmetrys = (String) dumplicate.get("independent_atom_site_symmetrys");
                String compound_name = dumplicate.getString("compound_name");
                String initStr = document.toJson().substring(0,document.toJson().length()-1);

                String endStr = initStr+",\"spaceGroup\":\""+spaceGroup+"\",\"symmetrys\":\""+symmetrys+"\",\"compound_name\":\"" + compound_name + "\"}";
                jsonAppendString.append(endStr).append(",");
                flag = true;
            }
        }
        StringBuilder dataSuffix = new StringBuilder();
        if (flag==true){
            String jsonStrTemp = jsonAppendString.substring(0,jsonAppendString.length()-1);
            dataSuffix.append(jsonStrTemp+"]");
        }else{
            dataSuffix.append("]");
        }
        dataSuffix.append(",\"count\":");
        dataSuffix.append(totalCount);
        dataSuffix.append("}");
        return dataSuffix.toString();
    }

    private static String Test(BasicDBObject base,int page){
        int totalCount = 0;

        for(Document document:mongoColletion.find(base)){
            String id = (String) document.get("m_id");

            Document dumplicate = null;
            for (Document dd:duplicateColletion.find(new BasicDBObject("source_folder_name",id)).limit(1)){
                dumplicate = dd;
            }
            if(dumplicate!=null){
                totalCount+=1;

            }
        }
        return String.valueOf(totalCount);
    }

    public static void main(String[] args) {
        String str = "Se&1A&2A&1B&2B&3A&4A|S&1A&2A&1B&2B&3A&4A|Te&1A&2A&1B&2B&3A&4A";
        System.out.println(getInfo(str,1));
    }



    // 根据符号筛选出条件
    private static ArrayList<String> indexArr(String str,char tag){
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

    public static ArrayList<String> analysisExpression(String str){
        Stack<Character> stack = new Stack<Character>();
        char [] strChar = str.toCharArray();
        for (char c:strChar){
            if (c==')'){
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
                    }
                }
            }else {
                stack.push(c);
            }
        }
        return null;
    }
}
