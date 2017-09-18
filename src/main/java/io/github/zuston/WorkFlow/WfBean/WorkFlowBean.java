package io.github.zuston.WorkFlow.WfBean;

/**
 * Created by zuston on 17-9-14.
 */
public class WorkFlowBean {
    public Integer sourceID;
    public Integer nextTrueTargetID;
    public Integer nextFalseTargetID;
    public Integer type;
    public WorkFlowBean nextTrue;
    public WorkFlowBean nextFalse;

    public Integer getSourceID() {
        return sourceID;
    }

    public void setSourceID(Integer sourceID) {
        this.sourceID = sourceID;
    }
}
