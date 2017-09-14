package io.github.zuston.WorkFlow.WfBean;

/**
 * Created by zuston on 17-9-13.
 */
public class QuestionMetaData {
    public int conditionId;
    /**
     * 分为简单判断和复杂判断,
     * 简单判断即为条件变量判断，比较等
     * 复杂判断即为执行脚本输出 true || false 类似
     */
    public int conditionType;
    //　即为　< > = 类似这种
    public String condition;
    // 即为简单模式下的比较值
    public String conditionValue;
    //　即为复杂模式下的输入脚本
    public String conditionSnippet;
    // 即为这个的label
    public String conditionLabel;

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }

    public int getConditionType() {
        return conditionType;
    }

    public void setConditionType(int conditionType) {
        this.conditionType = conditionType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getConditionSnippet() {
        return conditionSnippet;
    }

    public void setConditionSnippet(String conditionSnippet) {
        this.conditionSnippet = conditionSnippet;
    }

    public String getConditionLabel() {
        return conditionLabel;
    }

    public void setConditionLabel(String conditionLabel) {
        this.conditionLabel = conditionLabel;
    }
}
