/********************************************************************************************/
/*
  Written by Emma
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

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
Team comparisons - Done
Add some pride flag themes - Done :)
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

  private Scene multiTeamSelection;
  private Scene multiTeamStats;
  private Scene multiTeamAwards;

  APITeamManager searchSystem;
  APITeamManager secondarySearchSystem;

  
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
    
    Button teamComparisonStart = new Button("Team Comparison");
    Button teamComparisonStats = new Button("Stats");
    Button teamComparisonAwards = new Button("Awards");
    Button multiTeamsEntered = new Button("Done");
    Button returnMulti = new Button("Return");
    TextField team1 = new TextField();
    TextField team2 = new TextField();
    team1.setPromptText("Team number 1");
    team2.setPromptText("Team number 2");
    team1.setMaxWidth(screenWidth/2);
    team2.setMaxWidth(screenWidth/2);

    returnToSeasonSelection.setStyle("-fx-background-color: purple;");
    returnToSeasonSelection.setTextFill(Color.WHITE);
    returnButton.setStyle("-fx-background-color: white");
    //returnButton.setTextFill(Color.WHITE);
    stats.setStyle("-fx-background-color: #707070;");
    stats.setTextFill(Color.WHITE);
    button.setStyle("-fx-background-color: black;");
    button.setTextFill(Color.WHITE);
    awards.setStyle("-fx-background-color: #FF5011;");

    teamComparisonStats.setStyle("-fx-background-color: lightSeaGreen;");
    teamComparisonStart.setStyle("-fx-background-color: white;");
    teamComparisonAwards.setStyle("-fx-background-color: white;");
    returnMulti.setStyle("-fx-background-color: lightBlue");
    
    Label label;
    TextField tf = new TextField("");
    VBox vbox;
    seasonField.setPromptText("Enter season from 2019 to present");
    seasonField.setMaxWidth(370);
    tf.setMaxWidth(200);
    tf.setPromptText("Enter a valid team number");

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

    vbox = new VBox(label, tf, button, stats, teamComparisonStart, returnToSeasonSelection);
    vbox.setSpacing(20);
    vbox.setAlignment(Pos.CENTER);
    vbox.setStyle("-fx-background-color: lightBlue;");
    mainScene = new Scene(vbox, screenLength, screenWidth);

    /**
    * Sets search systems based on the user input, then changes to the stats scene
    * Discontinued button, as it is quite useless in the face of the stats button :P
    */
    multiTeamsEntered.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        searchSystem = new APITeamManager(Integer.valueOf(team1.getText()), season.getYear());
        secondarySearchSystem = new APITeamManager(Integer.valueOf(team2.getText()), season.getYear());
        changeToTeamComparisonStats(primaryStage);
      }
    });
    /**
    * Makes the hub scene for the multi team comparison, then changes the scene to the afformentioned scene
    */
    teamComparisonStart.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        returnButton.setStyle("-fx-background-color: royalblue");
        VBox startComparisonBox = new VBox(label, team1, team2, teamComparisonStats, teamComparisonAwards, returnButton);
        startComparisonBox.setStyle("-fx-background-color: lightBlue;");
        startComparisonBox.setAlignment(Pos.CENTER);
        startComparisonBox.setSpacing(20);
        multiTeamSelection = new Scene(startComparisonBox, screenLength, screenWidth);
        changeToMultiTeamSelection(primaryStage);
      }
    });
    returnMulti.setOnAction(new EventHandler<ActionEvent>(){
        @Override public void handle(ActionEvent e){
          
          VBox startComparisonBox = new VBox(label, team1, team2, teamComparisonStats, teamComparisonAwards, returnButton);
          startComparisonBox.setStyle("-fx-background-color: lightBlue;");
          startComparisonBox.setAlignment(Pos.CENTER);
          startComparisonBox.setSpacing(20);
          multiTeamSelection = new Scene(startComparisonBox, screenLength, screenWidth);
          changeToMultiTeamSelection(primaryStage);
        }
      });
    /**
    * Calculates and formats all the stats for both teams, and adds them to a gridpane, making that the new multi stats scene
    */
    teamComparisonAwards.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        searchSystem = new APITeamManager(Integer.valueOf(team1.getText()), season.getYear());
        secondarySearchSystem = new APITeamManager(Integer.valueOf(team2.getText()), season.getYear());
        if(searchSystem.properSetup() && secondarySearchSystem.properSetup()){
        String[] awardsArr1 = searchSystem.awards();
        ObservableList<String> awards1 = FXCollections.observableArrayList();
        ListView<String> awardsList1 = new ListView<>(awards1);
        for(int i = 0; i < awardsArr1.length; i++){
         awards1.add(awardsArr1[i]);
        }
        awardsList1.setStyle("-fx-font-family: Monospaced;");
        Label awardsTitle1 = new Label("Awards that " + searchSystem.teamName() + " has won this season:");

        

        String[] awardsArr2 = secondarySearchSystem.awards();
        ObservableList<String> awards2 = FXCollections.observableArrayList();
        ListView<String> awardsList2 = new ListView<>(awards2);
        for(int i = 0; i < awardsArr2.length; i++){
         awards2.add(awardsArr2[i]);
        }
        awardsList2.setStyle("-fx-font-family: Monospaced;");
        Label awardsTitle2 = new Label("Awards that " + secondarySearchSystem.teamName() + " has won this season:");

        GridPane gridPane = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(
            col1,                      // Liquid width for column 1
            col2                       // Liquid width for column 2
        );
        gridPane.add(awardsTitle1, 0, 0);
        gridPane.add(awardsList1, 0, 1);
        gridPane.add(awardsTitle2, 1, 0);
        gridPane.add(awardsList2, 1, 1);
        gridPane.add(returnMulti, 1, 2);
        gridPane.setStyle("-fx-background-color: lightPink;");
        multiTeamAwards = new Scene(gridPane, screenLength, screenWidth);
        changeToTeamComparisonAwards(primaryStage);
        } else {
          label.setText("Invalid search paramaters");
        }
      }
    });
    teamComparisonStats.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        searchSystem = new APITeamManager(Integer.valueOf(team1.getText()), season.getYear());
        secondarySearchSystem = new APITeamManager(Integer.valueOf(team2.getText()), season.getYear());
        if(searchSystem.properSetup() && secondarySearchSystem.properSetup()){
        ObservableList<String> stats = FXCollections.observableArrayList();
        ListView<String> statsList = new ListView<>(stats);

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



        ObservableList<String> stats2 = FXCollections.observableArrayList();
        ListView<String> statsList2 = new ListView<>(stats2);

        

        Label OPR2;
        Label isSchoolTeam2;
        Label whereFrom2;
        whereFrom2 = new Label("From: " + secondarySearchSystem.city() + ", " + secondarySearchSystem.state() + ", " + secondarySearchSystem.country() + ".");
        Label rookieYear2 = new Label("Their rookie year was: " + secondarySearchSystem.rookieYear());
        if(secondarySearchSystem.isSchoolTeam() == 1){
          isSchoolTeam2 = new Label("This team is a school team");
        } else if(secondarySearchSystem.isSchoolTeam() == 0){
          isSchoolTeam2 = new Label("This team is not a school team");
        } else {
          isSchoolTeam2 = new Label("Data is inconclusive to wether they are a school team");
        }
        Label team2 = new Label("Team " + secondarySearchSystem.teamNumber() + ": " + secondarySearchSystem.teamName());
        if(secondarySearchSystem.seasonOPR() > Integer.MIN_VALUE){
         stats2.add("OPR season average: " + String.format("%.2f", secondarySearchSystem.seasonOPR()).substring(0, 5));
        } else {
          stats2.add("DID NOT COMPETE ON SPECIFIED SEASON");
        }

        String[] OPRArray2 = secondarySearchSystem.latestOPR();

        if(OPRArray != null){
          stats2.add("Last event they participated in was: " + OPRArray2[1]);
          stats2.add("Last event they had an OPR of: " + OPRArray2[0]);
          stats2.add("Last event they had an auto OPR of: " + OPRArray2[2]);
          stats2.add("Last event they had an engame OPR of: " + OPRArray2[3]);
          stats2.add("Last event they had a driver controlled OPR of: " + OPRArray2[4]);

        } else {
          stats2.add("No data on latest event");
          stats2.add("No data on OPR");
        }
        if(secondarySearchSystem.rank() != -1){
          stats2.add("Global ranking: " + secondarySearchSystem.rank());
        }
        if(secondarySearchSystem.latestAverageScore() != null){
          stats2.add("Last event they had an average of: " + secondarySearchSystem.latestAverageScore()[0] + " points");
        } else {
          stats2.add("No data on latest average score");
        }


        if(secondarySearchSystem.isATeam() && searchSystem.isATeam()){
          if(!secondarySearchSystem.properSetup() && !searchSystem.properSetup()){
            label.setText("Neither of these teams competed this season: " + "Team " + secondarySearchSystem.teamNumber() + ": " + secondarySearchSystem.teamName() + ", & " + "Team " + searchSystem.teamNumber() + ": " + searchSystem.teamName());
          } else if(!secondarySearchSystem.properSetup()){
            label.setText("Team " + secondarySearchSystem.teamNumber() + ": " + secondarySearchSystem.teamName() + "  [Did not compete on specified season]");
            System.out.println("PASSED HERE");
          } else if(!searchSystem.properSetup()){
            label.setText("Team " + secondarySearchSystem.teamNumber() + ": " + secondarySearchSystem.teamName() + "  [Did not compete on specified season]");
          }else {
            label.setText("Both teams competed this season: " + "Team " + secondarySearchSystem.teamNumber() + ": " + secondarySearchSystem.teamName() + ", & " + "Team " + searchSystem.teamNumber() + ": " + searchSystem.teamName());
          }
        } else {          
          label.setText("Invalid search paramaters");         
        }

        GridPane statsGrid = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        statsGrid.getColumnConstraints().addAll(
            col1,                      // Liquid width for column 1
            col2                       // Liquid width for column 2
        );
        statsGrid.add(team, 0,0);
        statsGrid.add(team2, 1,0);
        statsGrid.add(whereFrom,0,1);
        statsGrid.add(whereFrom2,1,1);
        statsGrid.add(rookieYear,0,2);
        statsGrid.add(rookieYear2,1,2);
        statsGrid.add(isSchoolTeam,0,3);
        statsGrid.add(isSchoolTeam2,1,3);
        statsGrid.add(statsList, 0,5);
        statsGrid.add(statsList2, 1,5);
        statsGrid.add(returnMulti, 1,6);
        statsGrid.setStyle("-fx-background-color: lightPink;");
        multiTeamStats = new Scene(statsGrid, screenLength, screenWidth);
        changeToTeamComparisonStats(primaryStage);
        } else {
          label.setText("Invalid search paramaters");
        }
      }
    });
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

    
       
    returnButton.setOnAction(new EventHandler<ActionEvent>(){
      @Override public void handle(ActionEvent e){
        returnButton.setStyle("-fx-background-color: white");
        VBox vbox = new VBox(label, tf, button, stats, teamComparisonStart, returnToSeasonSelection);
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
        newBox.setStyle("-fx-background-color: lightBlue;");
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
        VBox vbox = new VBox(label, tf, button, stats, teamComparisonStart, returnToSeasonSelection);
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
  /**
  *  @Input Stage stage
  * Changes the scene of the stage given to multiTeamSelection
  */
  public void changeToMultiTeamSelection(Stage stage){
    stage.setScene(multiTeamSelection);
  }
  /**
  *  @Input Stage stage
  * Changes the scene of the stage given to multiTeamStats
  */
  public void changeToTeamComparisonStats(Stage stage){
    stage.setScene(multiTeamStats);
  }
  /**
  *  @Input Stage stage
  * Changes the scene of the stage given to multiTeamAwards
  */
  public void changeToTeamComparisonAwards(Stage stage){
    stage.setScene(multiTeamAwards);

  }
} 
