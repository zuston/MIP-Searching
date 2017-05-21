package io.github.zuston.Util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import io.github.zuston.Helper.ConfigHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zuston on 17-2-22.
 */
public class MongoDb {

    public static HashMap<String,String> config = ConfigHelper.getDbConfig();
    public static String host = config.get("host");
    public static int    port = Integer.valueOf(config.get("port"));
    public static String dbName = config.get("dbName");

    public static String username = config.get("username");
    public static String pwd = config.get("pwd");



    public volatile static MongoDatabase mongoDatabase = null;

    public MongoDb() {

    }

    public static MongoDatabase getInstance(){
        if(MongoDb.mongoDatabase==null){
            synchronized (MongoDb.class){
                if (MongoDb.mongoDatabase==null){

                    ServerAddress serverAddress = new ServerAddress(MongoDb.host,MongoDb.port);
                    List<ServerAddress> addrs = new ArrayList<ServerAddress>();
                    addrs.add(serverAddress);

                    MongoCredential credential = MongoCredential.createScramSha1Credential(MongoDb.username, MongoDb.dbName, MongoDb.pwd.toCharArray());
                    List<MongoCredential> credentials = new ArrayList<MongoCredential>();
                    credentials.add(credential);

                    MongoClient mongoClient = new MongoClient(addrs,credentials);

                    MongoDatabase mongoDatabase = mongoClient.getDatabase(MongoDb.dbName);
                    MongoDb.mongoDatabase = mongoDatabase;
                }
            }
        }
        return MongoDb.mongoDatabase;
    }
}
