package Entity;

import java.util.ArrayList;
import java.util.List;

public class SubProcess {
    //数据库主键id
    String id;
    //子流程名称
    String name;
    //子流程id SPXX
    String SPid;
    //属于该子流程的任务对应的tid的list
    List<String> tidList;
    //属于该子流程的任务对应的NewProcess对象的list
    List<NewProcess> newProcessList;
    //所属泳道id
    String laneId;
    //对应的执行条件和执行动作
    List<Implement> implementList;


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

    public List<String> getTidList() {

        return tidList;
    }

    public void setTidList(List<String> tidList) {

        this.tidList = tidList;
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
