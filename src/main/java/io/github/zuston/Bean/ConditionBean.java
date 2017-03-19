package io.github.zuston.Bean;

import java.util.ArrayList;

/**
 * Created by zuston on 17-2-22.
 */
public class ConditionBean {

    public String name = "";
    public int tag = 0;
    public ArrayList<String> contentArray = new ArrayList<String>();

    public ConditionBean() {

    }

    public ConditionBean(String name, int tag, ArrayList<String> contentArray) {
        this.name = name;
        this.tag = tag;
        this.contentArray = contentArray;
    }

    public String getName() {
        return name;
    }

    public int getTag() {
        return tag;
    }

    public ArrayList<String> getContentArray() {
        return contentArray;
    }
}
