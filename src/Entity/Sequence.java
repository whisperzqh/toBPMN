package Entity;

public class Sequence {
    //顺序流的随机数id
    String id;
    //顺序流的名称
    String name;
    //顺序流的源任务id
    String sourceId;
    //顺序流的目标任务id
    String targetId;

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

    public String getSourceId() {

        return sourceId;
    }

    public void setSourceId(String sourceId) {

        this.sourceId = sourceId;
    }

    public String getTargetId() {

        return targetId;
    }

    public void setTargetId(String targetId) {

        this.targetId = targetId;
    }

}
