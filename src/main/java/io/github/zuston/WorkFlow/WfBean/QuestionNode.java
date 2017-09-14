package io.github.zuston.WorkFlow.WfBean;

/**
 * Created by zuston on 17-9-13.
 */
public class QuestionNode {
    public int id;
    public String name;
    public QuestionMetaData metaData;

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

    public QuestionMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(QuestionMetaData metaData) {
        this.metaData = metaData;
    }
}
