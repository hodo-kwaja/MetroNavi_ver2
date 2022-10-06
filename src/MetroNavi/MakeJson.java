package MetroNavi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;

public class MakeJson {
    public static void Path(pathInfo shortPath, pathInfo transPath) {
        FileWriter writer = null;
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        JsonArray jsonArray1 = new JsonArray();

        for(SubwayData temp : shortPath.path) {
            JsonObject object = new JsonObject();
            object.addProperty("stationName", temp.stationName);
            object.addProperty("stationCode", temp.stationCode);
            object.addProperty("lineId", temp.lineId);

            JsonObject schedule = new JsonObject();
            schedule.addProperty("lineDirection", temp.schedule.lineDirection);
            schedule.addProperty("weekType", temp.schedule.weekType);
            schedule.addProperty("hour", temp.schedule.hour);
            schedule.addProperty("minute", temp.schedule.minute);
            schedule.addProperty("typeName", temp.schedule.typeName);
            schedule.addProperty("scheduleName", temp.schedule.scheduleName);
            schedule.addProperty("congestScore", temp.schedule.congestScore);
            schedule.addProperty("duration", temp.schedule.duration);
            schedule.addProperty("numStep", temp.schedule.numStep);

            JsonObject transfer = new JsonObject();
            transfer.addProperty("transferNum", temp.transferNum);
            transfer.addProperty("isTransfer", temp.transfer);
            transfer.addProperty("transferDistance", temp.transferInfo.distance);
            transfer.addProperty("transferTime", temp.transferInfo.timeSec);

            object.add("schedule", schedule);
            object.add("transfer", transfer);

            jsonArray.add(object);
        }
        jsonObject.add("ShortestPath", jsonArray);

        for(SubwayData temp : transPath.path) {
            JsonObject object = new JsonObject();
            object.addProperty("stationName", temp.stationName);
            object.addProperty("stationCode", temp.stationCode);
            object.addProperty("lineId", temp.lineId);

            JsonObject schedule = new JsonObject();
            schedule.addProperty("lineDirection", temp.schedule.lineDirection);
            schedule.addProperty("weekType", temp.schedule.weekType);
            schedule.addProperty("hour", temp.schedule.hour);
            schedule.addProperty("minute", temp.schedule.minute);
            schedule.addProperty("typeName", temp.schedule.typeName);
            schedule.addProperty("scheduleName", temp.schedule.scheduleName);
            schedule.addProperty("congestScore", temp.schedule.congestScore);
            schedule.addProperty("duration", temp.schedule.duration);
            schedule.addProperty("numStep", temp.schedule.numStep);

            JsonObject transfer = new JsonObject();
            transfer.addProperty("transferNum", temp.transferNum);
            transfer.addProperty("isTransfer", temp.transfer);
            transfer.addProperty("transferDistance", temp.transferInfo.distance);
            transfer.addProperty("transferTime", temp.transferInfo.timeSec);

            object.add("schedule", schedule);
            object.add("transfer", transfer);

            jsonArray1.add(object);
        }
        jsonObject.add("LowTransferPath", jsonArray1);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(jsonObject);
        try {
            writer = new FileWriter("Path.json");
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Path(pathInfo shortPath, pathInfo transPath, pathInfo congestPath) {
        FileWriter writer = null;
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        JsonArray jsonArray1 = new JsonArray();
        JsonArray jsonArray2 = new JsonArray();

        for(SubwayData temp : shortPath.path) {
            JsonObject object = new JsonObject();
            object.addProperty("stationName", temp.stationName);
            object.addProperty("stationCode", temp.stationCode);
            object.addProperty("lineId", temp.lineId);

            JsonObject schedule = new JsonObject();
            schedule.addProperty("lineDirection", temp.schedule.lineDirection);
            schedule.addProperty("weekType", temp.schedule.weekType);
            schedule.addProperty("hour", temp.schedule.hour);
            schedule.addProperty("minute", temp.schedule.minute);
            schedule.addProperty("typeName", temp.schedule.typeName);
            schedule.addProperty("scheduleName", temp.schedule.scheduleName);
            schedule.addProperty("congestScore", temp.schedule.congestScore);
            schedule.addProperty("duration", temp.schedule.duration);
            schedule.addProperty("numStep", temp.schedule.numStep);

            JsonObject transfer = new JsonObject();
            transfer.addProperty("transferNum", temp.transferNum);
            transfer.addProperty("isTransfer", temp.transfer);
            transfer.addProperty("transferDistance", temp.transferInfo.distance);
            transfer.addProperty("transferTime", temp.transferInfo.timeSec);

            object.add("schedule", schedule);
            object.add("transfer", transfer);

            jsonArray.add(object);
        }
        jsonObject.add("ShortestPath", jsonArray);

        for(SubwayData temp : transPath.path) {
            JsonObject object = new JsonObject();
            object.addProperty("stationName", temp.stationName);
            object.addProperty("stationCode", temp.stationCode);
            object.addProperty("lineId", temp.lineId);

            JsonObject schedule = new JsonObject();
            schedule.addProperty("lineDirection", temp.schedule.lineDirection);
            schedule.addProperty("weekType", temp.schedule.weekType);
            schedule.addProperty("hour", temp.schedule.hour);
            schedule.addProperty("minute", temp.schedule.minute);
            schedule.addProperty("typeName", temp.schedule.typeName);
            schedule.addProperty("scheduleName", temp.schedule.scheduleName);
            schedule.addProperty("congestScore", temp.schedule.congestScore);
            schedule.addProperty("duration", temp.schedule.duration);
            schedule.addProperty("numStep", temp.schedule.numStep);

            JsonObject transfer = new JsonObject();
            transfer.addProperty("transferNum", temp.transferNum);
            transfer.addProperty("isTransfer", temp.transfer);
            transfer.addProperty("transferDistance", temp.transferInfo.distance);
            transfer.addProperty("transferTime", temp.transferInfo.timeSec);

            object.add("schedule", schedule);
            object.add("transfer", transfer);

            jsonArray1.add(object);
        }
        jsonObject.add("LowTransferPath", jsonArray1);

        for(SubwayData temp : congestPath.path) {
            JsonObject object = new JsonObject();
            object.addProperty("stationName", temp.stationName);
            object.addProperty("stationCode", temp.stationCode);
            object.addProperty("lineId", temp.lineId);

            JsonObject schedule = new JsonObject();
            schedule.addProperty("lineDirection", temp.schedule.lineDirection);
            schedule.addProperty("weekType", temp.schedule.weekType);
            schedule.addProperty("hour", temp.schedule.hour);
            schedule.addProperty("minute", temp.schedule.minute);
            schedule.addProperty("typeName", temp.schedule.typeName);
            schedule.addProperty("scheduleName", temp.schedule.scheduleName);
            schedule.addProperty("congestScore", temp.schedule.congestScore);
            schedule.addProperty("duration", temp.schedule.duration);
            schedule.addProperty("numStep", temp.schedule.numStep);

            JsonObject transfer = new JsonObject();
            transfer.addProperty("transferNum", temp.transferNum);
            transfer.addProperty("isTransfer", temp.transfer);
            transfer.addProperty("transferDistance", temp.transferInfo.distance);
            transfer.addProperty("transferTime", temp.transferInfo.timeSec);

            object.add("schedule", schedule);
            object.add("transfer", transfer);

            jsonArray2.add(object);
        }
        jsonObject.add("LowCongestPath", jsonArray2);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(jsonObject);
        try {
            writer = new FileWriter("Path.json");
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
