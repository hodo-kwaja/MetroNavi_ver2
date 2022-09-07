package MetroNavi;

import java.sql.Time;
import java.util.*;

class Node {

    Node() {}

    Node(SubwayData SD) {
        this.data = SD;
        this.lineDirection = SD.lineDirection;
    }
    Node(SubwayData SD, boolean boo, Node parent) {
        if(boo) {   //하행
            this.lineDirection = 0; //하행
            this.data.lineDirection = 0;
            this.data.stationName = SD.stationName;
            this.data.lineId = SD.lineId;
            this.data.stationDetailId = SD.stationDetailId;
            this.data.stationCode = SD.stationCode;
            this.data.nextStation = SD.nextStation;
            this.data.transfer = SD.transfer;
            this.data.stationId = SD.stationId;
            this.data.transferNum = SD.transferNum;
            this.data.transferInfo.transferNum = SD.transferNum;
            this.line.addAll(parent.line);
        }
        else {  //상행
            this.lineDirection = 1; //상행
            this.data.lineDirection = 1;
            this.data.stationName = SD.stationName;
            this.data.lineId = SD.lineId;
            this.data.stationDetailId = SD.stationDetailId;
            this.data.stationCode = SD.stationCode;
            this.data.beforeStation = SD.beforeStation;
            this.data.transfer = SD.transfer;
            this.data.stationId = SD.stationId;
            this.data.transferNum = SD.transferNum;
            this.data.transferInfo.transferNum = SD.transferNum;
            this.line.addAll(parent.line);
        }
    }
    int lineDirection;  //진행방향
    boolean isAlive = true;    //살아있는지 여부
    SubwayData data = new SubwayData(); //데이터
    Node parentNode;   //부모 노드
    ArrayList<Node> child = new ArrayList<>();    //자식 노드
    Stack<SubwayData> step = new Stack<>();   //중간 정류장
    Set<Integer> line = new HashSet<>();    //지나온 노선
    public void initRoot(String DSN, int SH, int SM, String WT) {
        this.data.stationName = DSN;    //출발역
        this.data.schedule.hour = SH; //출발 시각(시)
        this.data.schedule.minute = SM; //출발 시각(분)
        this.data.schedule.weekType = WT; //요일
    }
}

class SubwayData {

    SubwayData() {};

    SubwayData(SubwayData SD1, SubwayData SD2, int DI) {
        this.stationName = SD1.stationName;
        this.stationDetailId = SD1.stationDetailId;
        this.schedule.stationDetailId = SD1.stationDetailId;
        this.lineId = SD1.lineId;
        this.schedule.lineId = SD1.lineId;
        this.schedule.weekType = ScheduleManager.weekType;
        this.schedule.typeName = SD2.schedule.typeName;
        this.schedule.lineDirection = DI;
        this.lineDirection = DI;
    }

    SubwayData(int SI, String SN, String SC, int SDI, int LI, int BS, int NS, int E, int S) {
        stationId = SI;
        stationName = SN;
        stationCode = SC;
        stationDetailId = SDI;
        lineId = LI;
        beforeStation = BS;
        nextStation = NS;
        express = E;
        special = S;
    }

    int lineDirection;
    String stationName; //역 이름
    String stationCode; //역 코드
    int stationDetailId;    //station_detail_id
    int lineId; //호선
    int stationId;  //역 ID
    int beforeStation;  //이전 역
    int nextStation;    //다음 역
    int transferNum = 0;
    int express;
    int special;
    TimeTable schedule = new TimeTable();  //최적 시간표
    boolean transfer = false;   //환승역 여부
    Transfer transferInfo = new Transfer(); //환승 정보
}

class Transfer {

    Transfer() {}
    Transfer(int TN, int D, int T) {
        this.transferNum = TN;
        this.distance = D;
        this.timeSec  = T;
    }
    int transferNum;
    int distance;   //이동 거리
    int timeSec;    //이동 시간
}

class TimeTable {

    TimeTable() {}

    TimeTable(int SDI, int H, int M, String WT, String SN, String TN, int LD, int LI) {
        this.stationDetailId = SDI;
        this.hour = H;
        this.minute = M;
        this.weekType = WT;
        this.scheduleName = SN;
        this.typeName = TN;
        this.lineDirection = LD;
        this.lineId = LI;
    }
    int stationDetailId;
    int hour = 0;   //출발 시각(시)
    int minute = 0; //출발 시각(분)
    String weekType;    //요일
    String scheduleName;    //종착 지점
    String typeName;    //열차 종류

    int lineId;
    int lineDirection;
    int duration = 0;   //소요 시간
    int transferNum = 0;    //환승 횟수
    int numStep = 0;    //정류장 수
    double congest;  //혼잡도
    int congestScore;  //혼잡도 환산 점수
}

class pathInfo {

    int transferNum;    //환승 횟수
    int stepNum;    //정류장 수
    int duration;   //소요 시간
    double congest;  //혼잡도
    ArrayList<SubwayData> path = new ArrayList<>(); //경로
}

public class MetroNavi {
    /*public static void ininialize()
     * 입력 받기*/
    public static void initialize(String[] args) {
        MakeTree mk = new MakeTree();
        ScheduleManager sm = new ScheduleManager();
        System.out.print("출발역, 도착역, 시, 분, 요일 : ");
        Scanner input = new Scanner(System.in);

/*        ScheduleManager.departureStaionName = args[0];
        ScheduleManager.destinationStationName = args[1];
        ScheduleManager.startHour = Integer.parseInt(args[2]);
        ScheduleManager.startMinute = Integer.parseInt(args[3]);
        ScheduleManager.weekType = args[4];*/

        ScheduleManager.departureStaionName = input.next();
        ScheduleManager.destinationStationName = input.next();
        ScheduleManager.startHour = input.nextInt();
        ScheduleManager.startMinute = input.nextInt();
        ScheduleManager.weekType = input.next();

        mk.initRoot();  //root노드 초기화
        sm.searchDstLineNum();  //도착역 호선 탐색
    }
    /*psvm
     * 메인 메서드*/
    public static void main(String[] args) {
        MakeTree mk = new MakeTree();
        databaseManager.connectDatabase();  //DB 연결
        initialize(args);
        ArrayList<pathInfo> pathInfos = MakeTree.makeTree();
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
        MakeJson.Path(shortestPath, lowTransferPath);
    }
}

