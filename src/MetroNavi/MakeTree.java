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
    public static void addChild(Node parent, ArrayList<SubwayData> childs, boolean b) {
        if(childs.size() > 1) {
            if(childs.get(0).beforeStation == childs.get(1).beforeStation) {
                childs.get(1).beforeStation = 0;
            }
            if(childs.get(0).nextStation == childs.get(1).nextStation) {
                childs.get(1).nextStation = 0;
            }
        }
             for (SubwayData sd : childs) {
                 int idx;
                 if(sd.stationDetailId == 3651) {
                    idx = 0;
                 }
                 else if(sd.stationDetailId == 1487) {
                     idx = 1043;
                 }
                 else {
                     idx = sd.stationDetailId;
                 }
                 if (!ScheduleManager.visit[idx]) {
                     if (parent.data.stationName.equals("응암") || parent.data.stationName.equals("구산") || sd.stationDetailId == 201 || parent.data.stationName.equals("독바위") || sd.stationDetailId == 203 || parent.data.stationName.equals("역촌")) {
                         if ((sd.beforeStation == 201 || sd.beforeStation == 367 || sd.beforeStation == 203 || sd.beforeStation == 366)) { //상행
                             parent.child.add(new Node(sd, true, parent));
                         } else if (sd.nextStation == 366 || sd.nextStation == 368 || sd.nextStation == 367 || sd.nextStation == 201 || sd.nextStation == 365 || sd.nextStation == 203 || sd.nextStation == 369) {
                             parent.child.add(new Node(sd, true, parent));
                         }
                     } else {
                         if (sd.nextStation != 0) {   //하행
                             parent.child.add(new Node(sd, true, parent));
                         }
                         if (sd.beforeStation != 0) {   //상행
                             parent.child.add(new Node(sd, false, parent));
                         }
                     }
                     ScheduleManager.visit[idx] = true;
                 }
             }

            for (Node child1 : parent.child) {
                child1.parentNode = parent;
                if (ScheduleManager.dstLineNum.contains(child1.data.lineId)) {    //도착역과 같은 호선
                    ScheduleManager.priorQ.offer(child1);    //우선큐 추가
                } else {  //도착역과 다른 호선
                    ScheduleManager.queue.offer(child1); //그냥 큐 추가
                }
                child1.line.add(child1.data.lineId);
            }
    }


    /*public static void makeTree()
     * */
    public static void makeTree() {
        while (true) {
            Node station = ScheduleManager.queue.poll();
            addChild(station, ScheduleManager.searchPossibleRoute(station), true);
            long start = System.currentTimeMillis();
            long end = start + 1 * 1500;
            while (System.currentTimeMillis() < end) {  //1.5초 실행
                if(!ScheduleManager.priorQ.isEmpty()) { //도착역과 같은 호선
                    ScheduleManager.onePath(ScheduleManager.priorQ.poll());
                }else if (!ScheduleManager.queue.isEmpty()){    //도착역과 다른 호선
                    ScheduleManager.onePath(ScheduleManager.queue.poll());
                }
            }
            ScheduleManager.abd();
        }
    }
}


