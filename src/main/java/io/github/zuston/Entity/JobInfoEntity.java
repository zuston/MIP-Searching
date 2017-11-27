package io.github.zuston.Entity;

/**
 * Created by zuston on 2017/11/27.
 */
public class JobInfoEntity {
    public String objectId;
    public String create_time;
    public String finish_time;
    public String calMethod;
    public String calServer;
    public String owner;
    public String jobid;
    public String extraid;

    public JobInfoEntity() {
    }

    public JobInfoEntity(String objectId, String create_time, String finish_time, String calMethod, String calServer, String owner) {
        this.objectId = objectId;
        this.create_time = create_time;
        this.finish_time = finish_time;
        this.calMethod = calMethod;
        this.calServer = calServer;
        this.owner = owner;
    }
}
