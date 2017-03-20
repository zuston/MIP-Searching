package io.github.zuston.Service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.zuston.Bean.ConditionBean;
import io.github.zuston.Bean.ConditionsBean;
import io.github.zuston.Util.MongoDb;
import org.bson.Document;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Created by zuston on 17-2-22.
 */
public class BaseService {

    public static MongoDatabase mongoDataBase = MongoDb.getInstance();
    public static MongoCollection<Document> mongoColletion = mongoDataBase.getCollection("vasp_input");


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
        if (expression.split("7").length>1){
            BasicDBObject base = new BasicDBObject();
            BasicDBList co = new BasicDBList();
            for (String str:expression.split("7")){
                co.add(BaseService.getInfoAnd(str,page));
            }

            base.put("$or",co);

            return BaseService.resAppend(base,page);

        }
        return BaseService.resAppend(BaseService.getInfoAnd(expression,page),page);
    }

    private static BasicDBObject getInfoAnd(String expression, int page) {
        ArrayList<String> elementList = new ArrayList<String>();
        Double bandGap = null;
        String spaceGroup = null;
        for(String str:expression.split("9")){
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

        StringBuilder jsonAppendString = new StringBuilder("{\"cpage\":");
        jsonAppendString.append(page);
        jsonAppendString.append(",\"count\":");
        jsonAppendString.append(totalCount);
        jsonAppendString.append(",\"c\":[");

        boolean flag = false;
        for(Document document:mongoColletion.find(base).skip((page-1)*20).limit(20)){
            jsonAppendString.append(document.toJson()).append(",");
            flag = true;
        }
        if (flag==true){
            String jsonStrTemp = jsonAppendString.substring(0,jsonAppendString.length()-1);
            return jsonStrTemp+"]}";
        }
        return jsonAppendString+"]}";
    }

    public static void main(String[] args) {
        String str = "(bang=1)";
        String [] str2 = str.split("7");
        System.out.println(str2.length);
    }
}
