package MetroNavi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class TEST {
    /*public static void ininialize()
     * 입력 받기*/
    public static void initialize(String[] args) {
        MakeTree mk = MakeTree.getInstance();
        ScheduleManager sm = ScheduleManager.getInstance();
        System.out.print("출발역, 도착역, 시, 분, 요일 : ");
        Scanner input = new Scanner(System.in);

/*        sm.departureStaionName = args[0];
        sm.destinationStationName = args[1];
        sm.startHour = Integer.parseInt(args[2]);
        sm.startMinute = Integer.parseInt(args[3]);
        sm.weekType = args[4];*/

        sm.departureStaionName = input.next();
        sm.destinationStationName = input.next();
        sm.startHour = input.nextInt();
        sm.startMinute = input.nextInt();
        sm.weekType = input.next();

        mk.initRoot();  //root노드 초기화
        sm.searchDstLineNum();  //도착역 호선 탐색
    }
    /*psvm
     * 메인 메서드*/
    public static void main(String[] args) {
        MakeTree mk = MakeTree.getInstance();
        ScheduleManager sm = ScheduleManager.getInstance();
        sm.departureStaionName = "이성목 만세";
        databaseManager.connectDatabase();  //DB 연결
        initialize(args);
        ArrayList<pathInfo> pathInfos = mk.makeTree();
        Collections.sort(pathInfos, new Comparator<pathInfo>() {
            @Override
            public int compare(pathInfo o1, pathInfo o2) {
                if(o1.duration == o2.duration) {
                    return o1.transferNum - o2.transferNum;
                }
                else {
                    return o1.duration - o2.duration;
                }
            }
        });
        pathInfo shortestPath = pathInfos.get(0);

        Collections.sort(pathInfos, new Comparator<pathInfo>() {
            @Override
            public int compare(pathInfo o1, pathInfo o2) {
                if(o1.transferNum == o2.transferNum) {
                    return o1.duration - o2.duration;
                }
                else {
                    return o1.transferNum - o2.transferNum;
                }
            }
        });
        pathInfo lowTransferPath = pathInfos.get(0);


        Collections.sort(pathInfos, new Comparator<pathInfo>() {
            @Override
            public int compare(pathInfo o1, pathInfo o2) {
                if(o1.duration == o2.duration) {
                    int result = o1.congest - o2.congest > 0 ? 1 : -1;
                    return result;
                }
                else {
                    return o1.duration - o2.duration;
                }
            }
        });
        pathInfo lowCongestPath = null;
        for(pathInfo PTH : pathInfos) {
            int time = shortestPath.duration + shortestPath.duration/10;
            double congest = shortestPath.congest;
            if(PTH.congest - congest < 0.001 & PTH.duration <= time) {
                lowCongestPath = PTH;
            }
        }

        if( 99999.0 - lowCongestPath.congest < 0.001)
            MakeJson.Path(shortestPath, lowTransferPath);
        else
            MakeJson.Path(shortestPath, lowTransferPath, lowCongestPath);

        ScheduleManager.reset();
        MakeTree.reset();
    }
}
