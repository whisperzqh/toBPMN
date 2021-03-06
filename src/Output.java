import Entity.Lane;
import Entity.NewProcess;
import Entity.Sequence;
import Entity.Parameter;

import java.io.*;
import java.util.List;

/**

 *@描述  程序中生成符合BPMN2.0规范的代码文件

 *@创建时间  2021/9/21

 *@其他

 */

public class Output {

    public static List<NewProcess> newProcessList = null;

    public static List<Sequence> SequenceList = null;

    public static void printToFile(List<NewProcess> newProcessList1,List<Sequence> SequenceList1, String fileName, boolean ifSub) throws IOException {

        newProcessList = newProcessList1;
        SequenceList = SequenceList1;
        String path = ".\\output\\"+fileName+".bpmn";
        File file = new File(path);
        //如果文件不存在，则自动生成文件；
        if(!file.exists()) {
            file.createNewFile();
        }
        //清空文件内容
        FileWriter fileWriter =new FileWriter(file);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();

        //引入输出流
        OutputStream outPutStream;
        try{
            outPutStream = new FileOutputStream(file);
            //使用长度可变的字符串对象；
            StringBuilder stringBuilder = new StringBuilder();
            if(!ifSub) {
                //追加文件内容
                stringBuilder.append(total());
            }
            else {
                stringBuilder.append(subTotal());
            }
            //将可变字符串变为固定长度的字符串，方便下面的转码；
            String context = stringBuilder.toString();
            //因为中文可能会乱码，这里使用了转码，转成UTF-8；
            byte[]  bytes = context.getBytes("UTF-8");
            //开始写入内容到文件；
            outPutStream.write(bytes);
            //一定要关闭输出流；
            outPutStream.close();
        }
        catch(Exception e) {
            //获取异常
            e.printStackTrace();
        }
    }

    //生成主图代码
    public static String total(){
        /**

         *@描述  生成主图代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        String begin = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" id=\"sample-diagram\" targetNamespace=\"http://bpmn.io/schema/bpmn\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">\n";
        lines.append(begin);
        lines.append(collaboration());
        lines.append(process());
        lines.append(position());
        String end = "</bpmn2:definitions>";
        lines.append(end);
        return lines.toString();
    }

    //collaboration
    public static String collaboration(){
        return "  <bpmn2:collaboration id=\"Collaboration_03bi9q3\" "+parameter()+">\n" +
                "    <bpmn2:participant id=\"Participant_15oueic\" processRef=\"Process_1\" />\n" +
                "  </bpmn2:collaboration>\n";
    }

    //parameter
    public static String parameter(){
        /**

         *@描述  生成参数模块代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        lines.append("Parameter=\"");
        for(int i = 0; i< ToBPMN.ParameterList.size(); i++){
            Parameter parameter = ToBPMN.ParameterList.get(i);
            lines.append("@"+parameter.getName()+":"+parameter.getType()+"@;&#10;");
        }
        lines.append("\"");
        return lines.toString();
    }

    //process
    public static String process(){
        /**

         *@描述  生成主流程部分代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        String begin = "  <bpmn2:process id=\"Process_1\" isExecutable=\"false\">\n";
        lines.append(begin);
        lines.append(laneSet());
        lines.append(elementList());
        lines.append(sequenceFlow());
        String end = "  </bpmn2:process>\n";
        lines.append(end);
        return lines.toString();
    }

    //laneSet
    public static String laneSet(){
        /**

         *@描述  生成泳道部分代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        String begin = "    <bpmn2:laneSet id=\"LaneSet_109303u\">\n";
        lines.append(begin);
        for(int i = 0; i< ToBPMN.LaneList.size(); i++){
            String laneString = lane(ToBPMN.LaneList.get(i));
            lines.append(laneString);
        }
        String end = "    </bpmn2:laneSet>\n";
        lines.append(end);
        return lines.toString();
    }

    //lane
    public static String lane(Lane lane){
        /**

         *@描述  生成池部分代码

         *@参数  [lane]

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        String begin = "      <bpmn2:lane id=\""+lane.getId()+"\" name=\""+lane.getName()+"\">\n";
        lines.append(begin);
        for(int i=0;i<lane.getElementIdList().size();i++){
            String line = "        <bpmn2:flowNodeRef>"+lane.getElementIdList().get(i)+"</bpmn2:flowNodeRef>\n";
            lines.append(line);
        }
        String end = "      </bpmn2:lane>\n";
        lines.append(end);
        return lines.toString();
    }

    //newProcessList
    public static String elementList(){
        /**

         *@描述  生成元素集合部分代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        for(int i=0;i<newProcessList.size();i++){
            NewProcess newProcess = newProcessList.get(i);
            lines.append("    <bpmn2:"+newProcess.getType()+" id=\""+newProcess.getId()+"\"");
            if(!isEmpty(newProcess.getName())){
                lines.append(" name=\""+newProcess.getName()+"\" ");
            }
            if(newProcess.getEventList().size()!=0){
                lines.append("Event=\"");
                for(int j=0;j<newProcess.getEventList().size();j++){
                    lines.append("$"+newProcess.getEventList().get(j)+"$;");
                }
                lines.append("\"");
            }
            lines.append(">\n");
            for(int j=0;j<newProcess.getIncomingList().size();j++){
                lines.append("      <bpmn2:incoming>"+newProcess.getIncomingList().get(j)+"</bpmn2:incoming>\n");
            }

            for(int k=0;k<newProcess.getOutgoingList().size();k++){
                lines.append("      <bpmn2:outgoing>"+newProcess.getOutgoingList().get(k)+"</bpmn2:outgoing>\n");
            }

            lines.append("    </bpmn2:"+newProcess.getType()+">\n");
        }
        return lines.toString();
    }

    //sequenceFlow
    public static String sequenceFlow(){
        /**

         *@描述   生成顺序流部分代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        for(int i=0;i<SequenceList.size();i++){
            Sequence sequence = SequenceList.get(i);
            lines.append("    <bpmn2:sequenceFlow id=\""+sequence.getId()+"\"");
            if(!isEmpty(sequence.getName()))
                lines.append(" name=\""+sequence.getName()+"\"");
            lines.append(" sourceRef=\""+sequence.getSourceId()+"\" targetRef=\""+sequence.getTargetId()+"\" />\n");
        }
        return lines.toString();
    }

    //position
    public static String position(){
        /**

         *@描述  生成总体自动布图位置代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        int count = 0;
        for(int i = 0; i< ToBPMN.LaneList.size(); i++){
            int size = ToBPMN.LaneList.get(i).getElementIdList().size();
            if (count < size){
                count = size;
            }
        }
        int width = 200*(count + 1) + 30;
        int widthLane = width - 30;
        lines.append("  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n" +
                "    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"Collaboration_03bi9q3\">\n" +
                "      <bpmndi:BPMNShape id=\"Participant_15oueic_di\" bpmnElement=\"Participant_15oueic\" isHorizontal=\"true\">\n" +
                "        <dc:Bounds x=\"0\" y=\"0\" width=\""+width+"\" height=\""+200* ToBPMN.LaneList.size()+"\" />\n" +
                "      </bpmndi:BPMNShape>\n");
        for(int i = 0; i< ToBPMN.LaneList.size(); i++){
            lines.append("      <bpmndi:BPMNShape id=\""+ ToBPMN.LaneList.get(i).getId()+"_di\" bpmnElement=\""+ ToBPMN.LaneList.get(i).getId()+
                    "\" isHorizontal=\"true\">\n");
            lines.append("        <dc:Bounds x=\"30\" y=\""+i*200+"\" width=\""+widthLane+"\" height=\"200\" />\n");
            lines.append("      </bpmndi:BPMNShape>\n");
        }
        lines.append(elementPosition());
        lines.append(edgePosition());
        lines.append("    </bpmndi:BPMNPlane>\n" +
                "  </bpmndi:BPMNDiagram>\n");
        return lines.toString();
    }

    //elementPosition
    public static String elementPosition(){
        /**

         *@描述  生成元素自动布图位置代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        for(int i = 0; i< ToBPMN.LaneList.size(); i++){
            List<String> elementIdList = ToBPMN.LaneList.get(i).getElementIdList();
            int flag = 0;
            //count用于控制第一个泳道除开始外的元素从280开始 其余泳道从280开始
            int count = 0;
            int y = i * 200 + 100;
            for(int j=0;j<elementIdList.size();j++){
                NewProcess newProcess = elementIdToObject(elementIdList.get(j));
                if (flag == 0 && newProcess.getType().equals("startEvent")){
                    flag = 1;
                    count=1;
                    newProcess.setX(60);
                    newProcess.setY(82);
                    lines.append("      <bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\">\n" +
                            "        <dc:Bounds x=\"60\" y=\"82\" width=\"36\" height=\"36\" />\n");
                    if(!isEmpty(newProcess.getName())){
                        lines.append(label(60,130));
                    }

                    lines.append("      </bpmndi:BPMNShape>\n");
                    continue;
                }
                int x = 260+200*(j-count);
                newProcess.setX(x);
                int y2 = y - newProcess.getHeight()/2;
                newProcess.setY(y2);
                lines.append("      <bpmndi:BPMNShape id=\""+newProcess.getId()+"_di\" bpmnElement=\""+newProcess.getId()+"\">\n" +
                        "        <dc:Bounds x=\""+x+"\" y=\""+y2+"\" width=\""+newProcess.getWidth()+"\" height=\""+newProcess.getHeight()+"\" />\n");
                if(!newProcess.getType().equals("task")&&!isEmpty(newProcess.getName())){
                    lines.append(label(x,y2 + newProcess.getHeight()+10));
                }

                lines.append("      </bpmndi:BPMNShape>\n");
            }
        }
        return lines.toString();
    }

    //label
    public static String label(int x,int y){
        /**

         *@描述   生成顺序流及元素标签自动布图位置代码

         *@参数  [x, y]

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        return "        <bpmndi:BPMNLabel>\n" +
                "          <dc:Bounds x=\""+x+"\" y=\""+y+"\" width=\"62\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n";
    }

    //edgePosition
    public static String edgePosition(){
        /**

         *@描述  生成顺序流自动布图位置代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        for(int i=0;i<SequenceList.size();i++){
            Sequence sequence = SequenceList.get(i);
            lines.append("      <bpmndi:BPMNEdge id=\""+sequence.getId()+"_di\" bpmnElement=\""+sequence.getId()+"\">\n");
            NewProcess source = elementIdToObject(sequence.getSourceId());
            NewProcess target = elementIdToObject(sequence.getTargetId());
            //如果source和target在同一个泳道 就左右连线
            if(source.getLaneId().equals(target.getLaneId())){
                int x = source.getX()+source.getWidth();
                int y = source.getY()+ source.getHeight()/2;
                lines.append("        <di:waypoint x=\""+x+"\" y=\""+y+"\" />\n");
                x = target.getX();
                y = target.getY()+ target.getHeight()/2;
                lines.append("        <di:waypoint x=\""+x+"\" y=\""+y+"\" />\n");
                lines.append("      </bpmndi:BPMNEdge>\n");
            }
            //如果不在一个泳道 就上下连线
            else{
                int targetIndex = laneIdToIndex(target.getLaneId());
                int sourceIndex = laneIdToIndex(source.getLaneId());
                if(sourceIndex<targetIndex){
                    int x = source.getX()+source.getWidth()/2;
                    int y = source.getY()+ source.getHeight();
                    lines.append("        <di:waypoint x=\""+x+"\" y=\""+y+"\" />\n");
                    x = target.getX()+target.getWidth()/2;
                    y = target.getY();
                    lines.append("        <di:waypoint x=\""+x+"\" y=\""+y+"\" />\n");
                    lines.append("      </bpmndi:BPMNEdge>\n");
                }
                else{
                    int x = source.getX()+source.getWidth()/2;
                    int y = source.getY();
                    lines.append("        <di:waypoint x=\""+x+"\" y=\""+y+"\" />\n");
                    x = target.getX()+target.getWidth()/2;
                    y = target.getY()+target.getHeight();
                    lines.append("        <di:waypoint x=\""+x+"\" y=\""+y+"\" />\n");
                    lines.append("      </bpmndi:BPMNEdge>\n");
                }
            }
        }
        return lines.toString();
    }



    //生成子流程图代码
    public static String subTotal(){
        /**

         *@描述  生成子流程图主框架代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        String begin = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" id=\"sample-diagram\" targetNamespace=\"http://bpmn.io/schema/bpmn\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">\n";
        lines.append(begin);
        lines.append(subProcess());
        lines.append(subPosition());
        String end = "</bpmn2:definitions>";
        lines.append(end);
        return lines.toString();
    }

    //subProcess
    public static String subProcess(){
        /**

         *@描述  生成子流程元素代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        String begin = "  <bpmn2:process id=\"Process_1\" isExecutable=\"false\">\n";
        lines.append(begin);
        lines.append(elementList());
        lines.append(sequenceFlow());
        String end = "  </bpmn2:process>\n";
        lines.append(end);
        return lines.toString();
    }

    //subPosition
    public static String subPosition(){
        /**

         *@描述  生成子流程自动布图位置代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        lines.append("  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n" +
                "    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"Collaboration_03bi9q3\">\n");
        lines.append(subElementPosition());
        lines.append(subEdgePosition());
        lines.append("    </bpmndi:BPMNPlane>\n" +
                "  </bpmndi:BPMNDiagram>\n");
        return lines.toString();
    }

    //subElementPosition
    public static String subElementPosition(){
        /**

         *@描述  生成子流程元素自动布图代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        for(int i=0;i<newProcessList.size();i++){
            int flag = 0;
            //count用于控制第一个泳道除开始外的元素从280开始 其余泳道从280开始
            int y = 100;
            NewProcess newProcess = newProcessList.get(i);
            if (flag == 0 && newProcess.getType().equals("startEvent")){
                flag = 1;
                newProcess.setX(60);
                newProcess.setY(82);
                lines.append("      <bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\">\n" +
                        "        <dc:Bounds x=\"60\" y=\"82\" width=\"36\" height=\"36\" />\n");
                if(!isEmpty(newProcess.getName())){
                    lines.append(label(60,130));
                }

                lines.append("      </bpmndi:BPMNShape>\n");
                continue;
            }
            int x = 60+200*i;
            newProcess.setX(x);
            int y2 = y - newProcess.getHeight()/2;
            newProcess.setY(y2);
            lines.append("      <bpmndi:BPMNShape id=\""+newProcess.getId()+"_di\" bpmnElement=\""+newProcess.getId()+"\">\n" +
                    "        <dc:Bounds x=\""+x+"\" y=\""+y2+"\" width=\""+newProcess.getWidth()+"\" height=\""+newProcess.getHeight()+"\" />\n");
            if(!newProcess.getType().equals("task")&&!isEmpty(newProcess.getName())){
                lines.append(label(x,y2 + newProcess.getHeight()+10));
            }

            lines.append("      </bpmndi:BPMNShape>\n");
        }
        return lines.toString();
    }

    //subEdgePosition
    public static String subEdgePosition(){
        /**

         *@描述  生成子流程顺序流自动布图代码

         *@参数  []

         *@返回值  java.lang.String

         *@创建时间  2021/9/21

         *@其他

         */
        StringBuilder lines = new StringBuilder();
        for(int i=0;i<SequenceList.size();i++){
            Sequence sequence = SequenceList.get(i);
            lines.append("      <bpmndi:BPMNEdge id=\""+sequence.getId()+"_di\" bpmnElement=\""+sequence.getId()+"\">\n");
            NewProcess source = elementIdToObject(sequence.getSourceId());
            NewProcess target = elementIdToObject(sequence.getTargetId());
            int x = source.getX()+source.getWidth();
            int y = source.getY()+ source.getHeight()/2;
            lines.append("        <di:waypoint x=\""+x+"\" y=\""+y+"\" />\n");
            x = target.getX();
            y = target.getY()+ target.getHeight()/2;
            lines.append("        <di:waypoint x=\""+x+"\" y=\""+y+"\" />\n");
            lines.append("      </bpmndi:BPMNEdge>\n");
        }
        return lines.toString();
    }




    //根据泳道id找到它在泳道列表中的索引
    public static int laneIdToIndex(String laneId){
        /**

         *@描述  根据泳道id找到它在泳道列表中的索引

         *@参数  [laneId]

         *@返回值  int

         *@创建时间  2021/9/21

         *@其他

         */
        for(int i = 0; i< ToBPMN.LaneList.size(); i++){
            if(ToBPMN.LaneList.get(i).getId().equals(laneId)){
                return i;
            }

        }
        return -1;//应该不会发生
    }


    //根据随机数id在总表中查找该元素
    public static NewProcess elementIdToObject(String id){
        /**

         *@描述  根据随机数id在总表中查找该元素

         *@参数  [id]

         *@返回值  Entity.NewProcess

         *@创建时间  2021/9/21

         *@其他

         */
        for(int i=0;i< newProcessList.size();i++){
            if(newProcessList.get(i).getId().equals(id)){
                return newProcessList.get(i);
            }

        }
        return null;//应该不会发生
    }

    public static boolean isEmpty(String item){
        if(item==null||item.length()==0){
            return true;
        }

        return false;
    }
}
