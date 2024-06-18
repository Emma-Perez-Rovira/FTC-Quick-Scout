import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import java.net.MalformedURLException;

public class APIQuickStats{
  private JSONObject data;
  private boolean properSetup = false;

  /**
  *  @Input int teamnum, int season
  * Sets up an API call URL from teamNum and season 
  * Passes that URL to the call() function
  */
  public APIQuickStats(int teamNum, int season){
    try{
      URL url;
      if(season != 0){
        url = new URL("https://api.ftcscout.org/rest/v1/teams/" + teamNum + "/quick-stats?season=" + season);
      } else {
        url = new URL("https://api.ftcscout.org/rest/v1/teams/" + teamNum + "/quick-stats");
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
      data = new JSONObject(response.toString());
      properSetup = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /**
  *  @Return int rank || -1
  * Returns the total rank of a team if it is contained within data, else it returns -1;
  */
  public int rank(){
    if(!data.isNull("tot")){
      JSONObject tot = data.getJSONObject("tot");
      if(!tot.isNull("rank")){
        return tot.getInt("rank");
      }
    }
    return -1;
  }
}