package io.github.zuston.WorkFlow.WfBean;

/**
 * Created by zuston on 17-9-13.
 */
public class EdgeNode {
    public int sourceID;
    public int targetID;
    // 1 =  action -> action
    // 2 =  action -> question
    // 3 =  question -> action
    public int type;
    // 即为　type=3 question 时候的判断结果
    public boolean conditionRes;

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public int getTargetID() {
        return targetID;
    }

    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isConditionRes() {
        return conditionRes;
    }

    public void setConditionRes(boolean conditionRes) {
        this.conditionRes = conditionRes;
    }
}


