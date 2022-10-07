package MetroNavi;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.sql.Time;
import java.util.*;

class ScheduleManager {

    private static ScheduleManager innstance = null;

    public static void reset() {
        innstance = null;
    }
    ScheduleManager() {}
    public static ScheduleManager getInstance()
    {
        if (innstance == null)
        {
            synchronized(ScheduleManager.class)
            {
                innstance = new ScheduleManager();
            }
        }
        return innstance;
    }
    public boolean[] visit = new boolean[1045]; //지나온 역
    public String departureStaionName; //출발역 이름
    public String destinationStationName;  //도착역 이름
    public ArrayList<Integer> dstLineNum = new ArrayList<>();
    public int startHour, startMinute; //출발 시각(시, 분)
    public String weekType;    //요일
    public ArrayList<Node> path = new ArrayList<>();   //도착역에 도착한 노드들
    public Queue<Node> queue = new LinkedList<>(); //도착역과 호선이 다른 경로
    public Queue<Node> priorQ = new LinkedList<>();    //도착역과 호선이 같은 경로
    public ArrayList<Stack<SubwayData>> finalPath = new ArrayList<>();  //최종 도출 경로


    /*public static SubwayData giveMe(TimeTable TT)
    * TT의 scheduleName의 역 정보 가져오기*/
    public static SubwayData giveMe(TimeTable TT) {
        ArrayList<SubwayData> subs = databaseManager.getSubLineNameInfoDB(TT.scheduleName);
        SubwayData result = new SubwayData();
        for (SubwayData SD : subs) {
            if (SD.lineId == TT.lineId) {
                result = SD;
            }
        }
        return result;
    }

    /*public static ArrayList<TimeTable> refineSchedule (ArrayList<TimeTable> schedules)
     * 조건 중복되는 시간표 제거*/
    public static TimeTable refineSchedule(ArrayList<TimeTable> schedules, int SDI) {
        TimeTable newSchedule = new TimeTable();
        SubwayData transtation = databaseManager.getStationWithDetailIdDB(SDI);

        for (int i = 0; i < schedules.size(); i++) {
            TimeTable TT = schedules.get(i);

            if (TT.lineDirection == 0) {    //하행
                if (TT.lineId == 108) { //경의중
                    SubwayData sub = giveMe(TT);
                    int result = sub.stationCode.compareTo(transtation.stationCode);

                    if (TT.typeName.equals("S")) {  //급행
                        if (transtation.express == 1) {
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
                    } else {
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
                } else if (TT.lineId == 2) {  //2호선
                    newSchedule = TT;
                    break;
                } else if(TT.lineId == 9) {    //9호선
                    if (TT.typeName.equals("S")) {  //급행
                        if (transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result <= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else if (TT.typeName.equals("E")) { //특급
                        if (transtation.special == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result <= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);

                        if (result <= 0) {
                            newSchedule = TT;
                            break;
                        }
                    }
                } else if(TT.lineId == 104) {
                    if(TT.typeName.equals("S")) { //급행
                        if(transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            if(transtation.stationCode.contains("P")) {
                                if(sub.stationName.equals("서울역")) {
                                    newSchedule = TT;
                                    break;
                                }
                            }

                            int result = sub.stationCode.compareTo(transtation.stationCode);
                            if(transtation.stationCode.contains("K3")) {
                                if(result <= 0) {
                                    newSchedule = TT;
                                    break;
                                }
                            }
                            if(transtation.stationCode.contains("K1")) {
                                if(result >= 0) {
                                    newSchedule = TT;
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);
                        if(sub.stationCode.contains("K3")) {
                            if(result <= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                        if(sub.stationCode.contains("K1")) {
                            if(result >= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    }

                } else {
                    if (TT.typeName.equals("S")) {  //급행
                        if (transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result >= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else if (TT.typeName.equals("E")) { //특급
                        if (transtation.special == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result >= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);

                        if (result >= 0) {
                            newSchedule = TT;
                            break;
                        }
                    }
                }


            } else {  //상행
                if (TT.lineId == 108) { //경춘선
                    if (TT.typeName.equals("S")) {  //급형
                        if (transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);
                            if (result >= 0 && !sub.stationCode.contains("P")) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);
                        if (result >= 0 && !sub.stationCode.contains("P")) {
                            newSchedule = TT;
                            break;
                        }
                    }
                } else if (TT.lineId == 2) {  //2호선
                    newSchedule = TT;
                    break;
                } else if(TT.lineId == 9) {  //9호선
                    if (TT.typeName.equals("S")) {  //급행
                        if (transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result >= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else if (TT.typeName.equals("E")) { //특급
                        if (transtation.special == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result >= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);

                        if (result >= 0) {
                            newSchedule = TT;
                            break;
                        }
                    }
                } else if(TT.lineId == 104) {
                    if(TT.typeName.equals("S")) { //급행
                        if(transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);
                            if(transtation.stationCode.contains("K3")) {
                                if(result >= 0) {
                                    newSchedule = TT;
                                    break;
                                }
                            }
                            if(transtation.stationCode.contains("K1")) {
                                if (sub.stationCode.contains("K1")) {
                                    if (result <= 0) {
                                        newSchedule = TT;
                                        break;
                                    }
                                }
                                else {
                                    if(result >= 0) {
                                        newSchedule = TT;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        SubwayData sub = giveMe(TT);
                        int result = sub.stationCode.compareTo(transtation.stationCode);
                        if(sub.stationCode.contains("K3")) {
                            if(result >= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                        if(sub.stationCode.contains("K1")) {
                            if (transtation.stationCode.contains("K1")) {
                                if (result <= 0) {
                                    newSchedule = TT;
                                    break;
                                }
                            }
                            else {
                                if(result >= 0) {
                                    newSchedule = TT;
                                    break;
                                }
                            }
                        }
                    }

                } else {
                    if (TT.typeName.equals("E")) {  //특급
                        if (transtation.special == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result <= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else if (TT.typeName.equals("S")) { //급행
                        if (transtation.express == 1) {
                            SubwayData sub = giveMe(TT);
                            int result = sub.stationCode.compareTo(transtation.stationCode);

                            if (result <= 0) {
                                newSchedule = TT;
                                break;
                            }
                        }
                    } else {
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
        if(parent.stationName.equals(child.stationName)) {
            child.schedule.numStep = parent.schedule.numStep;
        }
        else {
            child.schedule.numStep = parent.schedule.numStep + 1;   //경유역 수
        }
        child.schedule.transferNum = parent.schedule.transferNum;   //환승 수
        child.schedule.duration = TimeAndDate.convertTimeToScore(child.schedule.hour, child.schedule.minute)
                - TimeAndDate.convertTimeToScore(parent.schedule.hour, parent.schedule.minute);    //소요시간
    }

    /*public static void updateCongest(SubwayData child)
     * 혼잡도 가져오기*/
    public static void updateCongest(SubwayData station) {
        TimeTable tt = station.schedule;
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
                        } else {
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
        ScheduleManager sm = ScheduleManager.getInstance();
        if (station.lineDirection == 0) {    //하행
            Stack<SubwayData> stepPath = new Stack<>();
            SubwayData temp;
            SubwayData previous = station.data;
            temp = databaseManager.getStationWithDetailIdDB(previous.nextStation);
            while (temp != null) {
                temp.lineDirection = 0;
                if (temp.stationName.equals(sm.destinationStationName)) { //목적지
                    temp.transferNum = station.data.transferNum;
                    Node destination = new Node(temp);
                    station.child.add(destination);
                    destination.parentNode = station;
                    station.step = stepPath;
                    sm.path.add(destination);
                    Arrays.fill(sm.visit, false);
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
                if (temp.stationName.equals(sm.destinationStationName)) { //목적지
                    temp.transferNum = station.data.transferNum;
                    Node destination = new Node(temp);
                    station.child.add(destination);
                    destination.parentNode = station;
                    station.step = stepPath;
                    sm.path.add(destination);
                    Arrays.fill(sm.visit, false);
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
    public static ArrayList<pathInfo> routeOrganization() {
        ScheduleManager sm = ScheduleManager.getInstance();
        ArrayList<Stack<Integer>> transNum = new ArrayList<>();
        for (Node dst : sm.path) {
            Stack<SubwayData> path1 = new Stack<>();
            Stack<Integer> transnum = new Stack<>();
            Node station = dst;
            transnum.push(station.data.stationDetailId);
            while (station.parentNode != null) {
                if (!station.step.isEmpty()) {
                    Stack<SubwayData> temp = new Stack<>();
                    temp.addAll(station.step);
                    while (!station.step.isEmpty()) {   //
                        SubwayData tmp = station.step.pop();
                        path1.push(tmp);
                    }
                    station.step.addAll(temp);
                }
                if (station.data.transfer && station.data.transferInfo.timeSec != 0) {
                    transnum.push(station.data.stationDetailId);
                }
                path1.push(station.data);
                station = station.parentNode;
            }
            transNum.add(transnum);
            sm.finalPath.add(path1);
        }
        return ScheduleManager.addTimeLine(transNum);   //시간표 추가
    }

    /*public void addTimeline()
     * 경로에 시간표 추가*/
    public static ArrayList<pathInfo> addTimeLine(ArrayList<Stack<Integer>> transNum) {
        ScheduleManager sm = ScheduleManager.getInstance();
        MakeTree mk = MakeTree.getInstance();
        ArrayList<pathInfo> pathInfos = new ArrayList<>();
        SubwayData root = mk.root.data;
        for (int i = 0; i < sm.finalPath.size(); i++) {
            long start = System.currentTimeMillis();
            Stack<SubwayData> finalroute = sm.finalPath.get(i);    //경로 n번
            Stack<Integer> transtation = transNum.get(i);   //통과역 n번
            pathInfo info1 = new pathInfo();
            SubwayData prev = root; //이번 역
            int count = 0;
            while (!finalroute.isEmpty()) {
                SubwayData station = finalroute.pop();
                if ((station.transfer && station.transferInfo.timeSec == 0) || prev == root) {  //출발역 or 환승역
                    int minute = prev.schedule.minute + prev.transferInfo.timeSec/60;
                    station.schedule = refineSchedule(databaseManager.getScheduleDB(prev, station, minute), transtation.pop());
                    info1.transferNum++;
                } else {  //그 외 역
                    station.schedule = databaseManager.getOneScheduleDB(prev, station);
                    if (station.schedule.lineId == 0) {
                        if(finalroute.isEmpty()) {  //끝 역 도착시간
                            getEndTime(prev, station);
                        }
                        else { //급행 시간표일 때 일반역 패스
                            continue;
                        }
                    }
                }
                info1.path.add(station);
                updatePathInfo(prev, station);  //경로 정보 업데이트
                if(prev == root) {
                    station.schedule.duration = 0;
                }
                updateCongest(station); //혼잡도 업데이트
                if(station.schedule.congest != 0.0) {
                    count++;
                    info1.congest += station.schedule.congest;
                }
                info1.stepNum++;
                info1.duration += station.schedule.duration;
                station.schedule.duration = prev.schedule.duration + station.schedule.duration;
                prev = station;
            }
            if((double)count/info1.stepNum >= 0.8) {
                info1.congest = info1.congest / (double) count;
            }
            else
                info1.congest = 99999;

            info1.transferNum -= 1;
            info1.stepNum -=1;
            if(info1.duration <= 0) {
                info1.duration = 99999;
                info1.transferNum = 99999;
            }
            pathInfos.add(info1);
            long finish = System.currentTimeMillis() - start;
            System.out.println(finish);
        }
        return pathInfos;
    }

    /*public static void getTransferInfo(SubwayData start, SubwayData finish)
    * 환승 정보 가져옴*/
    public static void getTransferInfo(SubwayData start, SubwayData finish) {
        databaseManager.getTransferInfoDB(start, finish);
    }

    /*public static void getEndTime(SubwayData parent, SubwayData child)
    * 도착 시간 계산*/
    public static void getEndTime(SubwayData parent, SubwayData child) {
        if (parent.schedule.lineDirection == 1) {
            SubwayData start = new SubwayData(child, parent, 0);
            SubwayData finish = new SubwayData(parent, child, 0);
            databaseManager.getEndScheduleDB(start);
            finish.schedule =  databaseManager.getOneScheduleDB(start, finish);
            TimeAndDate.calcEndTime(parent, child, start.schedule, finish.schedule);
        }
        else {
            SubwayData start = new SubwayData(child, parent, 1);
            SubwayData finish = new SubwayData(parent, child, 1);
            databaseManager.getEndScheduleDB(start);
            finish.schedule = databaseManager.getOneScheduleDB(start, finish);
            TimeAndDate.calcEndTime(parent, child, start.schedule, finish.schedule);
        }
    }
}

