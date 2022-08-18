package MetroNavi;

import java.sql.Time;
import java.util.*;

class Node {

    Node() {}

    Node(SubwayData SD) {
        this.data = SD;
        this.lineDirection = SD.lineDirection;
    }
    Node(SubwayData SD, boolean boo) {
        if(boo) {   //하행
            this.lineDirection = 0; //하행
            this.data.lineDirection = 0;
            this.data.stationName = SD.stationName;
            this.data.lineId = SD.lineId;
            this.data.stationDetailId = SD.stationDetailId;
            this.data.stationCode = SD.stationCode;
            this.data.nextStation = SD.nextStation;
            this.data.transfer = SD.transfer;
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
        }
    }
    int lineDirection;  //진행방향
    boolean isAlive = true;    //살아있는지 여부
    SubwayData data = new SubwayData(); //데이터
    Node parentNode;   //부모 노드
    ArrayList<Node> child = new ArrayList<>();    //자식 노드
    Stack<SubwayData> step;   //중간 정류장

    public void initRoot(String DSN, int SH, int SM, String WT) {
        this.data.stationName = DSN;    //출발역
        this.data.schedule.hour = SH; //출발 시각(시)
        this.data.schedule.minute = SM; //출발 시각(분)
        this.data.schedule.weekType = WT; //요일
    }
}

class SubwayData {

    SubwayData() {};

    SubwayData(int SI, String SN, String SC, int SDI, int LI, int BS, int NS) {
        stationId = SI;
        stationName = SN;
        stationCode = SC;
        stationDetailId = SDI;
        lineId = LI;
        beforeStation = BS;
        nextStation = NS;
    }

    int lineDirection;
    String stationName; //역 이름
    String stationCode; //역 코드
    int stationDetailId;    //station_detail_id
    int lineId; //호선
    int stationId;  //역 ID
    int beforeStation;  //이전 역
    int nextStation;    //다음 역

    TimeTable schedule = new TimeTable();  //최적 시간표
    ArrayList<TimeTable> candiSchedule = new ArrayList<>();   //후보 시간표

    boolean express = false;    //급행 여부
    boolean special = false;    //특급 여부
    boolean transfer = false;   //환승역 여부
    Transfer transferInfo; //환승 정보
}

class Transfer {

    Transfer(int SDI, int FDI, int D, int T) {
        this.startDetailId = SDI;
        this.finishDetailId = FDI;
        this.distance = D;
        this.timeSec  = T;
    }

    int startDetailId;  //시작 역 station detail id
    int finishDetailId; //도착 역 station detail id
    int distance;   //이동 거리
    int timeSec;    //이동 시간
}

class TimeTable {

    TimeTable() {}

    TimeTable(int H, int M) {
        this.hour = H;
        this.minute = M;
    }
    TimeTable(int H, int M, String WT, String SN, String TN) {
        this.hour = H;
        this.minute = M;
        this.weekType = WT;
        this.scheduleName = SN;
        this.typeName = TN;
    }
    int hour = 0;   //출발 시각(시)
    int minute = 0; //출발 시각(분)
    String weekType;    //요일
    String scheduleName;    //종착 지점
    String typeName;    //열차 종류

    int duration = 0;   //소요 시간
    int transferNum = 0;    //환승 횟수
    int numStep = 0;    //정류장 수
    double congest;  //혼잡도
    int congestScore;  //혼잡도 환산 점수
}


public class MetroNavi {
    /*public static void ininialize()
    * 입력 받기*/
    public static void initialize() {
        MakeTree mk = new MakeTree();
        ScheduleManager sm = new ScheduleManager();
        System.out.print("출발역, 도착역, 시, 분, 요일 : ");
        Scanner input = new Scanner(System.in);

        sm.departureStaionName = input.next();
        sm.destinationStationName = input.next();
        sm.startHour = Integer.parseInt(input.next());
        sm.startMinute = Integer.parseInt(input.next());
        sm.weekType = input.next();
        mk.initRoot();  //root노드 초기화
        sm.searchDstLineNum();  //도착역 호선 탐색
        }

    /*psvm
    * 메인 메서드*/
    public static void main(String[] args) {
        MakeTree mk = new MakeTree();
        databaseManager.connectDatabase();  //DB 연결
        initialize();
        mk.makeTree();
    }
}
