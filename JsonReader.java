import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;

import com.google.common.collect.Lists;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

    public static void main(String[] args) throws IOException, JSONException {
        JSONArray data = getRestraunts(2, 33.6471662, -117.8281741);
        Restraunt restraunt = new Restraunt(data.getJSONObject(0));
        System.out.println(restraunt.name + " " + restraunt.location + " " + restraunt.distance);

        HashSet<String> badFood = new HashSet<String>();
        badFood.add("bread");

        HashMap<String, Double> tagVals = new HashMap<String, Double>();
        tagVals.put("Mexican", 1.0);
        tagVals.put("Indian", 0.0);

        System.out.println(restraunt.getPriority(0, 1000, 1, badFood, tagVals));

    }

    public static JSONArray getItems(String brand) throws IOException {
        return getItems(brand, 0, 10000, 50);
    }

    public static JSONArray getRestraunts(float radius, double latitude, double longitude) throws IOException {
        radius = (int) (radius * 1609.34);
        JSONObject ob = getYelpData("https://api.yelp.com/v3/businesses/search?open_now=true&radius=" + radius + "&categories=Restaurants&latitude=" + latitude + "&longitude=" + longitude);
        return ob.getJSONArray("businesses");
    }

    public static JSONArray getItems(String brand, int calMin, int calMax, int maxResults) throws IOException {
        JSONObject json = readJsonFromUrl("https://api.nutritionix.com/v1_1/brand/search?query=" + brand + "&type=1&min_score=1&limit=1&appId=00566f3c&appKey=60fdf9bfd0c7d96b7314ab75c6e813e0");
        String brandID = json.getJSONArray("hits").getJSONObject(0).getJSONObject("fields").getString("_id");
        return readJsonFromUrl("https://api.nutritionix.com/v1_1/search/?brand_id=" + brandID + "&results=0%3A" + maxResults + "&cal_min=" + calMin + "&cal_max=" + calMax + "&fields=item_name%2Cnf_calories&appId=00566f3c&appKey=60fdf9bfd0c7d96b7314ab75c6e813e0").getJSONArray("hits");
    }

    private static JSONObject getYelpData(String url) throws IOException {
        Header header = new BasicHeader("Content-Type", "application/json");
        Header header2 = new BasicHeader("Authorization", "Bearer odnfxOE8P60tjW0KoNv9kX8fE001TdVnPQ7bEoqXdNBptfkVVpfWHA0ZtoakU52XETyaK5jyyv-ZLAXIZa-vrYtBrXxZ2qZejRM4AE82HmL1ixnoO11LGh4EH9RRXnYx");
        List<Header> headers = Lists.newArrayList(header,header2);
        CloseableHttpClient client = HttpClients.custom().setDefaultHeaders(headers).build();
        HttpUriRequest request = RequestBuilder.get().setUri(url).build();
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream instream = entity.getContent();
        String result = convertStreamToString(instream);
        return new JSONObject(result);
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        URL Url = new URL(url);
        InputStream is = Url.openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static void printItems(String brand) throws IOException {
        JSONArray json = getItems(brand);

        System.out.println(json.toString());
        for (Object j : json){
            JSONObject item = ((JSONObject) j).getJSONObject("fields");
            System.out.println("Item Name: " + item.getString("item_name") + "\tCalories: " + item.getInt("nf_calories"));
        }
    }
}