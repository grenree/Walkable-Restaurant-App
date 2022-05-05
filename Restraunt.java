import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.HashSet;

public class Restraunt {

    HashSet<String> tags;
    String name;
    double distance;
    String imgUrl;
    String location;

    public Restraunt(JSONObject restrauntData) {

        tags = new HashSet<String>();
        for(Object obj: restrauntData.getJSONArray("categories")){
            tags.add(((JSONObject) obj).getString("title"));
        }

        name = restrauntData.getString("name");

        distance = restrauntData.getDouble("distance") / 1609.34;

        imgUrl = restrauntData.getString("image_url");

        location = restrauntData.getJSONObject("location").getString("address1");
    }

    public double getPriority(int calMin, int calMax, double expectedDistance, HashSet<String> hatedFoods, HashMap<String, Double> tagVals) throws IOException { // need to implement tag vals
        int totalItems = 4;
        int totalTasteViable = 2;
        int totalNutritionViable = 1;


        try {
            JSONArray foods = JsonReader.getItems(this.name);

            totalItems = 0;
            totalTasteViable = 0;
            totalNutritionViable = 0;

            for (Object obj : foods) {
                JSONObject food = (JSONObject) obj;
                totalItems += 1;
                boolean isViable = true;
                for (String hatedFood : hatedFoods) {
                    if (food.getString("item_name").contains(hatedFood)) {
                        isViable = false;
                        break;
                    }
                }
                if (isViable) {
                    totalTasteViable += 1;
                    int cal = food.getInt("nf_calories");
                    totalNutritionViable += (cal >= calMin && cal <= calMax) ? 1 : 0;
                }

            }
        }
        catch (IOException e) // Failed to get food data
        { System.out.println("Failed to get food data");}
        catch (Exception e)
        { System.out.println("Well that went bad");}

        double tagValAvg = 0;
        int tagAmt = 0;
        for(String tag : this.tags)
        {
            tagAmt+= 1;
            if(tagVals.containsKey("tag")) tagValAvg += tagVals.get(tag);
            else tagValAvg += .5;
        }
        tagValAvg /= tagAmt;

        double vd = (Math.max(1 - Math.abs((this.distance - expectedDistance) / expectedDistance), 0));
        double vn = 1.0 * totalNutritionViable / totalTasteViable;
        double vt = 1.0 * totalTasteViable / totalItems * tagValAvg;

        double wd = 1;
        double wn = 1;
        double wt = 1;

        return vd * wd + vn * wn + vt * wt;
    }

}
