import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import java.net.MalformedURLException;

import java.util.Calendar;
import java.util.Date;

public class APITeamAwards{
  private Boolean properSetup = false;
  private JSONArray data;
  /**
  *  @Input int teamnum, int season
  * Sets up an API call URL from teamNum and season 
  * Passes that URL to the call() function
  */
  public APITeamAwards(int teamNum, int season){
    try{
      URL url;
      if(season != 0){
        url = new URL("https://api.ftcscout.org/rest/v1/teams/" + teamNum + "/awards?season=" + season);
      } else {
        java.util.Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        if(month >= 9){
          url = new URL("https://api.ftcscout.org/rest/v1/teams/" + teamNum + "/awards?season=" + calendar.get(Calendar.YEAR));
        } else {
          url = new URL("https://api.ftcscout.org/rest/v1/teams/" + teamNum + "/awards?season=" + (Integer.valueOf(calendar.get(Calendar.YEAR)) - 1));
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
  * @Return String[] awards
  * Gets all awards that the team has won within the specified season, and returns them formatted within a String[]
  */
  public String[] getAllAwards(){
    String[] arr = new String[data.length()];
    int posInArr = 0;
    for(int i = 0; i < data.length(); i++){
      JSONObject current = data.getJSONObject(i);
      if(!current.isNull("type")){
        // String toPad = current.getString("type") + " - " + current.getInt("placement");
        arr[posInArr] = current.getString("type")+ "-" + current.getInt("placement") + " at: " + current.getString("eventCode");
        posInArr++;
      }
    }
    return arr;
  }
  
}
