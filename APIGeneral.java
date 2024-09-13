19162  import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import java.net.MalformedURLException;

public class APIGeneral{
  private String baseUrl = "https://api.ftcscout.org/rest/v1/teams/";
  private JSONObject data;
  private boolean isATeam = true;

  /**
  *  @Input String baseUrl
  * Generates a URL from baseUrl and passes it to the call function.
  */
  public APIGeneral(int teamNum){
    baseUrl += teamNum + "/";
    try{
      URL url = new URL(baseUrl);
      call(url);
    } catch (MalformedURLException e){
      e.printStackTrace();
      System.out.print("URL FAILED");
    }
  }
  /**
  *  @Return boolean isATeam
  */
  public boolean isATeam(){
    return isATeam;
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
    } catch (IOException e) {
      isATeam = false;
      e.printStackTrace();
    }
  }
  /**
  * Re runs call with the baseUrl.
  * Useful to see if there have been any changes since the last call.
  */
  public void update(){
    try{
      URL url = new URL(baseUrl);
      call(url);
    } catch (MalformedURLException e){
      e.printStackTrace();
      System.out.print("URL FAILED");
    }
  }
  /**
  *  @Return int teamNum || -1
  * Returns teamNum if a team number is within data, however if it is not it returns -1
  */
  public int teamNumber(){
    if(!data.isNull("number")){
      return data.getInt("number");
    }
    return -1;
  }
  /**
  *  @Return String teamName || null
  * Returns teamName if it is within data, else it returns null;
  */
  public String teamName(){
    if(!data.isNull("name")){
      return data.getString("name");
    }
    return null;
  }
  /**
  *  @Return String country || null
  * Returns country if it is within data, else it returns null;
  */
  public String country(){
    if(!data.isNull("country")){
    return data.getString("country");
    }
    return null;
  }
  /**
  *  @Return String state || null
  * Returns state if it is within data, else it returns null;
  */
  public String state(){
    if(!data.isNull("state")){
    return data.getString("state");
    }
    return null;
  }
  /**
  *  @Return String city || null
  * Returns city if it is within data, else it returns null;
  */
  public String city(){
    if(!data.isNull("city")){
      return data.getString("city");
    }
    return null;
  }
  /**
  *  @Return String[] sponsors || null
  * Returns sponsors if the team has at least one sponsor, else it returns null;
  */
  public String[] sponsors(){
    if(!data.isNull("sponsors")){
      JSONArray arr = data.getJSONArray("sponsors");
      String[] strings = new String[arr.length()];
      for(int i = 0; i < arr.length(); i++){
        strings[i] = arr.get(i).toString();
      }
      return strings;
    }
    return null;
  }
  /**
  *  @Return int rookieYear || -1
  * Returns rookieYear if it is within data, else it returns -1;
  */
  public int rookieYear(){
    if(!data.isNull("rookieYear")){
      return data.getInt("rookieYear");
    }
    return -1;
  }
  /**
  *  @Return String website || null
  * Returns website if it is within data, else it returns null;
  */
  public String website(){
    if(!data.isNull("website")){
      return data.get("website").toString();
    }
    return null;
  }
  /**
  *  @Return -1 || 0 || 1
  * Returns 1 if the team in question is a school team, 0 if it is not, and -1 if the data is not contained within data;
  */
  public int isSchoolTeam(){
    if(!data.isNull("schoolName")){
      if(data.getString("schoolName").equals("Family/Community")){
        return 0;
      } else {
        return 1;
      }
    }
    return -1;
    
  }
  
}