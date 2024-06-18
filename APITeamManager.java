public class APITeamManager{
  private APIGeneral general;
  private APITeamStats stats;
  private APIQuickStats quickStats;
  private APITeamAwards awards;

  public APITeamManager(int teamNum, int season){
    general = new APIGeneral(teamNum);
    stats = new APITeamStats(teamNum, season);
    quickStats = new APIQuickStats(teamNum, season);
    awards = new APITeamAwards(teamNum, season);
  }
  public boolean properSetup(){
    // System.out.print("QS: " + quickStats.properSetup() + " ST: " + stats.properSetup() + " AW: " + awards.properSetup());
    return (quickStats.properSetup() && stats.properSetup() && awards.properSetup());
  }
  public String teamName(){
    return general.teamName();
  }
  public int teamNumber(){
    return general.teamNumber();
  }
  public int rookieYear(){
    return general.rookieYear();
  }
  public String country(){
    return general.country();
  }
  public String state(){
    return general.state();
  }
  public String city(){
    return general.city();
  }
  public String[] sponsors(){
    return general.sponsors();
  }
  public String website(){
    return general.website();
  }
  public int isSchoolTeam(){
    return general.isSchoolTeam();
  }
  public double seasonOPR(){
    return stats.seasonOPR();
  }
  public boolean isATeam(){
    return general.isATeam();
  }
  public String[] latestAverageScore(){
    return stats.latestAverageScore();
  }
  public String[] latestOPR(){
    return stats.latestOPR();
  }
  public int rank(){
    return quickStats.rank();
  }
  public String[] awards(){
    return awards.getAllAwards();
  }
  
}