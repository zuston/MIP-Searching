package io.github.zuston.Util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Created by zuston on 17-2-22.
 */
public class MongoDb {
    public static String host = "localhost";
    public static int    port = 27017;
    public static String dbName = "material";

    public volatile static MongoDatabase mongoDatabase = null;

    public MongoDb() {

    }

    public static MongoDatabase getInstance(){
        if(MongoDb.mongoDatabase==null){
            synchronized (MongoDb.class){
                MongoClient mongoClient = new MongoClient(MongoDb.host,MongoDb.port);
                MongoDb.mongoDatabase = mongoClient.getDatabase(MongoDb.dbName);
            }
        }
        return MongoDb.mongoDatabase;
    }
}
