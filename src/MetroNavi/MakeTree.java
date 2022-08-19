package MetroNavi;

import java.util.*;

public class MakeTree {

    Node root = new Node(); //root 노드


    /*void initRoot()
     * root 노드 초기화*/
    public void initRoot() {
        root.initRoot(ScheduleManager.departureStaionName, ScheduleManager.startHour, ScheduleManager.startMinute, ScheduleManager.weekType);   //root노드 초기화
        ScheduleManager.queue.offer(root);    //queue에 root노드 넣음
    }


    /*public static void addChild(Node parent, ArrayList<SubwayData> childs)
     * 부모 노드에 자식노드(갈 수 있는 경로) 추가*/
    public static void addChild(Node parent, ArrayList<SubwayData> childs) {
        if (!ScheduleManager.visit[parent.data.stationDetailId]) {
            for (SubwayData sd : childs) {
                if (parent.data.stationName.equals("응암") || parent.data.stationName.equals("구산") || sd.stationDetailId == 201 || parent.data.stationName.equals("독바위") || sd.stationDetailId == 203 || parent.data.stationName.equals("역촌")) {
                    if ((sd.beforeStation == 201 || sd.beforeStation == 367 || sd.beforeStation == 203 || sd.beforeStation == 366)) { //상행
                        parent.child.add(new Node(sd, true));
                    } else if (sd.nextStation == 366 || sd.nextStation == 368 || sd.nextStation == 367 || sd.nextStation == 201 || sd.nextStation == 365 || sd.nextStation == 203 || sd.nextStation == 369) {
                        parent.child.add(new Node(sd, true));
                    }
                } else {
                    if (sd.nextStation != 0) {   //하행
                        parent.child.add(new Node(sd, true));
                    }
                    if (sd.beforeStation != 0) {   //상행
                        parent.child.add(new Node(sd, false));
                    }
                }
            }

            for (Node child : parent.child) {
                child.parentNode = parent;
                if (ScheduleManager.dstLineNum.contains(child.data.lineId)) {    //도착역과 같은 호선
                    ScheduleManager.priorQ.offer(child);    //우선큐 추가
                } else {  //도착역과 다른 호선
                    ScheduleManager.queue.offer(child); //그냥 큐 추가
                }
            }
            if (parent.data.stationDetailId == 1487) {
                ScheduleManager.visit[0] = true;
            } else if (parent.data.stationDetailId == 3651) {
                ScheduleManager.visit[1043] = true;
            } else if(parent.data.stationId != 0){
                ScheduleManager.visit[parent.data.stationId] = true;
            }
        }
    }

    /*public static void makeTree()
     * */
    public static void makeTree() {
        while (true) {
            Node station = ScheduleManager.queue.poll();
            addChild(station, ScheduleManager.searchPossibleRoute(station));
            while (true) {
                if(!ScheduleManager.priorQ.isEmpty()) {
                    ScheduleManager.onePath(ScheduleManager.priorQ.poll()); //한방에 경로 찾기
                }else if (!ScheduleManager.queue.isEmpty()){
                    ScheduleManager.onePath(ScheduleManager.queue.poll());
                }
                if (ScheduleManager.path.size() == 7) {
                    break;
                }
            }
        }
    }
}


