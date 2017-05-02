package io.github.zuston.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import io.github.zuston.Util.CoreConditionGenerator;
import io.github.zuston.Util.ErrorMapper;
import io.github.zuston.Util.ExcelGenerate;
import io.github.zuston.Util.RedisUtil;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import static io.github.zuston.Service.BaseService.mongoDataBase;

/**
 * Created by zuston on 17/5/2.
 */
public class BaseServiceV2 {
    public static MongoCollection<Document> latestBasicCollection = mongoDataBase.getCollection("pfsas_0426");
    public static MongoCollection<Document> extraColletion = mongoDataBase.getCollection("extract");

    public static String basicInfoFunction(String expression, int page,int flag) {
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression);
        return basicInfoAppendFunction(basicDBObject,page,expression);
    }

    /**
     * 根据condition，查找出结果拼接成 json string
     * @param base
     * @param page
     * @return
     */
    private static String basicInfoAppendFunction(BasicDBObject base,int page,String expression){

        String redisJson = RedisUtil.getSearchJson(expression+"-"+String.valueOf(page));
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
        long redisCount = RedisUtil.getSearchCount(expression);
        if (redisCount==-1){
            long time = System.currentTimeMillis();
            totalCount = latestBasicCollection.count(base);
            System.out.println("统计耗时:"+(System.currentTimeMillis()-time));
            RedisUtil.setSearchCount(expression,String.valueOf(totalCount));
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
        for(Document document:latestBasicCollection.find(base).skip((page-1)*10).limit(10)){
            String id = (String) document.get("original_id");

            if(true){
                String spaceGroup = (String) document.get("space_group_type");
                String compound_name = (String) document.get("formula");
                DecimalFormat df   = new DecimalFormat("######0.00");
                String atomic_average_mass =  String.valueOf(df.format(document.getDouble("atomic_average_mass")));
                String simplified_name = (String) document.get("simplified_name");
                String initStr = document.toJson().substring(0,document.toJson().length()-1);


                String computedString = ",\"computed\":" +  document.get("is_computed") + "";

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
        RedisUtil.setSearchJson(expression+"-"+String.valueOf(page),dataSuffix.toString());
        return dataSuffix.toString();
    }


    public static String basicDownloadFunction(String expression) throws IOException, NoSuchAlgorithmException {
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression);
        return excelGenerate(basicDBObject);
    }


    private static String excelGenerate(BasicDBObject base) throws IOException, NoSuchAlgorithmException {
        ArrayList<LinkedHashMap<String,String>> container = new ArrayList<LinkedHashMap<String, String>>();
        for (Document document:latestBasicCollection.find(base)){
            String id = (String) document.get("original_id");

            if (true){
                LinkedHashMap<String,String> hm = new LinkedHashMap<String, String>();
                hm.put("original_id", (String) document.get("original_id"));
                hm.put("formula", (String) document.get("formula"));
                hm.put("spacegroup", (String) document.get("space_group_type"));
                hm.put("element_simple_name",(String) document.get("simplified_name"));
                hm.put("atomic_average_mass", String.valueOf(document.get("atomic_average_mass")));
                hm.put("space_group_type_num",String.valueOf(document.get("space_group_type_number")));
                container.add(hm);
            }
        }
        return ExcelGenerate.excelGenerate(container);
    }

    public static String basicDetailInfoFunction(String id) {
        BasicDBObject condition = new BasicDBObject();
        condition.put("_id", new ObjectId(id));
        return detailInfoById(condition);
    }

    private static String detailInfoById(BasicDBObject condition) {
        StringBuilder jsonAppendString = new StringBuilder("{\"basic\":");
        for(Document document:latestBasicCollection.find(condition).limit(1)){
            String id = (String) document.get("original_id");

            Document extract = null;
            Pattern pattern = Pattern.compile("^" + id + ".*$", Pattern.CASE_INSENSITIVE);
            for (Document document1:extraColletion.find(new BasicDBObject("m_id",new BasicDBObject("$regex",pattern)).append("source_path","static.test_scan")).limit(1)){
                extract = document1;
            }
            System.out.println(id);
            System.out.println(extract!=null);

            jsonAppendString.append(document.toJson());

            if (extract!=null){
                jsonAppendString.append(",\"extract\":");
                jsonAppendString.append(extract.toJson());
            }
        }
        jsonAppendString.append("}");
        return jsonAppendString.toString();
    }

    public static String basicJsmolFunction(String idd){
        String id = (String) latestBasicCollection.find(new BasicDBObject("_id",new ObjectId(idd))).limit(1).first().get("original_id");

        Pattern pattern = Pattern.compile("^" + id + ".*$", Pattern.CASE_INSENSITIVE);
        Document document = extraColletion.find(new BasicDBObject("m_id",new BasicDBObject("$regex",pattern)).append("source_path","static.test_scan")).limit(1).first();
        if (document==null){
            return "error";
        }

        String filename = (String) document.get("m_id");
        String suffixFilename = (String) ((Document)document.get("poscar_static")).get("system_type");
        System.out.println("checking...");
        String mainPath = "/Volumes/TOSHIBA EXT/xyl/"+suffixFilename+"/"+filename+"/Static/test_scan/POSCAR";

        //部署正式路径
//        String mainPath = "/opt/openresty/nginx/html/static/xyl/"+suffixFilename+"/"+filename+"/Static/test_scan/POSCAR";
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
