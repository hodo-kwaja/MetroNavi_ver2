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

    /*public static void onePath(Node station)
    * 도착역까지 한방에 경로 찾기*/

    public static void makeTree() {
        while(true) {
            Node station = ScheduleManager.queue.poll();
            ScheduleManager.searchPossibleRoute(station);
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


