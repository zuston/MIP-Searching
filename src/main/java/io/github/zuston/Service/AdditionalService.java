package io.github.zuston.Service;

import com.mongodb.BasicDBObject;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static io.github.zuston.Service.BaseService.mongoColletion;

/**
 * Created by zuston on 17/4/10.
 */
public class AdditionalService {
    /**
     * 获取 s 族化合物
     * 并且写入文件
     */
    public static void getElementsFromS(BasicDBObject basicDBObject,boolean flag){
        if (!flag)  return;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("data/Sfile.txt",false);
            for(Document document:mongoColletion.find(basicDBObject)){
                String id = (String) document.get("m_id");
                out.write((id+"\n").getBytes("utf-8"));
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
