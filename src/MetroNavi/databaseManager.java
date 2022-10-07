/*
* databaseManager
* 데이터베이스 관련된 클래스
* */
package MetroNavi;

import java.sql.*;
import java.util.ArrayList;

class databaseManager {

    private static Connection conn = null;

    /*conn 생성*/
    public static void connectDatabase() {
        String jdbcDriver = "com.mysql.cj.jdbc.Driver";
        String dbURL = "jdbc:mysql://localhost:3306/?user=root";
        String userName = "root";
        String password = "19980316";
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(dbURL, userName, password);
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
    }

    public static ArrayList<Integer> searchLineNumDB(String stationName) {
        ArrayList<Integer> dstLineNum = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT line_id FROM Subway.sub_line_name_info WHERE station_name = \"%s\" AND city_id = 1000", stationName);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                dstLineNum.add(resultSet.getInt("line_id"));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return dstLineNum;
    }

    public static ArrayList<SubwayData> getSubLineNameInfoDB(String stationName) {
        ArrayList<SubwayData> stations = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT * FROM Subway.sub_line_name_info WHERE station_name = \"%s\" AND city_id = 1000", stationName);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                stations.add(new SubwayData(
                        resultSet.getInt("station_id"),
                        resultSet.getString("station_name"),
                        resultSet.getString("station_code"),
                        resultSet.getInt("station_detail_id"),
                        resultSet.getInt("line_id"),
                        resultSet.getInt("before_station"),
                        resultSet.getInt("next_station"),
                        resultSet.getInt("express"),
                        resultSet.getInt("special")
                ));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return stations;
    }

    public static ArrayList<TimeTable> getScheduleDB(SubwayData parent, SubwayData child, int minute) {
        ArrayList<TimeTable> schedules = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery;
            strQuery = String.format("SELECT * FROM Subway.sub_tt_line_%d WHERE station_detail_id = %d AND hour - %d <= 1 AND ((hour * 60 + minute) " +
                            "- (%d * 60 + %d)) >= 0 AND week_type = \'%s\' AND line_direction = %d LIMIT 5",
                    child.lineId, child.stationDetailId, parent.schedule.hour, parent.schedule.hour, minute,
                    parent.schedule.weekType, child.lineDirection);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery);
            while(resultSet.next()) {
                schedules.add(new TimeTable(
                        resultSet.getInt("station_detail_id"),
                        resultSet.getInt("hour"),
                        resultSet.getInt("minute"),
                        resultSet.getString("week_type"),
                        resultSet.getString("schedule_name"),
                        resultSet.getString("subway_type"),
                        resultSet.getInt("line_direction"),
                        resultSet.getInt("line_id")
                ));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return schedules;
    }
    public static TimeTable getOneScheduleDB(SubwayData parent, SubwayData child) {
        TimeTable schedule = new TimeTable();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery;
            strQuery = String.format("SELECT station_detail_id, line_direction, subway_type, week_type, schedule_name, hour, minute, line_id " +
                            "FROM Subway.sub_tt_line_%d WHERE station_detail_id = %d AND hour - %d <= 1 AND ((hour * 60 + minute) " +
                            "- (%d * 60 + %d)) >= 0 AND week_type = \'%s\' AND line_direction = %d AND subway_type = \'%s\' AND schedule_name = \'%s\' LIMIT 1",
                    child.lineId, child.stationDetailId, parent.schedule.hour, parent.schedule.hour, parent.schedule.minute,
                    parent.schedule.weekType, child.lineDirection, parent.schedule.typeName, parent.schedule.scheduleName);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery);
            while(resultSet.next()) {
                schedule = new TimeTable(
                        resultSet.getInt("station_detail_id"),
                        resultSet.getInt("hour"),
                        resultSet.getInt("minute"),
                        resultSet.getString("week_type"),
                        resultSet.getString("schedule_name"),
                        resultSet.getString("subway_type"),
                        resultSet.getInt("line_direction"),
                        resultSet.getInt("line_id")
                );
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return schedule;
    }

    public static SubwayData getStationWithDetailIdDB(int stationDetailId) {
        SubwayData station = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT * FROM Subway.sub_line_name_info WHERE station_detail_id = %d AND city_id = 1000", stationDetailId);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                station = new SubwayData(
                        resultSet.getInt("station_id"),
                        resultSet.getString("station_name"),
                        resultSet.getString("station_code"),
                        resultSet.getInt("station_detail_id"),
                        resultSet.getInt("line_id"),
                        resultSet.getInt("before_station"),
                        resultSet.getInt("next_station"),
                        resultSet.getInt("express"),
                        resultSet.getInt("special")
                );
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return station;
    }

    public static double getCongestDB(String time, SubwayData station) {
        ScheduleManager sm = ScheduleManager.getInstance();
        double result = 0.0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT %s FROM Subway.sub_congest_data WHERE station_name = \"%s\" AND line_id = %d AND line_direction = %d AND week_type = \"%s\"", time, station.stationName, station.lineId, station.lineDirection, sm.weekType);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                result = resultSet.getDouble(time);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
        return result;
    }

    public static void getTransferInfoDB(SubwayData start, SubwayData finish) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT time_sec, distance FROM Subway.sub_transfer WHERE start_station_detail_id = %d AND finish_station_detail_id = %d ", start.stationDetailId, finish.stationDetailId);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                start.transferInfo = new Transfer(
                        start.transferNum,
                        resultSet.getInt("time_sec"),
                        resultSet.getInt("distance"));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }
    }

    public static void getEndScheduleDB(SubwayData station) {
        TimeTable TT = new TimeTable();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement stmt = conn.createStatement();
            String strQuery1 = String.format("SELECT * FROM Subway.sub_tt_line_%d WHERE station_detail_id = %d AND line_direction = %d AND week_type = \"%s\" AND subway_type = \"%s\" AND hour >= 5 LIMIT 1",
                    station.schedule.lineId, station.schedule.stationDetailId, station.schedule.lineDirection, station.schedule.weekType, station.schedule.typeName);
            java.sql.ResultSet resultSet = stmt.executeQuery(strQuery1);
            while(resultSet.next()) {
                station.schedule.hour = resultSet.getInt("hour");
                station.schedule.minute = resultSet.getInt("minute");
                station.schedule.scheduleName = resultSet.getString("schedule_name");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 에러");
        }

    }
}