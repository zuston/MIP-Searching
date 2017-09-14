package io.github.zuston.WorkFlow;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.github.zuston.WorkFlow.WfBean.ActionNode;
import io.github.zuston.WorkFlow.WfBean.EdgeNode;
import io.github.zuston.WorkFlow.WfBean.QuestionNode;
import io.github.zuston.WorkFlow.WfBean.WorkFlowBean;
import io.github.zuston.WorkFlow.WfSupport.EdgeConst;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zuston on 17-9-13.
 * 工作流引擎
 */
public class WorkFlowEngine {
    public ArrayList<ActionNode> actionNodes = new ArrayList<>();
    public ArrayList<QuestionNode> questionNodes = new ArrayList<>();
    public ArrayList<EdgeNode> edgeNodes = new ArrayList<>();

    public Map<Integer,ActionNode> actionHm = new HashMap<>();
    Map<Integer,QuestionNode> questionHm = new HashMap<>();
    /**
     * 将json格式的信息生成执行脚本
     * @param workFlowJson
     * @return
     */
    public boolean run(String workFlowJson) throws Exception {
        // 输入输出合规性判断
        check(workFlowJson);
        //　json -->  流程
        generateWf(workFlowJson);
        return false;
    }

    private void generateWf(String workFlowJson) {
        parse(workFlowJson);

        // 获取以 id　为　key　的值的节点
        actionHm =
                actionNodes.stream().collect(Collectors.toMap(ActionNode::getId,c->c));

        questionHm =
                questionNodes.stream().collect(Collectors.toMap(QuestionNode::getId,c->c));

        // 将　json　解析为链表
        ArrayList<WorkFlowBean> beansLinkedList = new ArrayList<>();
        for (EdgeNode edgeNode : edgeNodes){

            if (edgeNode.type == EdgeConst.QUESTION_ACTION){
                WorkFlowBean existBean = haveExistedBean(beansLinkedList,edgeNode.sourceID);
                if (edgeNode.conditionRes){
                    existBean.nextTrueTargetID = edgeNode.targetID;
                }else {
                    existBean.nextFalseTargetID = edgeNode.targetID;
                }
                continue;
            }

            WorkFlowBean workFlowBean = new WorkFlowBean();
            workFlowBean.sourceID = edgeNode.sourceID;
            workFlowBean.nextTrueTargetID = edgeNode.targetID;
            beansLinkedList.add(workFlowBean);
        }
        core(beansLinkedList);
    }

    //　流模型解析，映射到　script　生成
    private void core(ArrayList<WorkFlowBean> beansLinkedList) {

    }

    private WorkFlowBean haveExistedBean(ArrayList<WorkFlowBean> beansLinkedList, int sourceID) {
        for (WorkFlowBean bean : beansLinkedList){
            if (bean.sourceID == sourceID)  return bean;
        }
        return new WorkFlowBean();
    }

//    private void core(int[] flowContainer, int start) {
//        if (flowContainer[start]==0)    return;
//        int nextId = flowContainer[start];
//        int currentID = start;
//
//        // 不采用 edge type　字段的条件
//        int nextTag = actionHm.containsKey(nextId) && !questionHm.containsKey(nextId) ? 1:0;
//        int currentTag = actionHm.containsKey(currentID) && !questionHm.containsKey(currentID) ? 1:0;
//        int edgeType = 0;
//        if (nextTag == 1 && currentTag ==1) edgeType = EdgeConst.ACTION_ACTION;
//        if (nextTag == 1 && currentTag ==0) edgeType = EdgeConst.QUESTION_ACTION;
//        if (nextTag == 0 && currentTag ==1) edgeType = EdgeConst.ACTION_QUESTION;
//
//        if (edgeType == EdgeConst.QUESTION_ACTION){
//
//        }
//    }

    /**
     * json解析
     * @param workFlowJson
     */
    private void parse(String workFlowJson) {
        Gson gson = new Gson();

        // 整体与局部分割
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(workFlowJson);
        JsonObject jsonObject = root.getAsJsonObject();

        JsonArray actionObjectNode = jsonObject.get("ActionNode").getAsJsonArray();
        JsonArray questionObjectNode = jsonObject.get("QuestionNode").getAsJsonArray();
        JsonArray edgeObjectNode = jsonObject.get("edges").getAsJsonArray();


        Type actionNodesListType = new TypeToken<ArrayList<ActionNode>>(){}.getType();
        Type questionNodesListType = new TypeToken<ArrayList<QuestionNode>>(){}.getType();
        Type edgeNodesListType = new TypeToken<ArrayList<EdgeNode>>(){}.getType();

        actionNodes.addAll(
                gson.fromJson(
                actionObjectNode,
                actionNodesListType
                )
        );

        questionNodes.addAll(
                gson.fromJson(
                        questionObjectNode,
                        questionNodesListType
                )
        );

        edgeNodes.addAll(
                gson.fromJson(
                        edgeObjectNode,
                        edgeNodesListType
                )
        );
    }

    private void check(String workFlowJson) throws Exception {
//        throw new Exception("输入输出流不合规");
    }
}
