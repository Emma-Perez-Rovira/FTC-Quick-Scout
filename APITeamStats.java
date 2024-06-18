import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import java.net.MalformedURLException;

import java.util.Calendar;
import java.util.Date;
public class APITeamStats{

  private JSONArray data;
  private boolean properSetup = false;
  /**
  *  @Input int teamnum, int season
  * Sets up an API call URL from teamNum and season 
  * Passes that URL to the call() function
  */
  public APITeamStats(int teamNum, int season){
    try{
      URL url;
      if(season != 0){
        url = new URL("https://api.ftcscout.org/rest/v1/teams/" + teamNum + "/events/" + season);
      } else {
        java.util.Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        if(month >= 9){
          url = new URL("https://api.ftcscout.org/rest/v1/teams/" + teamNum + "/events/" + calendar.get(Calendar.YEAR));
        } else {
          url = new URL("https://api.ftcscout.org/rest/v1/teams/" + teamNum + "/events/" + (Integer.valueOf(calendar.get(Calendar.YEAR)) - 1));
        }
      }
      call(url);
    } catch (MalformedURLException e){
      e.printStackTrace();
      System.out.print("URL FAILED");
    }
  }
  /**
  *  @Return boolean properSetup;
  */
  public boolean properSetup(){
    return properSetup;
  }
  /**
  *  @Input URL url
  * Gets all the data from the api associated with the url provided.
  * Converts the data from a string to a JSONObject, and then stores that in the data variable
  * If it finalizes properly then properSetup will be set to true
  */
  private void call(URL url){
    try {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // connection.setRequestProperty("",heading);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        data = new JSONArray(response.toString());
      properSetup = true;

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /**
  *  @Return double seasonOPR
  * Iterates through every competition that the team has been in, adds the OPR they had at the comp to a total counter, then returns that divided by the number of events they have participated in
  */
  public double seasonOPR(){
    double total = 0;
    int events = 0;
    for(int i = 0; i < data.length(); i++){
      
      JSONObject obj = data.getJSONObject(i);
      if(!obj.isNull("stats")){
        JSONObject stats = obj.getJSONObject("stats");
        if(!stats.isNull("opr")){
          JSONObject opr = stats.getJSONObject("opr");
          if(!opr.isNull("totalPoints")){
          events++;
          total += opr.getDouble("totalPoints");
          }
        }
      }
    }
    if(events != 0){
      return (total/events);
    } else {
      return Integer.MIN_VALUE;
    }
  }
  /**
  *  @Return String[] latestAverageScore
  * Gets the latest average score for an event for the team and returns {score, eventCode}
  */
  public String[] latestAverageScore(){
    for(int i = 0; i < data.length(); i++){
      JSONObject obj = data.getJSONObject(i);
      if(!obj.isNull("stats")){
        JSONObject stats = obj.getJSONObject("stats");
        if(!stats.isNull("avg")){
          JSONObject avg = stats.getJSONObject("avg");
          if(!avg.isNull("totalPoints")){
            return new String[] {String.valueOf(avg.getDouble("totalPoints")), obj.getString("eventCode")};
          }
        }
      }
    }
    return null;
  }
  /**
  *  @Return String[] latestOPR
  * Gets the latest OPR for an event for the team and returns {OPR, eventCode, autoOPR, endGameOPR, driverControllOPR}
  */
  public String[] latestOPR(){
    for(int i = 0; i < data.length(); i++){
      JSONObject obj = data.getJSONObject(i);
      if(!obj.isNull("stats")){
        JSONObject stats = obj.getJSONObject("stats");
        if(!stats.isNull("opr")){
          JSONObject opr = stats.getJSONObject("opr");
          if(!opr.isNull("totalPoints") && !opr.isNull("autoPoints") && !opr.isNull("egPoints")){
            return new String[] {String.format("%.1f", opr.getDouble("totalPoints")), obj.getString("eventCode"), String.format("%.1f", opr.getDouble("autoPoints")), String.format("%.1f",opr.getDouble("egPoints")), String.format("%.1f",opr.getDouble("dcPoints"))};
          }
        }
      }
    }
    return null;
  }

  
}
