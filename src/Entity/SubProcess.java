package Entity;

import java.util.ArrayList;
import java.util.List;

public class SubProcess {
    String id;
    String name;
    String SPid; //子流程id SPXX
    String type; //类型 即子流程
    List<String> tidList; //对应的tid list
    List<Element> elementList; //流程表里对应tid所在行的list
    List<NewProcess> newProcessList;
    String laneId; //所属泳道id
    List<Implement> implementList;
//    String condition; //执行条件
//    String action; //执行动作

    public SubProcess() {
        this.newProcessList = new ArrayList<>();
        this.tidList = new ArrayList<>();
        this.implementList = new ArrayList<>();
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSPid() {
        return SPid;
    }

    public void setSPid(String SPid) {
        this.SPid = SPid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTidList() {
        return tidList;
    }

    public void setTidList(List<String> tidList) {
        this.tidList = tidList;
    }

    public List<Element> getElementList() {
        return elementList;
    }

    public void setElementList(List<Element> elementList) {
        this.elementList = elementList;
    }

    public String getLaneId() {
        return laneId;
    }

    public void setLaneId(String laneId) {
        this.laneId = laneId;
    }

    public List<NewProcess> getNewProcessList() {
        return newProcessList;
    }

    public void setNewProcessList(List<NewProcess> newProcessList) {
        this.newProcessList = newProcessList;
    }

    public List<Implement> getImplementList() {
        return implementList;
    }

    public void setImplementList(List<Implement> implementList) {
        this.implementList = implementList;
    }
}
