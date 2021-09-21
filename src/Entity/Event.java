package Entity;

import java.util.ArrayList;
import java.util.List;


public class Event {
    //事件ID
    String e_id;
    //事件名
    String name;
    //事件注释
    String description;
    //事件所属任务ID
    List<String> process_id;

    public Event() {
        this.process_id = new ArrayList<>();
    }

    public String getE_id() {
        return e_id;
    }

    public void setE_id(String e_id) {
        this.e_id = e_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getProcess_id() {
        return process_id;
    }

    public void setProcess_id(List<String> process_id) {
        this.process_id = process_id;
    }
}
