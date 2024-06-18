/********************************************************************************************/
/*
  Written by Emma Perez Rovira
  Final touches added on 6/6/2024
  Written as both a AP CSA block E assignment and as a hobby project that will be expanded on
  This is a tool that once completed should help simplify the scouting process for FTC teams
*/


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.util.Base64;
import javafx.scene.control.ListView;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.text.Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import java.net.MalformedURLException;

/**
Todo:
Clean up code (started on 5/28)
Make GUI more interesting - Done
Todo (if time permits):
Awards listing - Done
Team comparisons
*/
public class Main extends Application 
{ 
  private double screenWidthDouble = 550;
  private int screenLength = (int) (screenWidthDouble * (16.0/9));
  private int screenWidth = (int)screenWidthDouble;
  private Scene starterScene;
  private Scene mainScene;
  private Scene statsScene;
  private Scene awardsScene;

  APITeamManager searchSystem;

  
  @Override
  public void start(Stage primaryStage) {
    Button enterPage = new Button("Done");
    TextField seasonField = new TextField();
    YearStorage season = new YearStorage();
    Button returnToSeasonSelection = new Button("Change season");
    Button returnButton = new Button("Return");
    Button stats = new Button("Stats");
    Button button = new Button("Search");
    Button awards = new Button("Awards");

    returnToSeasonSelection.setStyle("-fx-background-color: green;");
    returnButton.setStyle("-fx-background-color: MediumSeaGreen");
    stats.setStyle("-fx-background-color: yellow;");
    button.setStyle("-fx-background-color: pink;");
    awards.setStyle("-fx-background-color: lightBlue;");
    
    Label label;
    TextField tf = new TextField("");;
    VBox vbox;
    seasonField.setPromptText("Enter season from 2019 to present");
    seasonField.setMaxWidth(370);
    tf.setMaxWidth(200);
    tf.setPromptText("Enter a valid team number");

    awards.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        String[] awardsArr = searchSystem.awards();
        ObservableList<String> awards = FXCollections.observableArrayList();
        ListView<String> awardsList = new ListView<>(awards);
        for(int i = 0; i < awardsArr.length; i++){
         awards.add(awardsArr[i]);
        }
        awardsList.setStyle("-fx-font-family: Monospaced;");
        Label awardsTitle = new Label("Awards that this team has won this season:");
        
        VBox awardsBox = new VBox(awardsTitle, awardsList, returnButton, returnToSeasonSelection);
        awardsBox.setStyle("-fx-background-color: lightPink;");
        awardsBox.setAlignment(Pos.CENTER);
        awardsBox.setSpacing(20);
        awardsScene = new Scene(awardsBox, screenLength, screenWidth);
        System.out.println(javafx.scene.text.Font.getFamilies());
        changeToAwardsScene(primaryStage);
      }
    });

    label = new Label("Enter a valid team number");

    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        searchSystem = new APITeamManager(Integer.valueOf(tf.getText()), season.getYear());
        if(searchSystem.isATeam()){
          label.setText("Team " + searchSystem.teamNumber() + ": " + searchSystem.teamName());
        } else {
          label.setText("Invalid search paramaters");
        }

      }
    });
    
    vbox = new VBox(label, tf, button, stats, returnToSeasonSelection);
    vbox.setSpacing(20);
    vbox.setAlignment(Pos.CENTER);
    vbox.setStyle("-fx-background-color: lightBlue;");
    mainScene = new Scene(vbox, screenLength, screenWidth);
       
    returnButton.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        VBox vbox = new VBox(label, tf, button, stats, returnToSeasonSelection);
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: lightBlue;");
        mainScene = new Scene(vbox, screenLength, screenWidth);
        changeToMainScene(primaryStage);
      }
    });

    stats.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        ObservableList<String> stats = FXCollections.observableArrayList();
        ListView<String> statsList = new ListView<>(stats);
        
        searchSystem = new APITeamManager(Integer.valueOf(tf.getText()), season.getYear());
        if(searchSystem.isATeam()){
          if(!searchSystem.properSetup()){
            label.setText("Team " + searchSystem.teamNumber() + ": " + searchSystem.teamName() + "  [Did not compete on specified season]");
            System.out.println("PASSED HERE");
          } else {
            label.setText("Team " + searchSystem.teamNumber() + ": " + searchSystem.teamName());
          }
        } else {          
          label.setText("Invalid search paramaters");         
        }
               
        Label OPR;
        Label isSchoolTeam;
        Label whereFrom;
        whereFrom = new Label("From: " + searchSystem.city() + ", " + searchSystem.state() + ", " + searchSystem.country() + ".");
        Label rookieYear = new Label("Their rookie year was: " + searchSystem.rookieYear());
        if(searchSystem.isSchoolTeam() == 1){
          isSchoolTeam = new Label("This team is a school team");
        } else if(searchSystem.isSchoolTeam() == 0){
          isSchoolTeam = new Label("This team is not a school team");
        } else {
          isSchoolTeam = new Label("Data is inconclusive to wether they are a school team");
        }
        Label team = new Label("Team " + searchSystem.teamNumber() + ": " + searchSystem.teamName());
        if(searchSystem.seasonOPR() > Integer.MIN_VALUE){
         stats.add("OPR season average: " + String.format("%.2f", searchSystem.seasonOPR()).substring(0, 5));
        } else {
          stats.add("DID NOT COMPETE ON SPECIFIED SEASON");
        }
        
        String[] OPRArray = searchSystem.latestOPR();
        
        if(OPRArray != null){
          stats.add("Last event they participated in was: " + OPRArray[1]);
          stats.add("Last event they had an OPR of: " + OPRArray[0]);
          stats.add("Last event they had an auto OPR of: " + OPRArray[2]);
          stats.add("Last event they had an engame OPR of: " + OPRArray[3]);
          stats.add("Last event they had a driver controlled OPR of: " + OPRArray[4]);
          
        } else {
          stats.add("No data on latest event");
          stats.add("No data on OPR");
        }
        if(searchSystem.rank() != -1){
          stats.add("Global ranking: " + searchSystem.rank());
        }
        if(searchSystem.latestAverageScore() != null){
          stats.add("Last event they had an average of: " + searchSystem.latestAverageScore()[0] + " points");
        } else {
          stats.add("No data on latest average score");
        }
        
        VBox newBox = new VBox(team, whereFrom, rookieYear, statsList, isSchoolTeam, awards, returnButton, returnToSeasonSelection);
        newBox.setAlignment(Pos.CENTER);
        newBox.setSpacing(20);
        newBox.setStyle("-fx-background-color: white;");
        statsScene = new Scene(newBox, screenLength, screenWidth);
        changeToStatsScene(primaryStage);
        primaryStage.show();
      }
    });

    enterPage.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        if(!seasonField.getText().equals("")){
          season.setYear(Integer.valueOf(seasonField.getText()));
        } else {
          season.setYear(0);
        }
        String tfHolder = tf.getText();
        if(!tfHolder.equals("") && !tfHolder.equals("Team number")){
          searchSystem = new APITeamManager(Integer.valueOf(tf.getText()), season.getYear());
          if(searchSystem.isATeam()){
            label.setText("Team " + searchSystem.teamNumber() + ": " + searchSystem.teamName());
          } else {
            label.setText("Invalid search paramaters");
          }
        }
        VBox vbox = new VBox(label, tf, button, stats, returnToSeasonSelection);
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: lightBlue;");
        mainScene = new Scene(vbox, screenLength, screenWidth);
        changeToMainScene(primaryStage);
      }
    });
    
    Label starterLabel = new Label("Enter season year below: (leave blank if current season)");
    VBox starter = new VBox(starterLabel, seasonField, enterPage);
    starter.setAlignment(Pos.CENTER);
    starterScene = new Scene(starter, screenLength, screenWidth);
    returnToSeasonSelection.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        VBox vbox = new VBox(label, tf, button, stats, returnToSeasonSelection);
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: lightBlue;");
        mainScene = new Scene(vbox, screenLength, screenWidth);
        changeToStarterScene(primaryStage);
      }
    });
    primaryStage.setTitle("FTC quick scout");
    changeToStarterScene(primaryStage);
    primaryStage.show();
  } 

  public static void main(String[] args) {
    launch(args);
  }
  /**
  *  @Input Stage stage
  * Changes the scene of the stage given to mainsScene
  */
  public void changeToMainScene(Stage stage){
    stage.setScene(mainScene);
  }
  /**
  *  @Input Stage stage
  * Changes the scene of the stage given to starterScene
  */
  public void changeToStarterScene(Stage stage){
    stage.setScene(starterScene);
  }
  /**
  *  @Input Stage stage
  * Changes the scene of the stage given to statsScene
  */
  public void changeToStatsScene(Stage stage){
    stage.setScene(statsScene);
  }
  /**
  *  @Input Stage stage
  * Changes the scene of the stage given to awardsScene
  */
  public void changeToAwardsScene(Stage stage){
    stage.setScene(awardsScene);
  }
} 
