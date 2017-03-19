package io.github.zuston.Bean;

import java.util.ArrayList;

/**
 * Created by zuston on 17-2-22.
 */
public class ConditionsBean {
    public ArrayList<ConditionBean> condition = new ArrayList<ConditionBean>();

    public int page;

    public ConditionsBean() {
    }

    public ConditionsBean(ArrayList<ConditionBean> condition) {
        this.condition = condition;
    }

    public ArrayList<ConditionBean> getCondition() {
        return condition;
    }

    public int getPage() {
        return page;
    }
}
