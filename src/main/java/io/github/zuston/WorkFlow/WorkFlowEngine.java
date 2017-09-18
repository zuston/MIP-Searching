package io.github.zuston.WorkFlow;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.github.zuston.WorkFlow.WfBean.ActionNode;
import io.github.zuston.WorkFlow.WfBean.EdgeNode;
import io.github.zuston.WorkFlow.WfBean.QuestionNode;
import io.github.zuston.WorkFlow.WfBean.WorkFlowBean;
import io.github.zuston.WorkFlow.WfSupport.EdgeConst;

import java.lang.reflect.Type;
import java.util.*;
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

    private void generateWf(String workFlowJson) throws Exception {
        parse(workFlowJson);

        // 获取以 id　为　key　的值的节点
        actionHm =
                actionNodes.stream().collect(Collectors.toMap(ActionNode::getId,c->c));

        questionHm =
                questionNodes.stream().collect(Collectors.toMap(QuestionNode::getId,c->c));

        // 将　json　解析为 list
        ArrayList<WorkFlowBean> beansLinkedList = new ArrayList<>();
        for (EdgeNode edgeNode : edgeNodes){

            if (edgeNode.type == EdgeConst.QUESTION_ACTION){
                WorkFlowBean existBean = haveExistedBean(beansLinkedList,edgeNode.sourceID);
                if (existBean == null){
                    existBean = new WorkFlowBean();
                    beansLinkedList.add(existBean);
                }
                existBean.sourceID = edgeNode.sourceID;
                existBean.type = edgeNode.type;
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
            workFlowBean.type = edgeNode.type;
            beansLinkedList.add(workFlowBean);
        }
        //　流程变为
        WorkFlowBean startNode = core(beansLinkedList);
    }

    //　arrayList ---> linkedList
    private WorkFlowBean core(ArrayList<WorkFlowBean> beanArrayList) throws Exception {
        //　show
//        for (WorkFlowBean workFlowBean : beanArrayList){
//            System.out.println(workFlowBean.sourceID + " : "+workFlowBean.nextTrueTargetID + " : "+workFlowBean.nextFalseTargetID +" :" +workFlowBean.type);
//        }

        Map<Integer,WorkFlowBean> arrayHm = beanArrayList.stream().collect(Collectors.toMap(
                WorkFlowBean::getSourceID,c->c
        ));
        // 遍历节点
        // 获取初始节点，只有出没有进,即只有sourceId 未成为别人的 targetID
        Set<Integer> existId = new HashSet<>();
        for (WorkFlowBean flowBean : beanArrayList){
            existId.add(flowBean.sourceID);
            if (flowBean.nextFalseTargetID != null){
                existId.add(flowBean.nextFalseTargetID);
            }
            if (flowBean.nextTrueTargetID != null){
                existId.add(flowBean.nextTrueTargetID);
            }
        }

        for (WorkFlowBean flowBean : beanArrayList){
            flowBean.nextTrue = arrayHm.get(flowBean.nextTrueTargetID);
            if (flowBean.type == EdgeConst.QUESTION_ACTION){
                flowBean.nextFalse = arrayHm.get(flowBean.nextFalseTargetID);
            }

            // 获取初始节点
            if (flowBean.nextFalseTargetID != null && existId.contains(flowBean.nextFalseTargetID))
                existId.remove(flowBean.nextFalseTargetID);
            if (flowBean.nextTrueTargetID != null && existId.contains(flowBean.nextTrueTargetID))
                existId.remove(flowBean.nextTrueTargetID);
        }

        if (existId == null || existId.size() != 1)
            throw new Exception("json error");

        // 获取初始节点
        WorkFlowBean startNode = arrayHm.get(existId.toArray()[0]);

        WorkFlowBean Node = arrayHm.get(existId.toArray()[0]);

        while (startNode!=null){
            if (startNode.nextTrue == null){
                WorkFlowBean endNode = new WorkFlowBean();
                endNode.sourceID = startNode.nextTrueTargetID;
                endNode.type = 0;
                startNode.nextTrue = endNode;
                break;
            }else
                startNode = startNode.nextTrue;
        }

        WorkFlowBean sb = new WorkFlowBean();
        sb = Node;
        while (Node!=null){
            System.out.println(String.format("sourceId:%s,targetId:%s,type:%s",Node.sourceID,Node.nextTrueTargetID,Node.type));
            Node = Node.nextTrue;
        }

        return sb;
    }

    private WorkFlowBean haveExistedBean(ArrayList<WorkFlowBean> beansLinkedList, int sourceID) {
        for (WorkFlowBean bean : beansLinkedList){
            if (bean.sourceID != null && bean.sourceID == sourceID)  return bean;
        }
        return null;
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
