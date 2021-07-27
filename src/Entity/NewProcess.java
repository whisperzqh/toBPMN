package Entity;

import java.util.ArrayList;
import java.util.List;

public class NewProcess {
    String id;
    String tid;//TXX 或 SPXX
    String laneId;//所属泳道id
    String executor;//执行方
    String name;//执行行为action 即名字
    String recipient;//接收方
    List<Implement> implementList;//对应的执行条件和执行行为
    String type;//子流程 或 活动
    List<String> incomingList; //流入的顺序流id
    List<String> outgoingList; //流出的顺序流id
    int x;
    int y;
    int width;
    int height;


    public NewProcess() {
        this.implementList = new ArrayList<>();
        this.incomingList = new ArrayList<>();
        this.outgoingList = new ArrayList<>();
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
}
