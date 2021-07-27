package Entity;

import java.util.List;

public class Element {
    String id;
    String tid; //任务编号TXX
    String name;
    String laneId; //所属泳道的id
    String type; //类型 即活动或网关
    List<String> incomingList; //流入的顺序流id
    List<String> outgoingList; //流出的顺序流id
    String condition; //执行条件
    String action; //执行动作

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLaneId() {
        return laneId;
    }

    public void setLaneId(String laneId) {
        this.laneId = laneId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getIncomingList() {
        return incomingList;
    }

    public void setIncomingList(List<String> incomingList) {
        this.incomingList = incomingList;
    }

    public List<String> getOutgoingList() {
        return outgoingList;
    }

    public void setOutgoingList(List<String> outgoingList) {
        this.outgoingList = outgoingList;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
