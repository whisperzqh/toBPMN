package Entity;

import java.util.ArrayList;
import java.util.List;

public class Lane {
    //数据库主键id
    String id;
    //泳道名称
    String name;
    //内部元素的id
    List<String> elementIdList;
    //横坐标
    int x;
    //纵坐标
    int y;

    public Lane() {

        this.elementIdList = new ArrayList<>();
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

    public List<String> getElementIdList() {

        return elementIdList;
    }

    public void setElementIdList(List<String> elementIdList) {

        this.elementIdList = elementIdList;
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

}
