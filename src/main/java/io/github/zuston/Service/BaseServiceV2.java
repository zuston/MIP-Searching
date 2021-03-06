package io.github.zuston.Service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import io.github.zuston.Entity.JobInfoEntity;
import io.github.zuston.Helper.ZipHelper;
import io.github.zuston.Listener.DbInitListener;
import io.github.zuston.MipCore.CoreConditionGenerator;
import io.github.zuston.Tools.Notify.Impl.WechatNotifyTool;
import io.github.zuston.Util.Mapper.ErrorMapper;
import io.github.zuston.Util.ExcelUtil;
import io.github.zuston.Util.FileUtil;
import io.github.zuston.Helper.RedisHelper;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;


/**
 * Created by zuston on 17/5/2.
 */
@Service
public class BaseServiceV2 {

    public final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseServiceV2.class);

    public static MongoCollection<Document> latestBasicCollection = DbInitListener.latestBasicCollection;
    public static MongoCollection<Document> extraColletion = DbInitListener.extraColletion;
    public static MongoCollection<Document> caculateMetaCollection = DbInitListener.caculateMetaCollection;
    public static MongoCollection<Document> smallFileCollection = DbInitListener.smallFileCollection;
    public static MongoCollection<Document> jobInfoCollection = DbInitListener.jobInfoCollection;

    @Autowired
    private WechatNotifyTool wechatNotifyTool;

    public String basicSearch(String expression, int page,int flag, String owner){
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        logger.info("===================");
        wechatNotifyTool.send(new HashMap<>());
        return basicInfoAppendFunction(basicDBObject,page,expression,flag, owner);
    }

    public String basicGetAllCalculate(String expression,int flag){
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        logger.info("===================");
        return basicAllCalculateFunction(basicDBObject,expression,flag);
    }

    public String basicGetRandomCalculate(String expression,int flag){
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        logger.info("===================");
        return basicRandomCalculateFunction(basicDBObject,expression,flag);
    }

    private String basicRandomCalculateFunction(BasicDBObject base, String expression, int tag) {

        boolean randomFlag = true;
        if (base==null){
            return ErrorMapper.ElementLackError();
        }
        long totalCount = 0;
        long redisCount = RedisHelper.getInt(expression);
        if (redisCount==-1){
            long time = System.currentTimeMillis();
            totalCount = latestBasicCollection.count(base);

            randomFlag = totalCount>300?true:false;

            logger.info("未命中缓存，统计耗时:{}",System.currentTimeMillis()-time);

            RedisHelper.set(expression+"-"+String.valueOf(tag),String.valueOf(totalCount));
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

    private String basicAllCalculateFunction(BasicDBObject base, String expression, int tag) {

        if (base==null){
            return ErrorMapper.ElementLackError();
        }
        long totalCount = 0;
        long redisCount = RedisHelper.getInt(expression);
        if (redisCount==-1){
            long time = System.currentTimeMillis();
            totalCount = latestBasicCollection.count(base);

            logger.info("未命中缓存，统计耗时:{}",System.currentTimeMillis()-time);

            RedisHelper.set(expression+"-"+String.valueOf(tag),String.valueOf(totalCount));
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
     * @param owner
     * @return
     */
    private String basicInfoAppendFunction(BasicDBObject base, int page, String expression, int tag, String owner){

//        String redisJson = RedisHelper.getSearchJson(expression+"-"+String.valueOf(page)+"-"+String.valueOf(tag));
//        if (!redisJson.equals("error")){
//            logger.info("命中json");
//            return redisJson;
//        }

        int dataBaseCount = RedisHelper.getInt("allCount");
        if (dataBaseCount==-1){
            logger.info("未命中数目");
            dataBaseCount = (int) latestBasicCollection.count();
            RedisHelper.set("allCount",dataBaseCount+"");
        }

        if (base==null){
            return ErrorMapper.ElementLackError();
        }
        // TODO: 17/4/15 可以将优化结果存入redis中
        long totalCount = 0;
//        long redisCount = RedisHelper.getInt(expression+"-"+String.valueOf(tag));
        long redisCount = -1;
        if (redisCount==-1){
            long time = System.currentTimeMillis();
            totalCount = latestBasicCollection.count(base);
//            RedisHelper.set(expression+"-"+String.valueOf(tag),String.valueOf(totalCount));
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
                    List<JobInfoEntity> jobInfoEntityList = new ArrayList<>();
                    Pattern pattern = Pattern.compile("^" + id + ".*$", Pattern.CASE_INSENSITIVE);
                    int count = 0;
                    for (Document document1:extraColletion.find(new BasicDBObject("m_id",new BasicDBObject("$regex",pattern)).append("source_path","scf.test_scan"))){
                        if (count==0){
                            // 只取第一个数的计算结果bandGap
                            extract = document1;
                        }
                        count++;

                        String extractId = document1.get("_id").toString();
                        // 获取 jobId 来查找 jobinfo 的具体信息
                        ObjectId calculateMetaId = (ObjectId)document1.get("caculate_meta_id");
                        Document calculateDoc = caculateMetaCollection.find(new BasicDBObject("_id",calculateMetaId)).limit(1).first();

                        try {
                            String jobId = String.valueOf(calculateDoc.get("jobid"));

                            Document jobDoc = jobInfoCollection.find(new BasicDBObject("jobid",jobId)).limit(1).first();
                            if (jobDoc!=null) {
                                if (jobDoc.getString("owner").trim().equals(owner)){
                                    JobInfoEntity entity = new JobInfoEntity();
                                    entity.objectId = jobDoc.get("_id").toString();
                                    entity.calMethod = jobDoc.getString("calMethod");
                                    entity.calServer = jobDoc.getString("calServer");
                                    entity.create_time = jobDoc.getString("create_time");
                                    entity.finish_time = jobDoc.getString("finish_time");
                                    entity.owner = jobDoc.getString("owner");
                                    entity.jobid = jobDoc.getString("jobid");
                                    entity.extraid = extractId;
                                    jobInfoEntityList.add(entity);
                                }
                            }
                        }catch (Exception e){
                            logger.warn(e.getMessage());
                        }

                    }

                    if (extract!=null){
                        try {
                            Document extractDoc = ((Document)extract.get("extract_info"));
                            bandGap = ",\"bandgap\":" +  ((Document)extract.get("extract_info")).get("band_gap") + "";
                            extractJobIdStr += ",\"jobs\":[";
                            for (JobInfoEntity entity : jobInfoEntityList) {
                                extractJobIdStr += String.format( "{\"oid\":\"%s\",\"calMethod\":\"%s\",\"calServer\":\"%s\",\"create_time\":\"%s\",\"finish_time\":\"%s\",\"owner\":\"%s\",\"jobid\":\"%s\",\"extractid\":\"%s\"},",
                                        entity.objectId,
                                        entity.calMethod,
                                        entity.calServer,
                                        entity.create_time,
                                        entity.finish_time,
                                        entity.owner,
                                        entity.jobid,
                                        entity.extraid);
                            }
                            if (!extractJobIdStr.substring(extractJobIdStr.length()-1).equals("[")){
                                extractJobIdStr = extractJobIdStr.substring(0,extractJobIdStr.length()-1);
                            }
                            extractJobIdStr += "]";
                        }catch (Exception e){
                            logger.warn("original_id={}",id);
                        }
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
//        RedisHelper.setSearchJson(expression+"-"+String.valueOf(page)+"-"+String.valueOf(tag),dataSuffix.toString());
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
    public void basicExcelDownloadFunction(HttpServletResponse res,String expression,int flag) throws IOException, NoSuchAlgorithmException {
        BasicDBObject basicDBObject = CoreConditionGenerator.coreContionGenertor(expression,flag);
        byte[] valueByte = excelGenerate(basicDBObject);
        FileUtil.generateDownloadResponseByBytes(valueByte,res,"excelResults.xls");
    }


    private byte[] excelGenerate(BasicDBObject base) throws IOException, NoSuchAlgorithmException {
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
        return ExcelUtil.excelGenerateToByte(container);
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
            // 添加页面是否显示
            boolean isShowTransport = false;
            try{
                //childs.transport.childs.test_scan.childs.transported_txt
                Document a = (Document) calculateDoc.get("childs");
                Document b = (Document) a.get("transport");
                Document c = (Document) b.get("childs");
                Document d = (Document) c.get("test_scan");
                Document e = (Document) d.get("childs");
                Document f = (Document) e.get("transported_txt");
                isShowTransport = true;
            }catch (Exception e){
                logger.warn("[method=detailInfoById,tag=isShowTransport,id={}]"+e.getMessage(),mid);
            }

//            isShowTransport = calculateDoc.containsKey("childs.transport.childs.test_scan.childs.transported_txt");

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
                jsonAppendString.append(",\"isShowTransport\":"+isShowTransport);
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
    public String basicJsmolFunction(String idd){
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
    public String basicJsmolFunctionFromMongoDb(String idd){
        String caculateMetaId = (String) extraColletion.find(new BasicDBObject("_id",new ObjectId(idd))).limit(1).first().get("caculate_meta_id").toString();
        logger.info("渲染jsmol[caculateMetaId:{}]",caculateMetaId);
        logger.info("===================");
        return getStringFromMongo(caculateMetaId,"bposcar");
    }

    private String getStringFromMongo(String caculateMetaId,String columnName){
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
    public void basicFileDownloadFunction(HttpServletResponse res,String extractId,String columnName){
        String caculateMetaId = (String) extraColletion.find(new BasicDBObject("_id",new ObjectId(extractId))).limit(1).first().get("caculate_meta_id").toString();
        String v = getStringFromMongo(caculateMetaId,columnName);
        FileUtil.generateDownloadResponseByString(v,res,columnName);
    }

    /**
     * 图片从库中读取
     * @param response
     * @param jobid
     * @param type
     */
    public void basicImgLoad(HttpServletResponse response, String jobid, int type) throws IOException {
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
    public void basicPoscarDownloadFunction(HttpServletResponse response, String expression, int flag) throws IOException {
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

    private LinkedHashMap<String,byte[]> getPoscarFromExpression(String expression,int flag){
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



    /**
     * 预留接口，全部下载excel 和 poscar
     * @param res
     * @param expression
     * @param flag
     */
    public void basicPoscarAndExcelDownload(HttpServletResponse res, String expression, int flag) {
    }

    // TODO: 9/26/17 与上面方面重复度太高，待改进，写的太丑
    public void choosedPoscarDownloadFunction(HttpServletResponse res, String mids, int flag) throws IOException {
        logger.info(mids);
        String zipName = "choosedPoscarResults.zip";
        res.setContentType("APPLICATION/OCTET-STREAM");
        res.setHeader("Content-Disposition","attachment; filename="+zipName);
        ZipOutputStream out = new ZipOutputStream(res.getOutputStream());
        try {
            LinkedHashMap<String,byte[]> hm = getPoscarFromMids(mids,flag);
            ArrayList<byte[]> poscarList = new ArrayList<byte[]>();
            ArrayList<String> nameList = new ArrayList<String>();
            for (Map.Entry<String,byte[]> mm:hm.entrySet()){
                poscarList.add(mm.getValue());
                nameList.add(mm.getKey());
            }
            ZipHelper.doCompress(poscarList,nameList,out);
            res.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            out.close();
        }
    }

    private LinkedHashMap<String,byte[]> getPoscarFromMids(String mids, int flag) {
        List<String> midList = Arrays.asList(mids.split("&"));
        BasicDBObject base = new BasicDBObject();
        BasicDBList list = new BasicDBList();
        for (String mid : midList){
            list.add(new BasicDBObject("original_id",mid));
        }
        base.put("$or",list);
        logger.info(base.toString());
        ArrayList<byte[]> res = new ArrayList<byte[]>();
        LinkedHashMap<String,byte[]> result = new LinkedHashMap<String, byte[]>();
        for (Document document:latestBasicCollection.find(base)){
            String valuePoscar = (String) document.get("poscar");
            String name = (String) document.get("simplified_name");
            String objectid = (String) document.get("_id").toString();
            result.put(name+"-"+objectid,valuePoscar.getBytes());
        }
        return result;
    }

    public void choosedExcelDownloadFunction(HttpServletResponse res, String mids, int flag) throws IOException, NoSuchAlgorithmException {
        List<String> midList =Arrays.asList(mids.split("&"));
        BasicDBObject base = new BasicDBObject();
        BasicDBList list = new BasicDBList();
        for (String mid : midList){
            list.add(new BasicDBObject("original_id",mid));
        }
        base.put("$or",list);
        byte[] valueByte = excelGenerate(base);
        FileUtil.generateDownloadResponseByBytes(valueByte,res,"choosedExcelResults.xls");
    }
}
