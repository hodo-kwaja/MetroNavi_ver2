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
        for(SubwayData sd : childs) {
            if(sd.nextStation != 0) {   //하행
                parent.child.add(new Node(sd, true));
            }
            if(sd.beforeStation != 0) { //상행
                parent.child.add(new Node(sd, false));
            }
        }
        for(Node child : parent.child) {
            ScheduleManager.getScheduleData(parent.data, child.data);   //시간표 업데이트
            child.parentNode = parent;
            if(ScheduleManager.dstLineNum.contains(child.data.lineId)) {    //도착역과 같은 호선
                ScheduleManager.priorQ.offer(child);    //우선큐 추가
            }
            else {  //도착역과 다른 호선
                ScheduleManager.queue.offer(child); //그냥 큐 추가
            }
        }
    }

    /*public static void makeTree()
    * */
    public static void makeTree() {
        while(true) {
            Node station = ScheduleManager.queue.poll();
            addChild(station, ScheduleManager.searchPossibleRoute(station));
            while(!ScheduleManager.priorQ.isEmpty()) {
                ScheduleManager.onePath(ScheduleManager.priorQ.poll()); //한방에 경로 찾기
            }
            while(!ScheduleManager.queue.isEmpty()) {
                //한 라운드 실행
            }
            if(ScheduleManager.path.size() == 5) {
                break;
            }
        }
    }
}


