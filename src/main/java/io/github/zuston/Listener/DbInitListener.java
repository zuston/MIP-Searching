package io.github.zuston.Listener;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.zuston.Helper.DbHelper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by zuston on 17/5/24.
 */
public class DbInitListener implements ApplicationListener<ApplicationStartedEvent>{

    public static final Logger LOGGER = LoggerFactory.getLogger(DbInitListener.class);

    public static MongoDatabase mongoDataBase = null;
    public static MongoCollection<Document> latestBasicCollection = null;
    public static MongoCollection<Document> extraColletion = null;
    public static MongoCollection<Document> caculateMetaCollection = null;
    public static MongoCollection<Document> smallFileCollection = null;
    // job info
    public static MongoCollection<Document> jobInfoCollection = null;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        LOGGER.info("[init the db instance]");
        mongoDataBase = DbHelper.getInstance();
        latestBasicCollection = mongoDataBase.getCollection("pfsas20170501");
        extraColletion = mongoDataBase.getCollection("extract");
        caculateMetaCollection = mongoDataBase.getCollection("caculate_meta");
        smallFileCollection = mongoDataBase.getCollection("small_files");
        jobInfoCollection = mongoDataBase.getCollection("job_info");
    }
}
