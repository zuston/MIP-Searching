package io.github.zuston.Util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuston on 17-2-22.
 */
public class MongoDb {
    public static String host = "202.120.121.198";
    public static int    port = 27017;
    public static String dbName = "material";

    public static String username = "material";
    public static String pwd = "!@QWmaterial";


//    public static String dbName = "test_meng";
//
//    public static String username = "test_meng";
//    public static String pwd = "test_meng";

    public volatile static MongoDatabase mongoDatabase = null;

    public MongoDb() {

    }

    public static MongoDatabase getInstance(){
        if(MongoDb.mongoDatabase==null){
            synchronized (MongoDb.class){
//                MongoClient mongoClient = new MongoClient(MongoDb.host,MongoDb.port);
//                MongoDb.mongoDatabase = mongoClient.getDatabase(MongoDb.dbName);

                ServerAddress serverAddress = new ServerAddress(MongoDb.host,MongoDb.port);
                List<ServerAddress> addrs = new ArrayList<ServerAddress>();
                addrs.add(serverAddress);

                MongoCredential credential = MongoCredential.createScramSha1Credential(MongoDb.username, MongoDb.dbName, MongoDb.pwd.toCharArray());
                List<MongoCredential> credentials = new ArrayList<MongoCredential>();
                credentials.add(credential);

                //通过连接认证获取MongoDB连接
                MongoClient mongoClient = new MongoClient(addrs,credentials);

                //连接到数据库
                MongoDatabase mongoDatabase = mongoClient.getDatabase(MongoDb.dbName);
                MongoDb.mongoDatabase = mongoDatabase;
            }
        }
        return MongoDb.mongoDatabase;
    }
}
