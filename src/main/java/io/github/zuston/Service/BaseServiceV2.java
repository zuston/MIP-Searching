package io.github.zuston.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import io.github.zuston.Helper.ZipHelper;
import io.github.zuston.Listener.DbInitListener;
import io.github.zuston.Util.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;


/**
 * Created by zuston on 17/5/2.
 */
public class BaseServiceV2 {

    public final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseServiceV2.class);

    public static MongoCollection<Document> latestBasicCollection = DbInitListener.latestBasicCollection;
    public static MongoCollection<Document> extraColletion = DbInitListener.extraColletion;
    public static MongoCollection<Document> caculateMetaCollection = DbInitListener.caculateMetaCollection;
    public static MongoCollection<Document> smallFileCollection = DbInitListener.smallFileCollection;

    public static String basicInfoFunction(String expression, int page,int flag) {
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        logger.info("===================");
        return basicInfoAppendFunction(basicDBObject,page,expression,flag);
    }

    public static String basicGetAllCalculate(String expression,int flag){
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        logger.info("===================");
        return basicAllCalculateFunction(basicDBObject,expression,flag);
    }

    public static String basicGetRandomCalculate(String expression,int flag){
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        logger.info("===================");
        return basicRandomCalculateFunction(basicDBObject,expression,flag);
    }

    private static String basicRandomCalculateFunction(BasicDBObject base, String expression, int tag) {

        boolean randomFlag = true;
        if (base==null){
            return ErrorMapper.ElementLackError();
        }
        long totalCount = 0;
        long redisCount = RedisUtil.getSearchCount(expression);
        if (redisCount==-1){
            long time = System.currentTimeMillis();
            totalCount = latestBasicCollection.count(base);

            randomFlag = totalCount>300?true:false;

            logger.info("未命中缓存，统计耗时:{}",System.currentTimeMillis()-time);

            RedisUtil.setSearchCount(expression+"-"+String.valueOf(tag),String.valueOf(totalCount));
        }else{
            logger.info("命中缓存");
            totalCount = redisCount;
        }

        if (totalCount==0){
            return ErrorMapper.NoDataError();
        }

        StringBuilder jsonAppendString = new StringBuilder("{\"count\":");
        jsonAppendString.append(totalCount);

        jsonAppendString.append(",\"c\":[");

        boolean flag = false;

        long time1 = System.currentTimeMillis();

        int randomSum = 300;
        for(Document document:latestBasicCollection.find(base)){

            if (randomFlag){
                if (randomSum<=0){
                    break;
                }
                int randomValue = ((int) (Math.random()*1000))%2;

                if (randomValue==0){
                    continue;
                }
                randomSum--;
            }

            String id = (String) document.get("original_id");

            if(true){

                String endStr = "{\"original_id\":\"" +  id + "\"}";
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
        dataSuffix.append("}");
        return dataSuffix.toString();
    }

    private static String basicAllCalculateFunction(BasicDBObject base, String expression, int tag) {

        if (base==null){
            return ErrorMapper.ElementLackError();
        }
        long totalCount = 0;
        long redisCount = RedisUtil.getSearchCount(expression);
        if (redisCount==-1){
            long time = System.currentTimeMillis();
            totalCount = latestBasicCollection.count(base);

            logger.info("未命中缓存，统计耗时:{}",System.currentTimeMillis()-time);

            RedisUtil.setSearchCount(expression+"-"+String.valueOf(tag),String.valueOf(totalCount));
        }else{
            logger.info("命中缓存");
            totalCount = redisCount;
        }

        if (totalCount==0){
            return ErrorMapper.NoDataError();
        }

        StringBuilder jsonAppendString = new StringBuilder("{\"count\":");
        jsonAppendString.append(totalCount);

        jsonAppendString.append(",\"c\":[");

        boolean flag = false;

        long time1 = System.currentTimeMillis();
        for(Document document:latestBasicCollection.find(base)){
            String id = (String) document.get("original_id");

            if(true){

                String endStr = "{\"original_id\":\"" +  id + "\"}";
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
        dataSuffix.append("}");
        return dataSuffix.toString();
    }

    /**
     * 根据condition，查找出结果拼接成 json string
     * @param base
     * @param page
     * @return
     */
    private static String basicInfoAppendFunction(BasicDBObject base,int page,String expression,int tag){

//        String redisJson = RedisUtil.getSearchJson(expression+"-"+String.valueOf(page)+"-"+String.valueOf(tag));
//        if (!redisJson.equals("error")){
//            System.out.println("命中json");
//            return redisJson;
//        }

        long dataBaseCount = latestBasicCollection.count();

        if (base==null){
            return ErrorMapper.ElementLackError();
        }
        // TODO: 17/4/15 可以将优化结果存入redis中
        long totalCount = 0;
//        long redisCount = RedisUtil.getSearchCount(expression);
        long redisCount = -1;
        if (redisCount==-1){
            long time = System.currentTimeMillis();
            totalCount = latestBasicCollection.count(base);
//            RedisUtil.setSearchCount(expression+"-"+String.valueOf(tag),String.valueOf(totalCount));
            logger.info("未命中缓存,统计耗时:{}",System.currentTimeMillis()-time);
        }else{
            logger.info("命中缓存");
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
        for(Document document:latestBasicCollection.find(base).skip((page-1)*30).limit(30)){
            String id = (String) document.get("original_id");

            if(true){
                String spaceGroup = (String) document.get("space_group_type");
                String compound_name = (String) document.get("formula");
                DecimalFormat df   = new DecimalFormat("######0.00");
                String atomic_average_mass =  String.valueOf(df.format(document.getDouble("atomic_average_mass")));
                String simplified_name = (String) document.get("simplified_name");
//                String initStr = document.toJson().substring(0,document.toJson().length()-1);

//                String computedString = ",\"computed\":" +  document.get("is_computed") + "";

                String bandGap = "";
                String extractJobIdStr = "";
                Document extract = null;

                if (tag<2){
                    ArrayList<String> idList = new ArrayList<String>();
                    Pattern pattern = Pattern.compile("^" + id + ".*$", Pattern.CASE_INSENSITIVE);
                    int count = 0;
                    for (Document document1:extraColletion.find(new BasicDBObject("m_id",new BasicDBObject("$regex",pattern)).append("source_path","scf.test_scan"))){
                        if (count==0){
                            // 只取第一个数的计算结果bandGap
                            extract = document1;
                        }
                        idList.add(document1.get("_id").toString());
                        count++;
                    }

                    if (extract!=null){
                        bandGap = ",\"bandgap\":" +  ((Document)extract.get("extract_info")).get("band_gap") + "";
                        extractJobIdStr += ",\"jobs\":[";
                        for (String ooid:idList){
                            extractJobIdStr += "\""+ooid+"\",";
                        }
                        extractJobIdStr = extractJobIdStr.substring(0,extractJobIdStr.length()-1);
                        extractJobIdStr += "]";
                    }
                }

                String computedString = ",\"computed\":" +  (extract!=null?1:0) + "";

                String original_id = ",\"original_id\":\"" +  id + "\"";
                String oid = ",\"id\":\"" +  document.get("_id") + "\"";
                String initStr = "{\"init\":" +  1 + "";

                String endStr = initStr+extractJobIdStr+original_id+oid+bandGap+computedString+",\"spaceGroup\":\""+spaceGroup+"\",\"atomic_average_mass\":\"" + atomic_average_mass + "\",\"simplified_name\":\"" + simplified_name+ "\",\"compound_name\":\"" + compound_name+ "\"}";
                jsonAppendString.append(endStr).append(",");
                flag = true;
            }
        }

        logger.info("skip获取所用时间:{}",System.currentTimeMillis()-time1);
        StringBuilder dataSuffix = new StringBuilder();
        if (flag==true){
            String jsonStrTemp = jsonAppendString.substring(0,jsonAppendString.length()-1);
            dataSuffix.append(jsonStrTemp+"]");
        }else{
            dataSuffix.append(jsonAppendString+"]");
        }
        dataSuffix.append(",\"count\":");
        dataSuffix.append(totalCount);
        dataSuffix.append(",\"allCount\":");
        dataSuffix.append(dataBaseCount);
        dataSuffix.append("}");
//        RedisUtil.setSearchJson(expression+"-"+String.valueOf(page),dataSuffix.toString());
        return dataSuffix.toString();
    }

    /**
     * 生成excel，再转为流输出下载
     * 临时目录，需要设置 777 权限
     * @param res
     * @param expression
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void basicExcelDownloadFunction(HttpServletResponse res,String expression,int flag) throws IOException, NoSuchAlgorithmException {
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        byte[] valueByte = excelGenerate(basicDBObject);
        FileDownLoadUtil.generateDownloadResponseByBytes(valueByte,res,"excelResults.xls");
    }


    private static byte[] excelGenerate(BasicDBObject base) throws IOException, NoSuchAlgorithmException {
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
        return ExcelGenerate.excelGenerateToByte(container);
    }

    public static String basicDetailInfoFunction(String id) {
        BasicDBObject condition = new BasicDBObject();
        condition.put("_id", new ObjectId(id));
        logger.info("===================");
        return detailInfoById(condition);
    }

    private static String detailInfoById(BasicDBObject condition) {
        StringBuilder jsonAppendString = new StringBuilder("{\"basic\":");
        for(Document document:extraColletion.find(condition).limit(1)){
            String mid = (String) document.get("m_id");

            ObjectId calculateMetaId = (ObjectId)document.get("caculate_meta_id");
            Document calculateDoc = caculateMetaCollection.find(new BasicDBObject("_id",calculateMetaId)).limit(1).first();
            String jobId = String.valueOf(calculateDoc.get("jobid"));

            String original_id = mid.split("-")[0]+"-"+mid.split("-")[1];

            Document basic = null;
            for (Document document1:latestBasicCollection.find(new BasicDBObject("original_id",original_id)).limit(1)){
                basic = document1;
            }

            jsonAppendString.append(basic.toJson());

            if (basic!=null){
                jsonAppendString.append(",\"extract\":");
                jsonAppendString.append(document.toJson());
                jsonAppendString.append(",\"jobid\":"+jobId);
            }
        }
        jsonAppendString.append("}");
        return jsonAppendString.toString();
    }

    /**
     * 老版本，直接从文件中读取来渲染jsmol
     * @param idd
     * @return
     */
    public static String basicJsmolFunction(String idd){
        Document document = extraColletion.find(new BasicDBObject("_id",new ObjectId(idd)).append("source_path","static.test_scan")).limit(1).first();
        if (document==null){
            return "error";
        }

        String filename = (String) document.get("m_id");
        String suffixFilename = (String) ((Document)document.get("extract_info")).get("system_type");
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

    /**
     * 从 MongoDb中直接获取poscar文件来渲染jsmol
     * @param idd
     * @return
     */
    public static String basicJsmolFunctionFromMongoDb(String idd){
        String caculateMetaId = (String) extraColletion.find(new BasicDBObject("_id",new ObjectId(idd))).limit(1).first().get("caculate_meta_id").toString();
        logger.info("渲染jsmol[caculateMetaId:{}]",caculateMetaId);
        logger.info("===================");
        return getStringFromMongo(caculateMetaId,"bposcar");
    }

    private static String getStringFromMongo(String caculateMetaId,String columnName){
        Document document = caculateMetaCollection.find(new BasicDBObject("_id",new ObjectId(caculateMetaId))).limit(1).first();
        if (document==null){
            return "error";
        }

        Document one = (Document) document.get("childs");
        Document two = (Document) one.get("scf");
        Document three = (Document) two.get("childs");
        Document four = (Document) three.get("test_scan");
        Document five = (Document) four.get("childs");
        Document six = (Document) five.get(columnName);
        String fid = six.get("f_id").toString();

        Document document1 = smallFileCollection.find(new BasicDBObject("_id",new ObjectId(fid))).limit(1).first();

        Binary bb = (Binary) document1.get("data");
        byte[] res = bb.getData();
        return new String(res);
    }

    /**
     * 旧版直接从nginx的静态资源库中读取
     * 新版直接从mongo中读取
     * poscar static relax 文件下载
     */
    public static void basicFileDownloadFunction(HttpServletResponse res,String extractId,String columnName){
        String caculateMetaId = (String) extraColletion.find(new BasicDBObject("_id",new ObjectId(extractId))).limit(1).first().get("caculate_meta_id").toString();
        String v = getStringFromMongo(caculateMetaId,columnName);
        FileDownLoadUtil.generateDownloadResponseByString(v,res,columnName);
    }

    /**
     * 图片从库中读取
     * @param response
     * @param jobid
     * @param type
     */
    public static void basicImgLoad(HttpServletResponse response, String jobid, int type) throws IOException {
        Document document = caculateMetaCollection.find(new BasicDBObject("jobid",Integer.valueOf(jobid))).limit(1).first();
        Document one = (Document) document.get("childs");


        String columnName = type==1?"bsimg_png":"dosimg_png";
        Document six = (Document) one.get(columnName);
        String fid = six.get("f_id").toString();
        System.out.println(fid);
        Document document1 = smallFileCollection.find(new BasicDBObject("_id",new ObjectId(fid))).limit(1).first();

        Binary bb = (Binary) document1.get("data");

        response.setContentType("image/*"); // 设置返回的文件类型
        OutputStream toClient = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
        toClient.write(bb.getData()); // 输出数据
        toClient.close();
    }

    /**
     * 计算结果中，poscar下载
     * @param response
     * @param expression
     * @param flag
     */
    public static void basicPoscarDownloadFunction(HttpServletResponse response, String expression, int flag) throws IOException {
        String zipName = "poscarResults.zip";
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition","attachment; filename="+zipName);
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        try {
            LinkedHashMap<String,byte[]> hm = getPoscarFromExpression(expression,flag);
            ArrayList<byte[]> poscarList = new ArrayList<byte[]>();
            ArrayList<String> nameList = new ArrayList<String>();
            for (Map.Entry<String,byte[]> mm:hm.entrySet()){
                poscarList.add(mm.getValue());
                nameList.add(mm.getKey());
            }
            ZipHelper.doCompress(poscarList,nameList,out);
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            out.close();
        }
    }

    private static LinkedHashMap<String,byte[]> getPoscarFromExpression(String expression,int flag){
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        ArrayList<byte[]> res = new ArrayList<byte[]>();
        LinkedHashMap<String,byte[]> result = new LinkedHashMap<String, byte[]>();
        for (Document document:latestBasicCollection.find(basicDBObject)){
            String valuePoscar = (String) document.get("poscar");
            String name = (String) document.get("simplified_name");
            String objectid = (String) document.get("_id").toString();
            result.put(name+"-"+objectid,valuePoscar.getBytes());
        }
        return result;
    }
}
