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
        //　解析成链表
        WorkFlowBean startNode = core(beansLinkedList);
        // 寻找回路。或者是分叉点
        WorkFlowBean tempNode = startNode;
        Set<Integer> walkedNodes = new HashSet<>();
        //　pathArr 此处演示，可将各 sourceID 对应的代码放入容器中，最后生成
        //　至于代码中的变量，可从ActioNode 或者 QuestionNode 中获取
        ArrayList<String> pathArr = new ArrayList<>();
        generateCode(tempNode,walkedNodes,pathArr);
        for (String path : pathArr){
            System.out.println(path);
        }
    }

    private void generateCode(WorkFlowBean tempNode, Set<Integer> walkedNodes, ArrayList<String> pathArr){
        if (tempNode == null){
            return;
        }

        while (tempNode != null){
            // 判断点
            if (tempNode.type != null && tempNode.type == EdgeConst.QUESTION_ACTION ){
                // 如果是环路,此处预设为环路
                // mode　为判断的模式
                int MODE = 2;
                if (MODE==1){
                    int targetTrueId = tempNode.nextTrueTargetID;
                    int targetFalseId = tempNode.nextFalseTargetID;
                    int nextId = 0;
                    int retureId = 0;
                    //　判断哪个是回去的，哪个是向下继续进行的
                    if (walkedNodes.contains(targetFalseId)){
                        nextId = targetTrueId;
                        retureId = targetFalseId;
                    }else{
                        nextId = targetFalseId;
                        retureId = targetTrueId;
                    }
                    //　确定 returnId　则需要把原有的路径删掉，代码替换掉
                    int returnIndex = pathArr.indexOf("模块---"+retureId);
                    String code = (String.format("for(模块---%s){",tempNode.sourceID));
                    pathArr.add(returnIndex,code);
                    pathArr.add("}");
                    tempNode = tempNode.nextTrue;
                    continue;
                }else {
                    String trueCode = String.format("if(模块---%s){",tempNode.sourceID);
                    pathArr.add(trueCode);
                    generateCode(tempNode.nextTrue,walkedNodes,pathArr);
                    String falseCode = String.format("}else{");
                    pathArr.add(falseCode);
                    System.out.println("tag:");
                    generateCode(tempNode.nextFalse,walkedNodes,pathArr);
                    pathArr.add("}");
                    return;
                }

            }
            walkedNodes.add(tempNode.sourceID);
            //　写入脚本代码
            String code = ("模块---"+tempNode.sourceID);
            pathArr.add(code);
            tempNode = tempNode.nextTrue;
        }


    }

    //　arrayList ---> linkedList
    private WorkFlowBean core(ArrayList<WorkFlowBean> beanArrayList) throws Exception {


        Map<Integer,WorkFlowBean> arrHm = beanArrayList.stream().collect(Collectors.toMap(
                WorkFlowBean::getSourceID,c->c
        ));

        Set<Integer> tailIds = new HashSet<>();
        for (WorkFlowBean workFlowBean : beanArrayList){
            System.out.println(workFlowBean.sourceID + " : "+workFlowBean.nextTrueTargetID + " : "+workFlowBean.nextFalseTargetID +" :" +workFlowBean.type);
//            if (!arrayHm.containsKey(workFlowBean.nextTrueTargetID)){
//                WorkFlowBean workFlowBean1 = new WorkFlowBean();
//                workFlowBean1.sourceID = workFlowBean.nextTrueTargetID;
//                wb.add(workFlowBean1);
//                continue;
//            }
//            wb.add(workFlowBean);
            if (!arrHm.containsKey(workFlowBean.nextTrueTargetID))
                tailIds.add(workFlowBean.nextTrueTargetID);
        }

        if (tailIds.size() == 1){
            WorkFlowBean workFlowBean = new WorkFlowBean();
            workFlowBean.sourceID = tailIds.iterator().next();
            beanArrayList.add(workFlowBean);
        }

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
            if (flowBean.type != null && flowBean.type == EdgeConst.QUESTION_ACTION){
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

//        while (startNode!=null){
//            if (startNode.nextTrue == null){
//                WorkFlowBean endNode = new WorkFlowBean();
//                endNode.sourceID = startNode.nextTrueTargetID;
//                endNode.type = 0;
//                startNode.nextTrue = endNode;
//                break;
//            }else
//                startNode = startNode.nextTrue;
//        }

        WorkFlowBean sb = new WorkFlowBean();
        sb = Node;
        //　此处只是针对单路径输出,true的输出
        while (Node!=null){
            System.out.println(String.format("sourceId:%s,trueTargetId:%s,falseTargetId:%s,type:%s",Node.sourceID,Node.nextTrueTargetID,Node.nextFalseTargetID,Node.type));
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
