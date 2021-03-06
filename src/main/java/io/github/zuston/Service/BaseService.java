package io.github.zuston.Service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.zuston.Bean.ConditionBean;
import io.github.zuston.Bean.ConditionsBean;
import io.github.zuston.Helper.DbHelper;
import io.github.zuston.Helper.RedisHelper;
import io.github.zuston.MipCore.CoreConditionGenerator;
import io.github.zuston.MipCore.CoreExpressionDecoder;
import io.github.zuston.Util.*;
import io.github.zuston.Util.Mapper.ErrorMapper;
import io.github.zuston.Util.Mapper.RaceMapper;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import static io.github.zuston.MipCore.CoreExpressionDecoder.indexArr;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Created by zuston on 17-2-22.
 */
public class BaseService {

    public static MongoDatabase mongoDataBase = DbHelper.getInstance();
    public static MongoCollection<Document> mongoColletion = mongoDataBase.getCollection("vasp_input");

    public static MongoCollection<Document> duplicateColletion = mongoDataBase.getCollection("poscar_uniqueness_info");

    public static MongoCollection<Document> extraColletion = mongoDataBase.getCollection("extract");

    public static MongoCollection<Document> latestBasicCollection = mongoDataBase.getCollection("pfsas_0426");

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


    public static String basicInfoFunction(String expression, int page,int flag) {
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,1);
        System.out.println(basicDBObject);
        return BaseService.resAppend(basicDBObject,page,expression);
    }


    public static String getInfo(String expression, int page,int flag) {

        /**
         * 过渡办法
         */
        if (expression.indexOf("ve")>=0&&expression.indexOf("es")>=0){
            ArrayList<String> a = new ArrayList<String>(Arrays.asList(expression.split("&")));
            String es = "";
            String ve = "";
            for (String i:a){
                if (i.indexOf("es")>=0){
                    es = i.split("=")[1];
                    if (es.indexOf(")")>=0){
                        es = es.substring(0,es.length()-1);
                    }
                    continue;
                }
                if (i.indexOf("ve")>=0){
                    ve = i.split("=")[1];
                    if (ve.indexOf(")")>=0){
                        ve = ve.substring(0,ve.length()-1);
                    }
                }
            }
            System.out.println(ve);
            System.out.println(es);
            return getBiliInfo(es,ve,page,flag);
        }


        ArrayList<String> expressionAnalyArr = CoreExpressionDecoder.simpleAnaly(expression);
        if (expressionAnalyArr!=null&&expressionAnalyArr.size()>1){
            BasicDBObject base = new BasicDBObject();
            BasicDBList co = new BasicDBList();
            for (String str:expressionAnalyArr){
                co.add(getInfoComplex(str,page,flag));
            }

            base.put("$or",co);
            return BaseService.resAppend(base,page,expression);
        }
        if (expressionAnalyArr==null) return ErrorMapper.FormatError();

        return BaseService.resAppend(BaseService.getInfoComplex(expression,page,flag),page,expression);
    }


    /**
     * 增加族系复合搜索
     * @param expression
     * @param page
     * @return
     */
    private static BasicDBObject getInfoComplex(String expression,int page,int flag){
        System.out.println(expression);
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
            }else if(temp.indexOf("es")>=0){
                /**
                 *
                 * 增加的表达式解析
                 */
                String esValue = temp.split("=")[1];
                esValue = String.valueOf(esValue.substring(0, esValue.length()-1));
                System.out.println(esValue);
            }else if(temp.indexOf("ve")>=0){
                String veValue = temp.split("=")[1];
                veValue = String.valueOf(veValue.substring(0,veValue.length()-1));
                System.out.println(veValue);
            }else{
                andShackList.add(temp);
            }
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
        condition.put("type","distinct");
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

    /**
     * 根据condition，查找出结果拼接成 json string
     * @param base
     * @param page
     * @return
     */
    private static String resAppend(BasicDBObject base,int page,String expression){

        String redisJson = RedisHelper.getString(expression+"-"+String.valueOf(page));
        System.out.println(expression+"-"+String.valueOf(page));
        if (!redisJson.equals("error")){
            System.out.println("命中json");
            return redisJson;
        }

        if (base==null){
            return ErrorMapper.ElementLackError();
        }
        // TODO: 17/4/15 可以将优化结果存入redis中
        long totalCount = 0;
        long redisCount = RedisHelper.getInt(expression);
        if (redisCount==-1){
            long time = System.currentTimeMillis();
            totalCount = mongoColletion.count(base);
            System.out.println("统计耗时:"+(System.currentTimeMillis()-time));
            RedisHelper.set(expression,String.valueOf(totalCount));
        }else{
            System.out.println("命中缓存");
            totalCount = redisCount;
        }

        if (totalCount==0){
            return ErrorMapper.NoDataError();
        }

        StringBuilder jsonAppendString = new StringBuilder("{\"cpage\":");
        jsonAppendString.append(page);

        jsonAppendString.append(",\"c\":[");

        boolean flag = false;

        long time1 = System.currentTimeMillis();
        for(Document document:mongoColletion.find(base).skip((page-1)*10).limit(10)){
            String id = (String) document.get("m_id");

            Document dumplicate = null;
            /**
             * 获取查重表中的数据
             */
            for (Document dd:duplicateColletion.find(new BasicDBObject("original_id",id)).limit(1)){
                dumplicate = dd;
            }
            /**
             * 赶时间暂且这么处理
             */
            if(dumplicate!=null){
                String spaceGroup = (String) dumplicate.get("space_group_type");
                String compound_name = (String) dumplicate.get("compound_name");
                DecimalFormat    df   = new DecimalFormat("######0.00");
                String atomic_average_mass =  String.valueOf(df.format(dumplicate.getDouble("atomic_average_mass")));
                String simplified_name = (String) dumplicate.get("simplified_name");
                String initStr = document.toJson().substring(0,document.toJson().length()-1);

                String computedString = "";

                /**
                 * 查询是否是计算数据
                 */
                Pattern pattern = Pattern.compile("^" + id + ".*$", Pattern.CASE_INSENSITIVE);
                long computedValue = extraColletion.count(new BasicDBObject("m_id",new BasicDBObject("$regex",pattern)).append("source_path","static.test_scan"));
                if (computedValue>0) {
                    computedString = ",\"computed\":" + 1 + "";
                }else{
                    computedString = ",\"computed\":" + 0 + "";
                }

                String endStr = initStr+computedString+",\"spaceGroup\":\""+spaceGroup+"\",\"atomic_average_mass\":\"" + atomic_average_mass + "\",\"simplified_name\":\"" + simplified_name+ "\",\"compound_name\":\"" + compound_name+ "\"}";
                jsonAppendString.append(endStr).append(",");
                flag = true;
            }
        }

        System.out.println("skip获取:"+(System.currentTimeMillis()-time1));
        StringBuilder dataSuffix = new StringBuilder();
        if (flag==true){
            String jsonStrTemp = jsonAppendString.substring(0,jsonAppendString.length()-1);
            dataSuffix.append(jsonStrTemp+"]");
        }else{
            dataSuffix.append(jsonAppendString+"]");
        }
        dataSuffix.append(",\"count\":");
        dataSuffix.append(totalCount);
        dataSuffix.append("}");
        RedisHelper.set(expression+"-"+String.valueOf(page),dataSuffix.toString());
        return dataSuffix.toString();
    }

    public static String getBiliInfo(String bili, String biliNumber, int page, int flag) {
        String biliContent = bili;
        if (biliNumber.equals("")){
            BasicDBObject condition = new BasicDBObject();
            condition.put("atomic_numbers_ratio",new BasicDBObject().append("$eq",biliContent));
            return biliResAppend(condition,page,flag);
        }
        String [] biliNumberArr = biliNumber.split("\\|");
        BasicDBObject base = new BasicDBObject();

        BasicDBList co = new BasicDBList();
        for (String number:biliNumberArr){
            co.add(getBiliConditions(biliContent,number));
        }
        base.put("$or",co);
        return biliResAppend(base,page,flag);
    }

    private static String biliResAppend(BasicDBObject base, int page,int computeFlag) {
        if (base==null){
            return ErrorMapper.ElementLackError();
        }
        long totalCount = duplicateColletion.count(base);
        if (totalCount==0){
            return ErrorMapper.NoDataError();
        }

        StringBuilder jsonAppendString = new StringBuilder("{\"cpage\":");
        jsonAppendString.append(page);

        jsonAppendString.append(",\"c\":[");

        boolean flag = false;
        for(Document document:duplicateColletion.find(base).skip((page-1)*10).limit(10)){

            String id = (String) document.get("original_id");

            Document vasp = null;
            /**
             * 获取查重表中的数据
             */
            for (Document dd:mongoColletion.find(new BasicDBObject("m_id",id)).limit(1)){
                vasp = dd;
            }

            if(vasp!=null){
                String spaceGroup = (String) document.get("space_group_type");
                Integer ves = (Integer) document.get("element_valence_electrons_sum");
                String compound_name = (String) document.get("compound_name");
                DecimalFormat    df   = new DecimalFormat("######0.00");
                String atomic_average_mass =  String.valueOf(df.format(document.getDouble("atomic_average_mass")));
                String simplified_name = (String) document.get("simplified_name");
                String initStr = vasp.toJson().substring(0,vasp.toJson().length()-1);

                String computedString = "";
                /**
                 * 查询是否是计算数据
                 */
                Pattern pattern = Pattern.compile("^" + id + ".*$", Pattern.CASE_INSENSITIVE);
                long computedValue = extraColletion.count(new BasicDBObject("m_id",new BasicDBObject("$regex",pattern)).append("source_path","static.test_scan"));
                /**
                 * computeFlag==0 时为全部数据
                 * ==1 为计算数据抽取
                 * 但是结果都需要计算和全部数据标识
                 */
                if (computeFlag==0){
                    if (computedValue>0) {
                        computedString = ",\"computed\":" + 1 + "";
                    }else{
                        computedString = ",\"computed\":" + 0 + "";
                    }
                }else{
                    if (computedValue>0){
                        computedString = ",\"computed\":" + 1 + "";
                    }else{
                        computedString = ",\"computed\":" + 0 + "";
                    }
                }
                String endStr = initStr+computedString+",\"spaceGroup\":\""+spaceGroup+"\",\"ves\":\"" + String.valueOf(ves) + "\",\"atomic_average_mass\":\"" + atomic_average_mass + "\",\"simplified_name\":\"" + simplified_name+ "\",\"compound_name\":\"" + compound_name+ "\"}";
                System.out.println(endStr);
                jsonAppendString.append(endStr).append(",");
                flag = true;
            }
        }
        StringBuilder dataSuffix = new StringBuilder();
        if (flag==true){
            String jsonStrTemp = jsonAppendString.substring(0,jsonAppendString.length()-1);
            dataSuffix.append(jsonStrTemp+"]");
        }else{
            dataSuffix.append(jsonAppendString+"]");
        }
        dataSuffix.append(",\"count\":");
        dataSuffix.append(totalCount);
        dataSuffix.append("}");
        return dataSuffix.toString();
    }

    private static BasicDBObject getBiliConditions(String biliContent, String number) {
        BasicDBObject condition = new BasicDBObject();
        condition.put("atomic_numbers_ratio",new BasicDBObject().append("$eq",biliContent));
        condition.put("element_valence_electrons_sum",new BasicDBObject().append("$eq",Integer.valueOf(number)));
        return condition;
    }


    public static String getComplexInfo(String id) {
        BasicDBObject condition = new BasicDBObject();
        condition.put("_id", new ObjectId(id));
//        condition.put("type","distinct");
        return complexInfo(condition);
    }

    private static String complexInfo(BasicDBObject condition) {
        StringBuilder jsonAppendString = new StringBuilder("{\"vasp\":");
//        condition.put("type",new BasicDBObject("$exists",true));
        for(Document document:mongoColletion.find(condition).limit(1)){
            String id = (String) document.get("m_id");

            Document dumplicate = null;
            /**
             * 获取查重表中的数据
             */
            for (Document dd:duplicateColletion.find(new BasicDBObject("original_id",id)).limit(1)){
                dumplicate = dd;
            }

            Document extract = null;
            Pattern pattern = Pattern.compile("^" + id + ".*$", Pattern.CASE_INSENSITIVE);
            for (Document document1:extraColletion.find(new BasicDBObject("m_id",new BasicDBObject("$regex",pattern)).append("source_path","static.test_scan")).limit(1)){
                extract = document1;
            }

            System.out.println(dumplicate);
            System.out.println(extract);
            jsonAppendString.append(document.toJson());

            if(dumplicate!=null){
                jsonAppendString.append(",\"dumplicate\":");
                jsonAppendString.append(dumplicate.toJson());
            }
            if (extract!=null){
                jsonAppendString.append(",\"extract\":");
                jsonAppendString.append(extract.toJson());
            }
        }
        jsonAppendString.append("}");
        return jsonAppendString.toString();
    }

    public static String downloadBiliInfo(String bili, String biliNumber) throws IOException, NoSuchAlgorithmException {
        String biliContent = bili;
        String [] biliNumberArr = biliNumber.split("\\|");
        BasicDBObject base = new BasicDBObject();
        BasicDBList co = new BasicDBList();
        for (String number:biliNumberArr){
            co.add(getBiliConditions(biliContent,number));
        }
        base.put("$or",co);
        return excelGenerate(base);
    }

    private static String excelGenerate(BasicDBObject base) throws IOException, NoSuchAlgorithmException {
        ArrayList<LinkedHashMap<String,String>> container = new ArrayList<LinkedHashMap<String, String>>();
        for (Document document:duplicateColletion.find(base)){
            String id = (String) document.get("original_id");

            Document vasp = null;
            for (Document dd:mongoColletion.find(new BasicDBObject("m_id",id)).limit(1)){
                vasp = dd;
            }
            if (vasp!=null){
                LinkedHashMap<String,String> hm = new LinkedHashMap<String, String>();
                hm.put("original_id", (String) document.get("original_id"));
                hm.put("化合物名称", (String) document.get("compound_name"));
                hm.put("空间群", (String) document.get("space_group_type"));
                hm.put("element_simple_name",(String) document.get("simplified_name"));
                hm.put("element_valence_electrons_sum", String.valueOf(document.get("element_valence_electrons_sum")));
                hm.put("space_group_type_num",String.valueOf(document.get("space_group_type_num")));
                container.add(hm);
            }
        }
        return ExcelUtil.excelGenerateToFile(container);
    }

    public static String getJSmolInfo(String idd){
        String id = (String) mongoColletion.find(new BasicDBObject("_id",new ObjectId(idd))).limit(1).first().get("m_id");

        Pattern pattern = Pattern.compile("^" + id + ".*$", Pattern.CASE_INSENSITIVE);
        Document document = extraColletion.find(new BasicDBObject("m_id",new BasicDBObject("$regex",pattern)).append("source_path","static.test_scan")).limit(1).first();
        if (document==null){
            return "error";
        }
        String filename = (String) document.get("m_id");
        String suffixFilename = (String) ((Document)document.get("poscar_static")).get("system_type");
        System.out.println("checking...");
//        String mainPath = "/Volumes/TOSHIBA EXT/xyl/"+suffixFilename+"/"+filename+"/Static/test_scan/POSCAR";

        //部署正式路径
        String mainPath = "/opt/openresty/nginx/html/static/xyl/"+suffixFilename+"/"+filename+"/Static/test_scan/POSCAR";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        String str1 = "";
        BufferedReader br = null;
        try {
            String str = "";
            String path = mainPath;
            System.out.println(path);
            fis = new FileInputStream(path);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                str1 += str + "\r\n";
            }
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件");
        } catch (IOException e) {
            System.out.println("读取文件失败");
        } finally {
            try {
                br.close();
                isr.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str1;
    }

}
