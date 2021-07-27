import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Entity.*;

/**
 * 这是个非常简单的SQLite的Java程序,
 * 程序中创建数据库、创建表、然后插入数据，
 * 最后读出数据显示出来
 */
public class SqliteTest
{
    public static List<SubProcess> SubProcessList = new ArrayList<>();
    public static List<Lane> LaneList = new ArrayList<>();
    public static List<NewProcess> newProcessList = new ArrayList<>();
    public static List<Sequence> SequenceList = new ArrayList<>();
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

            //subprocess表
            ResultSet rs = stat.executeQuery("select * from BPMN_subprocess;"); //查询数据
            while (rs.next()) {
                SubProcess subProcess = new SubProcess();
                subProcess.setSPid(rs.getString("sp_id"));
                subProcess.setName(rs.getString("name"));
                subProcess.setId("Activity_"+getRandomString());
                String processId = rs.getString("process_id");
                subProcess.setTidList(Arrays.asList(processId.split("、")));
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
                newProcess.setRecipient(rs.getString("recipient"));
                newProcess.setType(rs.getString("type"));
                if(newProcess.getType().equals("task")){
                    newProcess.setWidth(100);
                    newProcess.setHeight(80);
                }
                else if(newProcess.getType().equals("exclusiveGateway")){
                    newProcess.setWidth(50);
                    newProcess.setHeight(50);
                }
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
                //查询是否属于子流程
                int flag = 0;
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
                            newProcessList.add(subNewProcess);
                        }
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0){
                    //如果不属于任意子流程
//                    newProcess.setType("task");
                    newProcessList.add(newProcess);
//                    System.out.println(newProcess.getTid()+" "+newProcess.getName()+" "+newProcess.getExecutor()+" "+newProcess.getType());
//                    System.out.println("--------------------");
                }
            }
//            for (int i=0;i<newProcessList.size();i++){
//                NewProcess newProcess1 = newProcessList.get(i);
//                System.out.println(newProcess1.getTid()+" "+newProcess1.getName()+" "+newProcess1.getExecutor()+" "+newProcess1.getType());
//                for(int j=0;j<newProcess1.getImplementList().size();j++){
//                    System.out.println(newProcess1.getImplementList().get(j).getCondition()+" "+newProcess1.getImplementList().get(j).getAction());
//                }
//                System.out.println("---------------------------------------");
//            }
//
//            for (int i=0;i<SubProcessList.size();i++){
//                SubProcess  newProcess1 = SubProcessList.get(i);
//                System.out.println(newProcess1.getId()+" "+newProcess1.getName()+" "+newProcess1.getSPid()+" ");
//                for(int j=0;j<newProcess1.getTidList().size();j++){
//                    System.out.println(newProcess1.getTidList().get(j));
//                }
//                for(int k=0;k<newProcess1.getNewProcessList().size();k++){
//                    NewProcess newProcess = newProcess1.getNewProcessList().get(k);
//                    System.out.println(newProcess.getTid()+" "+newProcess.getName()+" "+newProcess.getExecutor()+" "+newProcess.getType());
//                    for(int l=0;l<newProcess.getImplementList().size();l++){
//                        System.out.println(newProcess.getImplementList().get(l).getCondition()+" "+newProcess.getImplementList().get(l).getAction());
//                    }
//                }
//                System.out.println("---------------------------------------");
//            }

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



//            for(int i=0;i<SubProcessList.size();i++){
//                SubProcess s = SubProcessList.get(i);
//                System.out.println(s.getSPid());
//                for(int j=0;j<s.getNewProcessList().size();j++){
//                    NewProcess newProcess1 = s.getNewProcessList().get(j);
//                    System.out.println(newProcess1.getTid()+" "+newProcess1.getName()+" "+newProcess1.getExecutor()+" "+newProcess1.getType()+" "+newProcess1.getId()+" "+newProcess1.getLaneId());
//                    for(int k=0;k<newProcess1.getImplementList().size();k++){
//                        System.out.println(newProcess1.getImplementList().get(k).getCondition()+" "+newProcess1.getImplementList().get(k).getAction());
//                    }
//                    System.out.println("-----------------");
//                }
//                System.out.println("----------------------------------");
//            }

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
                                action = ifInSubProcess(action);
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


//            for (int i=0;i<newProcessList.size();i++){
//                NewProcess newProcess1 = newProcessList.get(i);
//                System.out.println(newProcess1.getTid()+" "+newProcess1.getName()+" "+newProcess1.getExecutor()+" "+newProcess1.getType()+" "+newProcess1.getId()+" "+newProcess1.getLaneId());
//                for(int j=0;j<newProcess1.getImplementList().size();j++){
//                    System.out.println(newProcess1.getImplementList().get(j).getCondition()+" "+newProcess1.getImplementList().get(j).getAction());
//                }
//                System.out.println("---------------------------------------");
//            }
//            for(int i=0;i<LaneList.size();i++){
//                System.out.println(LaneList.get(i).getName()+" "+LaneList.get(i).getElementIdList().toString());
//                System.out.println("---------------------------------------");
//            }

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

//            for(int i=0;i<LaneList.size();i++){
//                System.out.println(LaneList.get(i).getName()+" "+LaneList.get(i).getElementIdList().toString());
//                System.out.println("---------------------------------------");
//            }
//            for(int i=0;i<SequenceList.size();i++){
//                System.out.println(SequenceList.get(i).getId()+" "+SequenceList.get(i).getName()+" "+SequenceList.get(i).getSourceId()+" "+SequenceList.get(i).getTargetId());
//            }

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
                        Sequence sequence = new Sequence();
                        sequence.setId("Flow_"+getRandomString());
                        sequence.setName(implementList.get(j).getCondition());
                        sequence.setSourceId(newProcessList.get(i).getId());
                        newProcessList.get(i).getOutgoingList().add(sequence.getId());
                        if(implementList.get(j).getAction().equals("结束")){
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
                                if(!isEmpty(newProcessList.get(n).getTid())&&newProcessList.get(n).getTid().equals(implementList.get(j).getAction())){
                                    sequence.setTargetId(newProcessList.get(n).getId());
                                    newProcessList.get(n).getIncomingList().add(sequence.getId());
                                    break;
                                }
                            }
                        }
                        SequenceList.add(sequence);
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
                    list = Arrays.asList(action.split("\\/"));
                }
//                int tem = newProcessList1.get(i).getImplementList().get(j).getAction().length();
//                List<String> list = Arrays.asList(newProcessList1.get(i).getImplementList().get(j).getAction().substring(2,tem).split("\\/"));
                for(int k=0;k<list.size();k++){
                    if(!subProcess.getTidList().contains(list.get(k))){
                        //如果该tid不在本子流程内部的tid列表中，就判断是否在总表中，是否属于另一个子流程
                        if(!ifInNewProcess(list.get(k))){
                            String SPid = ifInSubProcess(list.get(k));
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
    public static String ifInSubProcess(String tid){
        for (int i=0;i<SubProcessList.size();i++){
            if(SubProcessList.get(i).getTidList().contains(tid))
                return SubProcessList.get(i).getSPid();
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

    //判断总表中某属性是否为空
    public static boolean isEmpty(String item){
        if(item==null||item.length()==0)
            return true;
        return false;
    }

}
