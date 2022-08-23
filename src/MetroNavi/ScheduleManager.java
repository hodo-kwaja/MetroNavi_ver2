package MetroNavi;

import java.sql.Time;
import java.util.*;

public class ScheduleManager {

    static boolean[] visit = new boolean[1045]; //지나온 역
    static String departureStaionName; //출발역 이름
    static String destinationStationName;  //도착역 이름
    static ArrayList<Integer> dstLineNum;
    static int startHour, startMinute; //출발 시각(시, 분)
    static String weekType;    //요일
    static ArrayList<Node> path = new ArrayList<>();   //도착역에 도착한 노드들
    static Queue<Node> queue = new LinkedList<>(); //도착역과 호선이 다른 경로
    static Queue<Node> priorQ = new LinkedList<>();    //도착역과 호선이 같은 경로
    static ArrayList<Stack<SubwayData>> finalPath = new ArrayList<>();  //최종 도출 경로

    /*public static void updateSchedule(SubwayData station)
     * candiSchedule에서 시간이 가장 빠른 것을 schedule로 결정*/
    public static void updateSchedule(SubwayData station) {
        int score = 9999;
        TimeTable schedule = null;
        for (TimeTable sch : station.candiSchedule) {
            if (score > TimeAndDate.convertTimeToScore(sch.hour, sch.minute)) {  //가장 도착 시간이 빠른 것
                score = TimeAndDate.convertTimeToScore(sch.hour, sch.minute);
                schedule = sch;
            }
        }
        station.schedule = schedule;
    }

    public static SubwayData giveMe(TimeTable TT) {
        ArrayList<SubwayData> subs = databaseManager.getSubLineNameInfoDB(TT.scheduleName);
        SubwayData result = new SubwayData();
        for(SubwayData SD : subs) {
            if(SD.lineId == TT.line_id) {
                result =  SD;
            }
        }
        return result;
    }

    /*public static ArrayList<TimeTable> refineSchedule (ArrayList<TimeTable> schedules)
     * 조건 중복되는 시간표 제거*/
    public static TimeTable refineSchedule(ArrayList<TimeTable> schedules, int SDI) {
        TimeTable newSchedule = new TimeTable();
        SubwayData transtation = databaseManager.getStationWithDetailIdDB(SDI);

        for(int i = 0; i < schedules.size(); i++) {
            TimeTable TT = schedules.get(i);

            if (TT.line_direction == 0) {    //하행

                if (TT.line_id == 104) { //경의중앙
                    SubwayData sub = giveMe(TT);
                    int result = sub.stationCode.compareTo(transtation.stationCode);

                    if (TT.typeName.equals("E")) {
                        if(transtation.express == 1) {
                            if (transtation.stationCode.contains("P")) {    //신촌, 서울역
                                if (TT.scheduleName.equals("서울역")) {
                                    newSchedule = TT;
                                    break;
                                }
                            } else {
                                if (result >= 0 && !sub.stationCode.contains("P")) {
                                    newSchedule = TT;
                                    break;
                                }
                            }
                        }
                    }

                    else {
                        if (transtation.stationCode.contains("P")) {    //신촌, 서울역
                            if (TT.scheduleName.equals("서울역")) {
                                newSchedule = TT;
                                break;
                            }
                        } else {
                            if (result >= 0 && !sub.stationCode.contains("P")) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    }
                }

                else {
                    if (TT.typeName.equals("E")) {  //급행
                        if(transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result >= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    }

                    else if (TT.typeName.equals("S")) { //특급
                        if(transtation.special == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result >= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    }

                    else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);

                        if (result >= 0) {
                            newSchedule = TT;
                            break;
                        }
                    }
                }
            }

            else {  //상행
                if(TT.line_id == 104) { //경의중앙
                    if (TT.typeName.equals("E")) {  //급행
                        if(transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);
                            if (result >= 0 && !sub.stationCode.contains("P")) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    }

                    else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);
                        if (result >= 0 && !sub.stationCode.contains("P")) {
                            newSchedule = TT;
                            break;
                        }
                    }
                }

                else {
                    if (TT.typeName.equals("E")) {  //급행
                        if(transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result <= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    }

                    else if (TT.typeName.equals("S")) { //특급
                        if(transtation.special == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result <= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    }

                    else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);

                        if (result <= 0) {
                            newSchedule = TT;
                            break;
                        }
                    }
                }
            }
        }
        return newSchedule;
    }

    /*public static void updatePathInfo(SubwayData parent, SubwayData child)
     * 경로 정보 업데이트*/
    public static void updatePathInfo(SubwayData parent, SubwayData child) {
            child.schedule.numStep = parent.schedule.numStep + 1;   //경유역 수
            child.schedule.transferNum = parent.schedule.transferNum;   //환승 수
            child.schedule.duration = TimeAndDate.convertTimeToScore(child.schedule.hour, child.schedule.minute)
                    - TimeAndDate.convertTimeToScore(parent.schedule.hour, parent.schedule.minute);    //소요시간
    }

    /*public static void updateCongest(SubwayData child)
     * 혼잡도 가져오기*/
    public static void updateCongest(SubwayData station) {
        for (TimeTable tt : station.candiSchedule) {
            String time = TimeAndDate.conver30Time(tt.hour, tt.minute); //30분 단위로 시간 끊기
            tt.congest = databaseManager.getCongestDB(time, station);   //혼잡도 가져오기

            if (tt.congest != 0.0) {
                if (tt.congest >= 80.0) {
                    tt.congestScore = 3;   //매우 혼잡
                } else if (tt.congest >= 50.0 && tt.congest < 80.0) {
                    tt.congestScore = 2;   //보통
                } else if (tt.congest < 50.0) {
                    tt.congestScore = 1;   //원활
                }
            } else {
                tt.congestScore = 0;   //결과 없음
            }
        }
    }

    /*public static boolean checkTransfer(String stationName)
     * 환승역 여부 확인*/
    public static boolean checkTransfer(String stationName) {
        if (databaseManager.searchLineNumDB(stationName).size() > 1) {
            return true;    //환승역
        } else {
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
    public static void filterTransfer(ArrayList<SubwayData> route, Node station, Node parent) {
        ArrayList<SubwayData> transRoute = new ArrayList<>();
        ArrayList<SubwayData> newRoute = new ArrayList<>();
        boolean transfer = false;
        for (SubwayData r : route) {
            r.lineDirection = station.lineDirection;
            if (r.lineId != station.data.lineId) {
                if (!parent.line.contains(r.lineId)) {
                    transRoute.add(r);
                    station.data.transferNum = parent.data.transferNum + 1;
                    r.transferNum = station.data.transferNum;
                    getTransferInfo(station.data, r);
                    r.transfer = true;
                    station.data.transfer = true;
                    transfer = true;
                }
            } else {
                if (station.lineDirection == 0) {    //하행
                    if (station.data.stationDetailId == r.stationDetailId) {    //왔던 방향은 추가 안함
                        r.transferNum = parent.data.transferNum;
                        r.beforeStation = 0;
                        newRoute.add(r);
                    } else {
                        if (r.beforeStation == 510) {    //광운대 -> 상봉 -> 중랑
                            r.nextStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        }
                        else {
                            r.transferNum = parent.data.transferNum;
                            r.beforeStation = 0;
                            newRoute.add(r);
                        }
                    }

                } else {  //상행
                    if (station.data.stationDetailId == r.stationDetailId) {
                        if (r.nextStation == 366) {  //새절 -> 응암 -> 역촌
                            r.transferNum = parent.data.transferNum;
                            r.beforeStation = 0;
                            newRoute.add(r);
                        } else {
                            r.transferNum = parent.data.transferNum;
                            r.nextStation = 0;
                            newRoute.add(r);
                        }
                    } else {
                        if (r.nextStation == 88) { //세마 -> 병점 -> 서동탄
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 72) { //석수 -> 금천구청 -> 광명
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 44) {  //가산디지털단지 -> 구로 -> 구일
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 68) {  //구일 -> 구로 -> 가산디지털단지
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 348) { //둔촌동 -> 강동 -> 길동
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 358) { //길동 -> 강동 -> 둔촌동
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 144) { //건대입구 -> 성수 -> 용답
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 177) { //문래 -> 신도림 -> 도림천
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 190) {  //홍대입구 -> 가좌 -> 신촌(경의중앙)
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.nextStation == 193) {  //신촌(경의중앙) -> 가좌 -> 홍대입구
                            r.beforeStation = 0;
                            transRoute.add(r);
                            station.data.transferNum = parent.data.transferNum + 1;
                            r.transferNum = station.data.transferNum;
                            getTransferInfo(station.data, r);
                            r.transfer = true;
                            station.data.transfer = true;
                            transfer = true;
                        } else if (r.beforeStation == 510) { //망우 -> 상봉 -> 중랑
                            r.transferNum = parent.data.transferNum;
                            r.nextStation = 0;
                            newRoute.add(r);
                        }
                    }
                }
            }
        }
        MakeTree.addChild(parent, newRoute, false);
        if (transfer) {
            station.line.addAll(parent.line);
            parent.child.add(station);
            station.parentNode = parent;
            MakeTree.addChild(station, transRoute, true);
        }
    }

    /*public static void onePath(Node station)
     * 도착역까지 한방에 경로 찾기*/
    public static void onePath(Node station) {
        if (station.lineDirection == 0) {    //하행
            Stack<SubwayData> stepPath = new Stack<>();
            SubwayData temp;
            SubwayData previous = station.data;
            temp = databaseManager.getStationWithDetailIdDB(previous.nextStation);
            while (temp != null) {
                temp.lineDirection = 0;
                if (temp.stationName.contains(destinationStationName)) { //목적지
                    temp.transferNum = station.data.transferNum;
                    Node destination = new Node(temp);
                    station.child.add(destination);
                    destination.parentNode = station;
                    station.step = stepPath;
                    path.add(destination);
                    Arrays.fill(visit, false);
                    break;
                } else {  //목적지 아님
                    if (ScheduleManager.checkTransfer(temp.stationName)) {   //환승역
                        Node transfer = new Node(temp);
                        filterTransfer(searchPossibleRoute(transfer), transfer, station);  //현재 역이랑 다른 노선만 큐에 추가
                        station.step = stepPath;
                        break;
                    } else {  //환승역 아님
                        stepPath.push(temp);
                    }
                    previous = temp;
                    temp.transferNum = station.data.transferNum;
                    temp = databaseManager.getStationWithDetailIdDB(previous.nextStation);
                }
            }
        } else {  //상행
            Stack<SubwayData> stepPath = new Stack<>();
            SubwayData temp;
            SubwayData previous = station.data;
            temp = databaseManager.getStationWithDetailIdDB(previous.beforeStation);
            while (temp != null) {
                temp.lineDirection = 1;
                if (temp.stationName.contains(destinationStationName)) { //목적지
                    temp.transferNum = station.data.transferNum;
                    Node destination = new Node(temp);
                    station.child.add(destination);
                    destination.parentNode = station;
                    station.step = stepPath;
                    path.add(destination);
                    Arrays.fill(visit, false);
                    break;
                } else {  //목적지 아님
                    if (ScheduleManager.checkTransfer(temp.stationName)) {   //환승역
                        Node transfer = new Node(temp);
                        filterTransfer(searchPossibleRoute(transfer), transfer, station);  //현재 역이랑 다른 노선만 큐에 추가
                        station.step = stepPath;
                        break;
                    } else {  //환승역 아님
                        stepPath.push(temp);
                    }
                    previous = temp;
                    temp.transferNum = station.data.transferNum;
                    temp = databaseManager.getStationWithDetailIdDB(previous.beforeStation);
                }
            }
        }
    }

    /*public static void routeOrganization()
    * 경로 정리해서 stack에 담음*/
    public static ArrayList<pathInfo> routeOrganization(){
        ArrayList<Stack<Integer>> transNum = new ArrayList<>();
        for (Node dst : path) {
            Stack<SubwayData> path1 = new Stack<>();
            Stack<Integer> transnum = new Stack<>();
            Node station = dst;
            transnum.push(station.data.stationDetailId);
            while (station.parentNode != null) {
                if (!station.step.isEmpty()) {
                    while (!station.step.isEmpty()) {
                        SubwayData tmp = station.step.pop();
                        path1.push(tmp);
                    }
                }
                if(station.data.transfer) {
                    transnum.push(station.data.stationDetailId);
                }
                path1.push(station.data);
                station = station.parentNode;
            }
            transNum.add(transnum);
            finalPath.add(path1);
        }
        return ScheduleManager.addTimeLine(transNum);   //시간표 추가
    }

    /*public void addTimeline()
    * 경로에 시간표 추가*/
    public static ArrayList<pathInfo> addTimeLine(ArrayList<Stack<Integer>> transNum) {
        ArrayList<pathInfo> pathInfos = new ArrayList<>();
        SubwayData root = MakeTree.root.data;
        for(int i = 0; i < finalPath.size(); i++) {
            Stack<SubwayData> finalroute = finalPath.get(i);    //경로 n번
            Stack<Integer> transtation = transNum.get(i);   //통과역 n번
            pathInfo info1 = new pathInfo();
            SubwayData prev = root; //이번 역
            while(!finalroute.isEmpty()) {
                SubwayData station = finalroute.pop();
                if(station.transfer || prev == root) {
                    station.schedule = refineSchedule(databaseManager.getScheduleDB(prev, station), transtation.pop());
                }
                else {
                    station.schedule = databaseManager.getOneScheduleDB(prev, station);
                    if(station.schedule.line_id == 0) {
                        continue;
                    }
                }
                info1.path.offer(station);
                updatePathInfo(prev, station);  //경로 정보 업데이트
                updateCongest(station); //혼잡도 업데이트
                prev = station;
            }
            pathInfos.add(info1);
            System.out.println("hello");
        }
        return pathInfos;
    }


    public static void getTransferInfo(SubwayData start, SubwayData finish) {
        databaseManager.getTransferInfoDB(start, finish);
    }
}
