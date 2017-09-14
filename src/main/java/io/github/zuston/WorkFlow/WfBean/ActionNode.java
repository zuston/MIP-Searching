package io.github.zuston.WorkFlow.WfBean;

/**
 * Created by zuston on 17-9-13.
 */
public class ActionNode {
    public int id;
    public String name;
    public ActionMetaData metaData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ActionMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(ActionMetaData metaData) {
        this.metaData = metaData;
    }
}

