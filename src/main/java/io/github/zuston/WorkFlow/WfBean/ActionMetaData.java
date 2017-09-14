package io.github.zuston.WorkFlow.WfBean;

import java.util.ArrayList;

/**
 * Created by zuston on 17-9-13.
 */
public class ActionMetaData {
    // 输出 path
    public ArrayList<String> outputDataSource;
    //　输出路径
    public ArrayList<String> inputDataSource;
    // 输入参数
    public ArrayList<String> inputParam;

    public ArrayList<String> getOutputDataSource() {
        return outputDataSource;
    }

    public void setOutputDataSource(ArrayList<String> outputDataSource) {
        this.outputDataSource = outputDataSource;
    }

    public ArrayList<String> getInputDataSource() {
        return inputDataSource;
    }

    public void setInputDataSource(ArrayList<String> inputDataSource) {
        this.inputDataSource = inputDataSource;
    }

    public ArrayList<String> getInputParam() {
        return inputParam;
    }

    public void setInputParam(ArrayList<String> inputParam) {
        this.inputParam = inputParam;
    }
}
