package Entity;

public class Implement {
    //执行条件
    String condition;
    //执行动作
    String action;

    public Implement() {
    }

    public Implement(String condition, String action) {
        this.condition = condition;
        this.action = action;
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
