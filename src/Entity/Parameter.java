package Entity;

public class Parameter {
    //参数ID
    String pid;
    //参数名
    String name;
    //参数类型，如字符串、整型
    String type;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }
}
