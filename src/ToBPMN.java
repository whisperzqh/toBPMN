import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Entity.*;

/**
 * 程序中创建数据库、创建表、然后插入数据，
 * 最后读出数据显示出来
 */
public class ToBPMN
{
    public static List<SubProcess> SubProcessList = new ArrayList<>();//子流程列表
    public static List<Lane> LaneList = new ArrayList<>();//泳道列表
    public static List<NewProcess> newProcessList = new ArrayList<>();//任务、网关、事件列表
    public static List<Sequence> SequenceList = new ArrayList<>();//顺序流列表
    public static List<Parameter> ParameterList = new ArrayList<>();//参数列表
    public static List<Event> EventList = new ArrayList<>();//事件列表
    public static void main(String[] args) {
        try
        {

            //连接SQLite的JDBC
            Class.forName("org.sqlite.JDBC");
            //建立一个数据库名lincj.db的连接，如果不存在就在当前目录下创建之
            Connection conn = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
            //Connection conn = DriverManager.getConnection("jdbc:sqlite:path(路径)/lincj.db");
            Statement stat = conn.createStatement();
            PreparedStatement preStat = null;

            //parameter表
            ResultSet rs = stat.executeQuery("select * from BPMN_parameter;");
            while(rs.next()) {
                Parameter parameter = new Parameter();
                parameter.setPid(rs.getString("p_id"));
                parameter.setName(rs.getString("name"));
                parameter.setType(rs.getString("typr"));
                ParameterList.add(parameter);
            }

            //event表
            rs = stat.executeQuery("select * from BPMN_event;");
            while (rs.next()){
                Event event = new Event();
                event.setE_id(rs.getString("e_id"));
                event.setName(rs.getString("name"));
                event.setDescription(rs.getString("description"));
                String pid = rs.getString("process_id");
                event.setProcess_id(new ArrayList<>(Arrays.asList(pid.split("、"))));
                EventList.add(event);
            }

            //subprocess表
            rs = stat.executeQuery("select * from BPMN_subprocess;"); //查询数据
            while (rs.next()) {
                SubProcess subProcess = new SubProcess();
                subProcess.setSPid(rs.getString("sp_id"));
                subProcess.setName(rs.getString("name"));
                subProcess.setId("Activity_"+getRandomString());
                String processId = rs.getString("process_id");
                subProcess.setTidList(new ArrayList<>(Arrays.asList(processId.split("、"))));
                SubProcessList.add(subProcess);
            }

            //process表 得到所有泳道
            rs = stat.executeQuery("select distinct executor from BPMN_process;");
            while(rs.next()) {
                Lane lane = new Lane();
                lane.setName(rs.getString("executor"));
                lane.setId("Lane_"+getRandomString());
                LaneList.add(lane);
            }

            //process表 替换子流程 创建newProcess列表
            rs = stat.executeQuery("select * from BPMN_process;");
            while (rs.next()) {
                NewProcess newProcess = new NewProcess();
                newProcess.setTid(rs.getString("t_id"));
                newProcess.setExecutor(rs.getString("executor"));
                newProcess.setName(rs.getString("action"));
                //如果接收方为空 则接收方为执行方
                if(isEmpty(rs.getString("recipient")))
                    newProcess.setRecipient(rs.getString("executor"));
                else newProcess.setRecipient(rs.getString("recipient"));
                newProcess.setType("task");
                newProcess.setWidth(100);
                newProcess.setHeight(80);
                int id = rs.getInt("id"); //implement表里的process_id process表里的id
                preStat = conn.prepareStatement("select implement_id from BPMN_process_execute where process_id = ?");
                preStat.setInt(1,id);
                ResultSet rs1 = preStat.executeQuery();//得到implement_id
                List<Implement> implementList = new ArrayList<>();
                while (rs1.next()) {
                    preStat = conn.prepareStatement("select * from BPMN_implement where id = ?");
                    preStat.setInt(1,rs1.getInt("implement_id"));
                    ResultSet rs2 = preStat.executeQuery();
                    while (rs2.next()) {
                        Implement implement = new Implement();
                        implement.setAction(rs2.getString("execute_action"));
                        implement.setCondition(rs2.getString("execute_condition"));
                        implementList.add(implement);
                    }
                }
                newProcess.setImplementList(implementList);
                newProcessList.add(newProcess);
            }


            //给执行条件大于1的任务后面插入一个网关
            int count =0;
            for(int i=0;i< newProcessList.size();i++){
                NewProcess newProcess = newProcessList.get(i);
                //一个执行条件对应多个执行动作  并行网关
                if(newProcess.getType().equals("task")){
                    List<Implement> implementList = newProcess.getImplementList();
                    for(int j=0;j< implementList.size();j++){
                        String action = implementList.get(j).getAction();
                        String tid = "";
                        int indexE = action.indexOf('/');
                        int indexP = action.indexOf('&');
                        if(indexE>=0||indexP>=0){
                            NewProcess gateway = new NewProcess();
                            gateway.setTid("G"+count);
                            if(indexE>=0){
                                tid = action.substring(2,indexE);
                                gateway.setType("exclusiveGateway");
                            }
                            else if(indexP>=0){
                                tid = action.substring(2,indexP);
                                gateway.setType("parallelGateway");
                            }
                            gateway.setWidth(50);
                            gateway.setHeight(50);
                            gateway.setExecutor(newProcess.getRecipient());
                            List<Implement> implementList1 = new ArrayList<>();
                            Implement implement = new Implement();
                            implement.setCondition(implementList.get(j).getCondition());
                            implement.setAction(implementList.get(j).getAction());
                            implementList1.add(implement);
                            gateway.setImplementList(implementList1);
                            count++;
                            implementList.get(j).setAction("进行"+gateway.getTid());
                            newProcessList.add(tidToIndex(tid),gateway);
                            //查询当前任务是否属于某一个子流程 如果是 则生成的网关也要加入该子流程
                            SubProcess subProcess = ifInSubProcess(tid);
                            if(subProcess!=null){
                                int index1 = subProcess.getTidList().indexOf(tid);
                                subProcess.getTidList().add(index1,gateway.getTid());
                            }
                        }
                    }
                }
                //多个执行条件和动作
                if(newProcess.getType().equals("task") && newProcess.getImplementList().size()>1){
                    NewProcess gateway = new NewProcess();
                    gateway.setTid("G"+count);
                    //如果执行条件为空 就是并行网关 否则是互斥网关
                    if(!isEmpty(newProcess.getImplementList().get(0).getCondition()))
                        gateway.setType("exclusiveGateway");
                    else gateway.setType("parallelGateway");
                    gateway.setWidth(50);
                    gateway.setHeight(50);
                    gateway.setExecutor(newProcess.getRecipient());
                    gateway.setImplementList(newProcess.getImplementList());
                    count++;
                    Implement implement = new Implement();
                    implement.setCondition("");
                    implement.setAction("进行"+gateway.getTid());
                    List<Implement> list = new ArrayList<>();
                    list.add(implement);
                    newProcess.setImplementList(list);
                    newProcessList.add(i+1,gateway);

                    //查询当前任务是否属于某一个子流程 如果是 则生成的网关也要加入该子流程
                    SubProcess subProcess = ifInSubProcess(newProcess.getTid());
                    if(subProcess!=null){
                        int index = subProcess.getTidList().indexOf(newProcess.getTid());
                        subProcess.getTidList().add(index+1,gateway.getTid());
                    }
                }


            }

            //生成成对网关
            //先给newProcessList中每个元素的preList赋值
            for(int i=0;i<newProcessList.size();i++){
                for(int j=0;j<newProcessList.get(i).getImplementList().size();j++){
                    String action = newProcessList.get(i).getImplementList().get(j).getAction();
                    int length = action.length();
                    if(!action.equals("结束")){ //并行网关
                        List<String> tidList = new ArrayList<>(Arrays.asList(action.substring(2,length)));
                        int indexE = action.indexOf('/');
                        int indexP = action.indexOf('&');
                        if(indexE>=0){
                            tidList = new ArrayList<>(Arrays.asList(action.substring(2,length).split("/")));
                        }
                        else if(indexP>=0){
                            tidList = new ArrayList<>(Arrays.asList(action.substring(2,length).split("&")));
                        }
                        for(int k=0;k<tidList.size();k++){
                            int index = tidToIndex(tidList.get(k));
                            newProcessList.get(index).getPreList().add(newProcessList.get(i).getTid());
                        }
                    }
                }
            }
            //如果prelist长度大于1则生成网关
            for(int i=0;i< newProcessList.size();i++){
                int size = newProcessList.get(i).getPreList().size();
                if(size>1){
                    List<List<String>> pathList = new ArrayList<>();
                    for(int j=0;j<size;j++){
                        List<String> path = new ArrayList<>();
                        path.add(newProcessList.get(i).getPreList().get(j));
                        int k = 0;
                        while(newProcessList.get(tidToIndex(path.get(k))).getPreList().size()==1){ //只有前置唯一的时候才继续生成路径
                            path.add(newProcessList.get(tidToIndex(path.get(k))).getPreList().get(0));
                            k++;
                        }
                        pathList.add(path);
                    }
                    //找到所有路径中有重复的网关
                    List<String> sameGatewayList = new ArrayList<>();//重复的网关
                    List<List<String>> sameList = new ArrayList<>();//重复的网关路径起点对应newProcessList的preList元素
                    for(int j=0;j<size;j++){
                        for(int k=0;k<pathList.get(j).size();k++){
                            if(pathList.get(j).get(k).substring(0,1).equals("G")){
                                if(sameGatewayList.indexOf(pathList.get(j).get(k))<0){//如果之前没有加入过相同的
                                    List<String> tidList = new ArrayList<>();
                                    for(int m=0;m<size;m++){
                                        if(pathList.get(m).indexOf(pathList.get(j).get(k))>=0)
                                            tidList.add(newProcessList.get(i).getPreList().get(m));
                                    }
                                    if(tidList.size()>1){
                                        sameGatewayList.add(pathList.get(j).get(k));
                                        sameList.add(new ArrayList<>(tidList));
                                    }
                                }
                            }
                        }
                    }
                    if(sameGatewayList.size()<1) continue;//没有重复的 不需要生成网关
                    int large = 0;//在sameGatewayList里的位置
                    int largeIndex = -1;
                    for(int j=0;j<sameGatewayList.size();j++){
                        if(tidToIndex(sameGatewayList.get(j))>largeIndex){
                            largeIndex = tidToIndex(sameGatewayList.get(j));
                            large = j;
                        }
                    }
                    //生成新网关
                    NewProcess gateway = new NewProcess();
                    gateway.setTid("G"+count);
                    gateway.setType(newProcessList.get(largeIndex).getType());
                    gateway.setWidth(50);
                    gateway.setHeight(50);
                    gateway.setExecutor(newProcessList.get(largeIndex).getExecutor());
                    List<String> l = new ArrayList();
                    l.add(sameGatewayList.get(large));
                    gateway.setPreList(l);
                    List<Implement> implementList = new ArrayList<>();
                    Implement implement = new Implement();
                    implement.setAction("进行"+newProcessList.get(i).getTid());
                    implementList.add(implement);
                    gateway.setImplementList(implementList);
                    count++;
                    //修改第i个newProcessList的preList
                    for(int j=0;j<sameList.get(large).size();j++){
                        int index = newProcessList.get(i).getPreList().indexOf(sameList.get(large).get(j));
                        newProcessList.get(i).getPreList().remove(index);
                    }
                    newProcessList.get(i).getPreList().add(gateway.getTid());
                    //修改sameList元素的执行动作
                    for(int j=0;j<sameList.get(large).size();j++){
                        int index = tidToIndex(sameList.get(large).get(j));
                        newProcessList.get(index).getImplementList().get(0).setAction("进行"+gateway.getTid());
                    }
                    //查询当前任务是否属于某一个子流程 如果是 则生成的网关也要加入该子流程
                    SubProcess subProcess = ifInSubProcess(newProcessList.get(i).getTid());
                    if(subProcess!=null){
                        int index = subProcess.getTidList().indexOf(newProcessList.get(i).getTid());
                        subProcess.getTidList().add(index,gateway.getTid());
                    }
                    //把网关放进newProcessList
                    newProcessList.add(i,gateway);
                }
            }

            //已经生成完了成对的网关 继续生成不成对的
            for(int i=0;i<newProcessList.size();i++){
                int size = newProcessList.get(i).getPreList().size();
                if(size>1) {
                    //生成新网关
                    NewProcess gateway = new NewProcess();
                    gateway.setTid("G"+count);
                    gateway.setType("exclusiveGateway");
                    gateway.setWidth(50);
                    gateway.setHeight(50);
                    gateway.setExecutor(newProcessList.get(i).getExecutor());
                    gateway.setPreList(new ArrayList<>(newProcessList.get(i).getPreList()));
                    List<Implement> implementList = new ArrayList<>();
                    Implement implement = new Implement();
                    implement.setAction("进行"+newProcessList.get(i).getTid());
                    implementList.add(implement);
                    gateway.setImplementList(implementList);
                    count++;
                    //修改原来前置元素的执行动作
                    for(int j=0;j<newProcessList.get(i).getPreList().size();j++){
                        int index = tidToIndex(newProcessList.get(i).getPreList().get(j));
                        for(int k=0;k<newProcessList.get(index).getImplementList().size();k++){
                            int length = newProcessList.get(index).getImplementList().get(k).getAction().length();
                            if(newProcessList.get(index).getImplementList().get(k).getAction().substring(2,length).equals(newProcessList.get(i).getTid())){
                                newProcessList.get(index).getImplementList().get(k).setAction("进行"+gateway.getTid());
                                break;
                            }
                        }
                    }
                    //修改第i个newProcessList的preList 如果先修改这里 就找不到原本的前置了 无法修改原来前置的执行动作
                    List<String> l = new ArrayList<>();
                    l.add(gateway.getTid());
                    newProcessList.get(i).setPreList(l);
                    //查询当前任务是否属于某一个子流程 如果是 则生成的网关也要加入该子流程
                    SubProcess subProcess = ifInSubProcess(newProcessList.get(i).getTid());
                    if(subProcess!=null){
                        int index = subProcess.getTidList().indexOf(newProcessList.get(i).getTid());
                        subProcess.getTidList().add(index,gateway.getTid());
                    }
                    //把网关放进newProcessList
                    newProcessList.add(i,gateway);
                    i++;
                }
            }

            //把event表赋值给对应的newProcessList元素
            for(int i=0;i<EventList.size();i++){
                for(int j=0;j<EventList.get(i).getProcess_id().size();j++){
                    NewProcess newProcess = taskTidToObject(EventList.get(i).getProcess_id().get(j));
                    newProcess.getEventList().add(EventList.get(i).getName());
                }
            }



            for(int j=0;j< newProcessList.size();j++){
                //查询是否属于子流程
                NewProcess newProcess = newProcessList.get(j);
                for (int i = 0; i < SubProcessList.size(); i++){
                    SubProcess subProcess = SubProcessList.get(i);
                    if(subProcess.getTidList().contains(newProcess.getTid())){
                        //给子流程中的事件或网关生成随机数id
                        if(newProcess.getType().equals("task"))
                            newProcess.setId("Activity_"+getRandomString());
                        else
                            newProcess.setId("Gateway_"+getRandomString());
                        //如果属于某一个子流程 就加进子流程的newProcessList
                        subProcess.getNewProcessList().add(newProcess);
                        newProcessList.remove(j);
                        if(subProcess.getTidList().get(0).equals(newProcess.getTid())){
                            //如果是某个子流程的第一个活动 就把子流程加入newProcessList
                            NewProcess subNewProcess = new NewProcess();
                            subNewProcess.setId(subProcess.getId());
                            subNewProcess.setType("subProcess");
                            subNewProcess.setName(subProcess.getName());
                            subNewProcess.setTid(subProcess.getSPid());
                            subNewProcess.setExecutor(newProcess.getExecutor());
                            subNewProcess.setWidth(100);
                            subNewProcess.setHeight(80);
                            newProcessList.add(j,subNewProcess);
                        }
                        j--;
                        break;
                    }
                }
            }


            rs.close();
            conn.close(); //结束数据库的连接

            //将总表中所有子流程的执行条件和执行动作赋值
            for(int i=0;i<newProcessList.size();i++){
                if(newProcessList.get(i).getType().equals("subProcess")){
                    String SPid = newProcessList.get(i).getTid();
                    for(int j=0;j<SubProcessList.size();j++){
                        if(SubProcessList.get(j).getSPid().equals(SPid)){
                            List<Implement> implementList = analyseSubProcess(SubProcessList.get(j));
                            newProcessList.get(i).setImplementList(implementList);
                            break;
                        }
                    }
                }
            }





            //填充总表中每个元素的泳道id 生成总表中每个元素的随机数id 修改总表中task的执行动作格式 补充泳道的元素id列表
            for(int i=0;i<newProcessList.size();i++){
                NewProcess process = newProcessList.get(i);
                process.setLaneId(laneNameToId(process.getExecutor()));//填充泳道id
                //如果不是子流程 要生成随机数id 修改执行动作
                if(!process.getType().equals("subProcess")){
                    //修改执行动作
                    List<Implement> implementList = process.getImplementList();
                    for(int j=0;j< implementList.size();j++){
                        String action = implementList.get(j).getAction();
                        if(!action.equals("结束")){
                            int length = action.length();
                            action = action.substring(2,length);
                            //判断执行动作是否属于子流程
                            if(!ifInNewProcess(action)){
                                //不在总表中 替换为对应的子流程SPid
                                action = ifInSubProcess(action).getSPid();
                            }
                            process.getImplementList().get(j).setAction(action);
                        }
                    }
                    //生成随机数id
                    if(process.getType().equals("task"))
                        process.setId("Activity_"+getRandomString());
                    else
                        process.setId("Gateway_"+getRandomString());
                }
                //在泳道类中填充元素的id列表
                LaneList.get(getLaneIndex(process.getLaneId())).getElementIdList().add(process.getId());
            }

            //生成开始和结束事件 生成顺序流
            NewProcess begin = new NewProcess();
            begin.setId("StartEvent_1");
            begin.setType("startEvent");
            begin.setName("start");
            begin.setName("");
            begin.setLaneId(newProcessList.get(0).getLaneId());
            begin.setExecutor(newProcessList.get(0).getExecutor());
            begin.setHeight(36);
            begin.setWidth(36);
            Sequence beginSequence = new Sequence();
            beginSequence.setSourceId(begin.getId());
            beginSequence.setTargetId(newProcessList.get(0).getId());
            beginSequence.setId("Flow_"+getRandomString());
            SequenceList.add(beginSequence);
            begin.getOutgoingList().add(beginSequence.getId());
            newProcessList.get(0).getIncomingList().add(beginSequence.getId());
            LaneList.get(getLaneIndex(begin.getLaneId())).getElementIdList().add(0,begin.getId());
            newProcessList.add(0,begin);
            for(int i=0;i< newProcessList.size();i++){
                List<Implement> implementList = newProcessList.get(i).getImplementList();
                for(int j=0;j<implementList.size();j++){
                    Sequence sequence = new Sequence();
                    sequence.setId("Flow_"+getRandomString());
                    sequence.setName(implementList.get(j).getCondition());
                    sequence.setSourceId(newProcessList.get(i).getId());
                    newProcessList.get(i).getOutgoingList().add(sequence.getId());
                    if(implementList.get(j).getAction().equals("结束")){
                        NewProcess end = new NewProcess();
                        end.setId("Event_"+getRandomString());
                        end.setType("endEvent");
                        end.setLaneId(newProcessList.get(i).getLaneId());
                        end.getOutgoingList().add(sequence.getId());
                        end.setHeight(36);
                        end.setWidth(36);
                        newProcessList.add(i+1,end);
                        sequence.setTargetId(end.getId());
                        int index = LaneList.get(getLaneIndex(end.getLaneId())).getElementIdList().indexOf(newProcessList.get(i).getId());
                        LaneList.get(getLaneIndex(end.getLaneId())).getElementIdList().add(index+1,end.getId());
                    }
                    else{
                        sequence.setTargetId(taskTidToObject(implementList.get(j).getAction()).getId());
                        taskTidToObject(implementList.get(j).getAction()).getIncomingList().add(sequence.getId());
                    }
                    SequenceList.add(sequence);
                }
            }

            Output.printToFile(newProcessList,SequenceList,"PO",false);


            //生成子流程的顺序流
            for(int m=0;m<SubProcessList.size();m++){
                List<NewProcess> newProcessList = SubProcessList.get(m).getNewProcessList();
                List<Sequence> SequenceList = new ArrayList<>();
                NewProcess beginning = new NewProcess();
                beginning.setId("StartEvent_1");
                beginning.setType("startEvent");
                beginning.setName("start");
                beginning.setName("");
                beginning.setHeight(36);
                beginning.setWidth(36);
                Sequence beginningSequence = new Sequence();
                beginningSequence.setSourceId(beginning.getId());
                beginningSequence.setTargetId(newProcessList.get(0).getId());
                beginningSequence.setId("Flow_"+getRandomString());
                SequenceList.add(beginningSequence);
                beginning.getOutgoingList().add(beginningSequence.getId());
                newProcessList.get(0).getIncomingList().add(beginningSequence.getId());
                newProcessList.add(0,beginning);
                for(int i=0;i< newProcessList.size();i++){
                    List<Implement> implementList = newProcessList.get(i).getImplementList();
                    for(int j=0;j<implementList.size();j++){
                        List<String> actionList = new ArrayList<>(Arrays.asList(implementList.get(j).getAction()));
                        int indexE = implementList.get(j).getAction().indexOf('/');
                        int indexP = implementList.get(j).getAction().indexOf('&');
                        if(indexE>=0){
                            actionList = new ArrayList<>(Arrays.asList(implementList.get(j).getAction().split("\\/")));
                        }
                        else if(indexP>=0){
                            actionList = new ArrayList<>(Arrays.asList(implementList.get(j).getAction().split("&")));
                        }//并行网关
                        for(int k = 0;k<actionList.size();k++){
                            Sequence sequence = new Sequence();
                            sequence.setId("Flow_"+getRandomString());
                            sequence.setName(implementList.get(j).getCondition());
                            sequence.setSourceId(newProcessList.get(i).getId());
                            newProcessList.get(i).getOutgoingList().add(sequence.getId());
                            if(actionList.get(k).equals("结束")){
                                NewProcess end = new NewProcess();
                                end.setId("Event_"+getRandomString());
                                end.setType("endEvent");
                                end.getOutgoingList().add(sequence.getId());
                                end.setHeight(36);
                                end.setWidth(36);
                                newProcessList.add(i+1,end);
                                sequence.setTargetId(end.getId());
                            }
                            else{
                                for(int n=0;n< newProcessList.size();n++){
                                    if(!isEmpty(newProcessList.get(n).getTid())&&newProcessList.get(n).getTid().equals(actionList.get(k))){
                                        sequence.setTargetId(newProcessList.get(n).getId());
                                        newProcessList.get(n).getIncomingList().add(sequence.getId());
                                        break;
                                    }
                                }
                            }
                            SequenceList.add(sequence);
                        }

                    }
                }
                Output.printToFile(newProcessList,SequenceList,"PO"+m,true);
            }

        }
        catch( Exception e )
        {
            e.printStackTrace ( );
        }

    }

    public static String getRandomString(){
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 7; i++) {
            int number = random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    //填充子流程的执行条件和执行动作
    public static List<Implement> analyseSubProcess(SubProcess subProcess){
        List<NewProcess> newProcessList1 = subProcess.getNewProcessList();
        List<Implement> implementList = new ArrayList<>();//子流程最终的implementList
        for(int i=0;i<newProcessList1.size();i++){
            for(int j=0;j<newProcessList1.get(i).getImplementList().size();j++){
                //implementList.add(newProcessList.get(i).getImplementList().get(j));
                String action = newProcessList1.get(i).getImplementList().get(j).getAction();
                List<String> list = new ArrayList<>();
                //把子流程中执行动作里的“进行”都去掉
                if(!action.equals("结束")){
                    int length = action.length();
                    action = action.substring(2,length);
                    newProcessList1.get(i).getImplementList().get(j).setAction(action);
                    list = new ArrayList<>(Arrays.asList(action));
                    int indexE = action.indexOf('/');
                    int indexP = action.indexOf('&');
                    if(indexE>=0){
                        list = new ArrayList<>(Arrays.asList(action.split("\\/")));
                    }
                    else if(indexP>=0){
                        list = new ArrayList<>(Arrays.asList(action.split("&")));
                    }//并行网关
                }

                for(int k=0;k<list.size();k++){
                    if(!subProcess.getTidList().contains(list.get(k))){
                        //如果该tid不在本子流程内部的tid列表中，就判断是否在总表中，是否属于另一个子流程
                        if(!ifInNewProcess(list.get(k))){
                            String SPid = ifInSubProcess(list.get(k)).getSPid();
                            if(SPid!=null){//SPid理论上不会为null
                                //属于另一个子流程
                                Implement implement = new Implement();
                                implement.setCondition(newProcessList1.get(i).getImplementList().get(j).getCondition());
                                implement.setAction(SPid);
                                implementList.add(implement);
                                newProcessList1.get(i).getImplementList().get(j).setAction("结束");
                                break;//只考虑了 如果一个执行条件对应多个执行动作 则执行动作均属于同一个子流程
                            }
                        }
                        else {//只要tid不属于子流程内部（在总表中） 就把执行动作改为结束 方便画子流程图
                            Implement implement = new Implement();
                            implement.setCondition(newProcessList1.get(i).getImplementList().get(j).getCondition());
                            implement.setAction(newProcessList1.get(i).getImplementList().get(j).getAction());
                            implementList.add(implement);
                            newProcessList1.get(i).getImplementList().get(j).setAction("结束");
                        }
                    }
                }
            }
        }
        subProcess.setImplementList(implementList);
        return implementList;
    }

    //查询tid是否在总表中 即是否属于某个子流程
    public static boolean ifInNewProcess(String tid){
        for(int i=0;i<newProcessList.size();i++){
            if(newProcessList.get(i).getTid().equals(tid))
                return true;
        }
        return false;
    }

    //查询tid在哪个子流程中
    public static SubProcess ifInSubProcess(String tid){
        for (int i=0;i<SubProcessList.size();i++){
            if(SubProcessList.get(i).getTidList().contains(tid))
                return SubProcessList.get(i);
        }
        return null;
    }

    //由泳道的name得到它的随机数id
    public static String laneNameToId(String name){
        for(int i=0;i<LaneList.size();i++){
            if(LaneList.get(i).getName().equals(name))
                return LaneList.get(i).getId();
        }
        return null;//应该不会发生
    }

    //由泳道id找到它在列表中的位置
    public static int getLaneIndex(String id){
        for(int i=0;i<LaneList.size();i++){
            if(LaneList.get(i).getId().equals(id))
                return i;
        }
        return -1;//应该不会发生
    }

    //根据Tid在总表中查找该元素
    public static NewProcess taskTidToObject(String tid){
        for(int i=0;i< newProcessList.size();i++){
            if(!isEmpty(newProcessList.get(i).getTid())&&newProcessList.get(i).getTid().equals(tid))
                return newProcessList.get(i);
        }
        return null;//应该不会发生
    }

    //根据Tid在总表中找到他的位置
    public static int tidToIndex(String tid){
        for(int i=0;i< newProcessList.size();i++){
            if(!isEmpty(newProcessList.get(i).getTid())&&newProcessList.get(i).getTid().equals(tid))
                return i;
        }
        return -1;//应该不会发生
    }


    //判断总表中某属性是否为空
    public static boolean isEmpty(String item){
        if(item==null||item.length()==0)
            return true;
        return false;
    }

}
