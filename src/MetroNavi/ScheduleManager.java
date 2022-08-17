package MetroNavi;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ScheduleManager {

    static String departureStaionName; //출발역 이름
    static String destinationStationName;  //도착역 이름
    static ArrayList<Integer> dstLineNum;
    static int startHour, startMinute; //출발 시각(시, 분)
    static String weekType;    //요일
    static Node[] shortestNode = new Node[644];   //역까지 최단 시간으로 도착하는 노드들
    static ArrayList<Node> path = new ArrayList<>();   //도착역에 도착한 노드들
    static Queue<Node> queue = new LinkedList<>(); //도착역과 호선이 다른 경로
    static Queue<Node> priorQ = new LinkedList<>();    //도착역과 호선이 같은 경로


    /*public static void updateSchedule(SubwayData station)
    * candiSchedule에서 시간이 가장 빠른 것을 schedule로 결정*/
    public static void updateSchedule(SubwayData station) {
        int score = 9999;
        TimeTable schedule = null;
        for(TimeTable sch : station.candiSchedule) {
            if(score > TimeAndDate.convertTimeToScore(sch.hour, sch.minute)) {  //가장 도착 시간이 빠른 것
                score = TimeAndDate.convertTimeToScore(sch.hour, sch.minute);
                schedule = sch;
            }
        }
        station.schedule = schedule;
    }

    /*public static ArrayList<TimeTable> refineSchedule (ArrayList<TimeTable> schedules)
    * 조건 중복되는 시간표 제거*/
    public static ArrayList<TimeTable> refineSchedule (ArrayList<TimeTable> schedules) {
        ArrayList<TimeTable> newSchedule = new ArrayList<>();
        newSchedule.add(schedules.get(0));
        try {
            if (schedules.get(0).scheduleName.equals(schedules.get(1).scheduleName)) {  //0, 1 종점 같음
                if (!schedules.get(0).typeName.equals(schedules.get(1).typeName)) { //0, 1 타입 다름
                    newSchedule.add(schedules.get(1));
                }
            }
            else {  //종점 다름
                newSchedule.add(schedules.get(1));
            }
            if (schedules.get(0).scheduleName.equals(schedules.get(2).scheduleName)) {  //0, 2 종점 같음
                if (!schedules.get(0).typeName.equals(schedules.get(2).typeName)) { //0, 2 타입 다름
                    if (schedules.get(1).scheduleName.equals(schedules.get(2).scheduleName)) {  //1, 2 종점 같음
                        if (!schedules.get(1).typeName.equals(schedules.get(2).typeName)) { //1, 2 종점 다름
                            newSchedule.add(schedules.get(2));
                        }
                    }
                    else {  //1, 2 종점 다름
                        newSchedule.add(schedules.get(2));
                    }
                }
            }
            else {  //0, 2 종점 다름
                if (schedules.get(1).scheduleName.equals(schedules.get(2).scheduleName)) {
                    if (!schedules.get(1).typeName.equals(schedules.get(2).typeName)) {
                        newSchedule.add(schedules.get(2));
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {}

        return newSchedule;
    }

    /*public static void updatePathInfo(SubwayData parent, SubwayData child)
    * 경로 정보 업데이트*/
    public static void updatePathInfo(SubwayData parent, SubwayData child) {
        for(int i = 0; i < parent.candiSchedule.size(); i++) {
            child.candiSchedule.get(i).numStep = parent.candiSchedule.get(i).numStep + 1;   //경유역 수
            child.candiSchedule.get(i).transferNum = parent.candiSchedule.get(i).transferNum;   //환승 수
            child.candiSchedule.get(i).duration = TimeAndDate.convertTimeToScore(child.candiSchedule.get(i).hour, child.candiSchedule.get(i).minute)
                    - TimeAndDate.convertTimeToScore(parent.candiSchedule.get(i).hour, parent.candiSchedule.get(i).minute);    //소요시간
        }
    }

    /*public static void updateCongest(SubwayData child)
    * 혼잡도 가져오기*/
    public static void updateCongest(SubwayData station) {
        for(TimeTable tt : station.candiSchedule) {
            String time = TimeAndDate.conver30Time(tt.hour, tt.minute); //30분 단위로 시간 끊기
            tt.congest = databaseManager.getCongestDB(time, station);   //혼잡도 가져오기

            if(tt.congest != 0.0) {
                if (tt.congest >= 80.0) {
                    tt.congestScore = 3;   //매우 혼잡
                } else if (tt.congest >= 50.0 && tt.congest < 80.0) {
                    tt.congestScore = 2;   //보통
                } else if (tt.congest < 50.0) {
                    tt.congestScore = 1;   //원활
                }
            }
             else {
                 tt.congestScore = 0;   //결과 없음
             }
        }
    }

    /*public static void getScheduleData(SubwayData parent, SubwayData child)
    * */
    public static void getScheduleData(SubwayData parent, SubwayData child) {
        ArrayList<TimeTable> candiSchedules = refineSchedule(databaseManager.getScheduleDB(parent, child)); //시간표 가져오기 AND 중복 조건 시간표 제거
        child.candiSchedule = candiSchedules;   //candiSchedule 업데이트
        updatePathInfo(parent, child);  //경로 정보 업데이트
        updateCongest(child);   //혼잡도 업데이트
        updateSchedule(child);  //schedule 업데이트
    }

    /*public static boolean checkTransfer(String stationName)
    * 환승역 여부 확인*/
    public static boolean checkTransfer(String stationName) {
        if(databaseManager.searchLineNumDB(stationName).size() > 1) {
            return true;    //환승역
        }
        else {
            return false;   //환승역 아님
        }
    }

    /*public static ArrayList<Node> searchPossibleRoute(String stationName)
     * 역에서 갈 수 있는 경로 찾아서 반환*/
    public static ArrayList<SubwayData> searchPossibleRoute(Node parent) {
        return databaseManager.getSubLineNameInfoDB(parent.data.stationName);   //이름으로 역 정보 가져옴
    }


    /*public void searchDstLineNum()
     * 도착역에 무슨 라인이 지나가나 탐색*/
    public void searchDstLineNum() {
        dstLineNum = databaseManager.searchLineNumDB(destinationStationName);    //도착역에 무슨 라인이 지나가나 탐색
    }

    /*public static void filterTransfer(ArrayList<SubwayData> route, Node station)
    * 갈 수 잆는 경로 중 현재 노선이랑 다른 경로만 큐에 추가*/
    public static void filterTransfer(ArrayList<SubwayData> route, Node station) {
        ArrayList<SubwayData> newRoute = new ArrayList<>();
        for(SubwayData r : route) {
            if(r.lineId != station.data.lineId) {
                newRoute.add(r);
            }
        }
        MakeTree.addChild(station, newRoute);
    }

    /*public static void onePath(Node station)
     * 도착역까지 한방에 경로 찾기*/
    public static void onePath(Node station) {
        if(station.lineDirection == 0) {    //하행
            Stack<SubwayData> stepPath = new Stack<>();
            SubwayData temp = new SubwayData();
            SubwayData previous = station.data;

            while(true) {
                temp = databaseManager.getStationWithDetailIdDB(previous.nextStation);
                temp.lineDirection = 0;
                if(temp.stationName.contains(destinationStationName)) { //목적지
                    ScheduleManager.getScheduleData(previous, temp);
                    Node destination = new Node(temp);
                    station.child.add(destination);
                    destination.parentNode = station;
                    path.add(destination);
                    break;
                }
                else {  //목적지 아님
                    ScheduleManager.getScheduleData(previous, temp);
                    if(temp.schedule.scheduleName.contains(temp.stationName)) { //종점일때
                        if(ScheduleManager.checkTransfer(temp.stationName)) {   //환승역
                            Node transfer = new Node(temp);
                            filterTransfer(searchPossibleRoute(transfer), transfer);  //현재 역이랑 다른 노선만 큐에 추가
                        }
                        else {  //환승역 아님
                            break;
                        }
                    }
                    else {  //종점 아님
                        previous = temp;
                        temp = databaseManager.getStationWithDetailIdDB(temp.nextStation);
                    }
                    stepPath.push(temp);
                }
            }
        }
        else {  //상행
            Stack<SubwayData> stepPath = new Stack<>();
            SubwayData temp = new SubwayData();
            SubwayData previous = station.data;

            while(true) {
                temp = databaseManager.getStationWithDetailIdDB(previous.beforeStation);
                temp.lineDirection = 1;
                if(temp.stationName.contains(destinationStationName)) { //목적지
                    ScheduleManager.getScheduleData(previous, temp);
                    Node destination = new Node(temp);
                    station.child.add(destination);
                    destination.parentNode = station;
                    path.add(destination);
                    break;
                }
                else {  //목적지 아님
                    ScheduleManager.getScheduleData(previous, temp);
                    if(temp.schedule.scheduleName.contains(temp.stationName)) { //종점일때
                        break;
                    }
                    else {
                        previous = temp;
                        temp = databaseManager.getStationWithDetailIdDB(temp.beforeStation);
                    }
                    stepPath.push(temp);
                }
            }
        }
    }
}