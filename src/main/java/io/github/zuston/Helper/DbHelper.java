package io.github.zuston.Helper;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import io.github.zuston.Util.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zuston on 17-2-22.
 */
public class DbHelper {

    public static HashMap<String,String> config = ConfigUtil.getDbConfig();
    public static String host = config.get("host");
    public static Integer port = Integer.valueOf(config.get("port"));
    public static String dbName = config.get("dbName");

    public static String username = config.get("username");
    public static String pwd = config.get("pwd");


    public volatile static MongoDatabase mongoDatabase = null;

    public DbHelper() {

    }

    public static MongoDatabase getInstance(){
        if(DbHelper.mongoDatabase==null){
            synchronized (DbHelper.class){
                if (DbHelper.mongoDatabase==null){

                    ServerAddress serverAddress = new ServerAddress(DbHelper.host, DbHelper.port);
                    List<ServerAddress> addrs = new ArrayList<ServerAddress>();
                    addrs.add(serverAddress);

                    MongoCredential credential = MongoCredential.createScramSha1Credential(DbHelper.username, DbHelper.dbName, DbHelper.pwd.toCharArray());
                    List<MongoCredential> credentials = new ArrayList<MongoCredential>();
                    credentials.add(credential);

                    MongoClient mongoClient = new MongoClient(addrs,credentials);

                    MongoDatabase mongoDatabase = mongoClient.getDatabase(DbHelper.dbName);
                    DbHelper.mongoDatabase = mongoDatabase;
                }
            }
        }
        return DbHelper.mongoDatabase;
    }
}
