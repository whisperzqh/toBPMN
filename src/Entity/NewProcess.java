package Entity;

import java.util.ArrayList;
import java.util.List;

public class NewProcess {
    String id;//数据库主键id
    String tid;//TXX 或 SPXX 或 GXX
    String laneId;//所属泳道id
    String executor;//执行方
    String name;//执行行为action 即名字
    String recipient;//接收方
    //对应的执行条件和执行动作
    List<Implement> implementList;
    String type;//子流程、任务、网关、事件
    List<String> incomingList; //流入的顺序流id
    List<String> outgoingList; //流出的顺序流id
    //属于该任务的事件列表
    List<String> eventList;
    int x;//横坐标
    int y;//纵坐标
    int width;//元素的宽
    int height;//元素的高
    List<String> preList;//前置任务的Tid列表


    public NewProcess() {
        this.implementList = new ArrayList<>();
        this.incomingList = new ArrayList<>();
        this.outgoingList = new ArrayList<>();
        this.eventList = new ArrayList<>();
        this.preList = new ArrayList<>();
    }

    public String getTid() {

        return tid;
    }

    public void setTid(String tid) {

        this.tid = tid;
    }

    public String getExecutor() {

        return executor;
    }

    public void setExecutor(String executor) {

        this.executor = executor;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getRecipient() {

        return recipient;
    }

    public void setRecipient(String recipient) {

        this.recipient = recipient;
    }

    public List<Implement> getImplementList() {

        return implementList;
    }

    public void setImplementList(List<Implement> implementList) {

        this.implementList = implementList;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getLaneId() {

        return laneId;
    }

    public void setLaneId(String laneId) {

        this.laneId = laneId;
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

    public int getX() {

        return x;
    }

    public void setX(int x) {

        this.x = x;
    }

    public int getY() {

        return y;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getWidth() {

        return width;
    }

    public void setWidth(int width) {

        this.width = width;
    }

    public int getHeight() {

        return height;
    }

    public void setHeight(int height) {

        this.height = height;
    }

    public List<String> getEventList() {

        return eventList;
    }

    public void setEventList(List<String> eventList) {

        this.eventList = eventList;
    }

    public List<String> getPreList() {

        return preList;
    }

    public void setPreList(List<String> preList) {

        this.preList = preList;
    }
}
