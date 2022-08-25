package MetroNavi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;

public class MakeJson {
    public static void ShortestPath(pathInfo path) {
        FileWriter writer = null;
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        for(SubwayData temp : path.path) {
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(jsonObject);
        try {
            writer = new FileWriter("ShortestPath.json");
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

    public static void LowTransferPath(pathInfo path) {
        FileWriter writer = null;
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        for(SubwayData temp : path.path) {
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(jsonObject);
        try {
            writer = new FileWriter("LowTransferPath.json");
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
