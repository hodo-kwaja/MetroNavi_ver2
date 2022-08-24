package MetroNavi;

import java.sql.Time;
import java.util.ArrayList;

public class TimeAndDate {

    /*public static int convertTimeToScore(int hour, int minute)
    * 분으로 시간 환산*/
    public static int convertTimeToScore(int hour, int minute) {
        return hour * 60 + minute;
    }

    /*public static String conver30Time(int hour, iㅡt minute)
    * 30분 단위로 시간 끊기*/
    public static String conver30Time(int hour, int minute) {
        if(minute < 30) {
            minute = 0;
        }
        else {
            minute = 30;
        }
        String str = String.format("%02d" + "h" + minute + "m", hour);
        return str;
    }

    public static void calcEndTime(SubwayData parent, SubwayData child, TimeTable start, TimeTable finish) {
        int duration = convertTimeToScore(finish.hour, finish.minute) - convertTimeToScore(start.hour, start.minute);
        int hour = parent.schedule.hour;
        int minute = parent.schedule.minute + duration;

        if(minute >= 60) {
            hour++;
            minute -= 60;
        }
        child.schedule.hour = hour;
        child.schedule.minute = minute;
    }

}

