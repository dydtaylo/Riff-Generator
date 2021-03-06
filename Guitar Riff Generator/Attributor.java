import java.util.ArrayList;

public class Attributor{
  // the whole purpose of this class is to give each riffStatistic its attribute tags
  ArrayList<RiffStatistics> riffStatistics;
  String currentNoteRepeatAttribute;
  String currentStringRepeatAttribute;
  String currentDistanceFromStartAttribute;
  String currentIntermediateDistancesAttribute;
  
  public Attributor(ArrayList<RiffStatistics> rS){
    riffStatistics = rS;
    currentNoteRepeatAttribute = "nra-0";
    currentStringRepeatAttribute = "sra-0";
    currentDistanceFromStartAttribute = "dfsa-0";
    currentIntermediateDistancesAttribute = "ida-0";
  }
  
  public ArrayList<String> giveAttributes(RiffStatistics riffStats){
    ArrayList<String> attributes = new ArrayList<String>();
    Riff riff = riffStats.getRiff();
    
    // noteRepeatAttribute
    RiffStatistics current = null;
    String noteRepeatAttribute = null;
    for(int i = 0; i < riffStatistics.size(); i++){
      current = riffStatistics.get(i);
      if(!current.getRiff().equals(riff)){
        if(sameNoteRepeatStructure(current, riffStats)){
          if(current.hasAttribute("nra")){
            noteRepeatAttribute = current.getAttribute("nra");
            break;
          }
        }
      }
    }
    if(noteRepeatAttribute == null){
      noteRepeatAttribute = currentNoteRepeatAttribute;
      // increment currentNoteRepeatAttribute
      String[] pieces = currentNoteRepeatAttribute.split("-");
      currentNoteRepeatAttribute = "nra-" + (Integer.parseInt(pieces[1]) + 1);
    }
    attributes.add(noteRepeatAttribute);
    
    // stringRepeatAttribute
    current = null;
    String stringRepeatAttribute = null;
    for(int i = 0; i < riffStatistics.size(); i++){
      current = riffStatistics.get(i);
      if(!current.getRiff().equals(riff)){
        if(sameStringRepeatStructure(current, riffStats)){
          if(current.hasAttribute("sra")){
            stringRepeatAttribute = current.getAttribute("sra");
            break;
          }
        }
      }
    }
    if(stringRepeatAttribute == null){
      stringRepeatAttribute = currentStringRepeatAttribute;
      // increment currentStringRepeatAttribute
      String[] pieces = currentStringRepeatAttribute.split("-");
      currentStringRepeatAttribute = "sra-" + (Integer.parseInt(pieces[1]) + 1);
    }
    attributes.add(stringRepeatAttribute);
    
    // distanceFromStartAttribute
    current = null;
    String distanceFromStartAttribute = null;
    for(int i = 0; i < riffStatistics.size(); i++){
      current = riffStatistics.get(i);
      if(!current.getRiff().equals(riff)){
        if(sameDistanceFromStart(current, riffStats)){
          if(current.hasAttribute("dfsa")){
            distanceFromStartAttribute = current.getAttribute("dfsa");
            break;
          }
        }
      }
    }
    if(distanceFromStartAttribute == null){
      distanceFromStartAttribute = currentDistanceFromStartAttribute;
      // increment currentDistanceFromStartAttribute
      String[] pieces = currentDistanceFromStartAttribute.split("-");
      currentDistanceFromStartAttribute = "dfsa-" + (Integer.parseInt(pieces[1]) + 1);
    }
    attributes.add(distanceFromStartAttribute);
    
    // intermediateDistancesAttribute
    current = null;
    String intermediateDistancesAttribute = null;
    for(int i = 0; i < riffStatistics.size(); i++){
      current = riffStatistics.get(i);
      if(!current.getRiff().equals(riff)){
        if(sameIntermediateDistances(current, riffStats)){
          if(current.hasAttribute("ida")){
            intermediateDistancesAttribute = current.getAttribute("ida");
            break;
          }
        }
      }
    }
    if(intermediateDistancesAttribute == null){
      intermediateDistancesAttribute = currentIntermediateDistancesAttribute;
      // increment currentIntermediateDistancesAttribute
      String[] pieces = currentIntermediateDistancesAttribute.split("-");
      currentIntermediateDistancesAttribute = "ida-" + (Integer.parseInt(pieces[1]) + 1);
    }
    attributes.add(intermediateDistancesAttribute);
    
    // System.out.println(riffStats);
    // System.out.println(attributes);
    return attributes;
  }
  
  public Boolean sameAttributes(RiffStatistics riffStats1, RiffStatistics riffStats2){
    ArrayList<String> attributes1 = riffStats1.getAttributes();
    ArrayList<String> attributes2 = riffStats2.getAttributes();
    for(String att: attributes1){
      if(!attributes2.contains(att)){
        return false;
      }
    }
    for(String att: attributes2){
      if(!attributes1.contains(att)){
        return false;
      }
    }
    System.out.println(riffStats1);
    System.out.println(riffStats2);
    System.out.println("--------------------------------------------------------------------------");
    return true;
  }
  
  public int numMutualAttributes(RiffStatistics riffStats1, RiffStatistics riffStats2){
    ArrayList<String> attributes1 = riffStats1.getAttributes();
    ArrayList<String> attributes2 = riffStats2.getAttributes();
    int mutualAttributes = 0;
    for(String att: attributes1){
      if(attributes2.contains(att)){
        mutualAttributes++;
      }
    }
    return mutualAttributes;
  }
  
  public boolean sameNoteRepeatStructure(RiffStatistics riffStats1, RiffStatistics riffStats2){
    ArrayList<Double> noteRepeatRate1 = riffStats1.getNoteRepeatRate();
    ArrayList<Double> noteRepeatRate2 = riffStats2.getNoteRepeatRate();
    double tolerance = .1d; // allowed variance between two corresponding non-zero rates
    
    double rate1 = 0d, rate2 = 0d;
    for(int i = 0; i < Math.min(noteRepeatRate1.size(), noteRepeatRate2.size()); i++){
      rate1 = noteRepeatRate1.get(i);
      rate2 = noteRepeatRate2.get(i);
      if(Math.abs(rate1 - rate2) > tolerance){
        return false;
      }
    }
    return true;
  }
  public boolean sameStringRepeatStructure(RiffStatistics riffStats1, RiffStatistics riffStats2){    
    ArrayList<Double> stringRepeatRate1 = riffStats1.getStringRepeatRate();
    ArrayList<Double> stringRepeatRate2 = riffStats2.getStringRepeatRate();
    double tolerance = .05d; // allowed variance between two corresponding non-zero rates
    
    double rate1 = 0d, rate2 = 0d;
    for(int i = 0; i < Math.min(stringRepeatRate1.size(), stringRepeatRate2.size()); i++){
      rate1 = stringRepeatRate1.get(i);
      rate2 = stringRepeatRate2.get(i);
      if(Math.abs(rate1 - rate2) > tolerance){
        return false;
      }
    }
    return true;
  }
  
  public boolean sameDistanceFromStart(RiffStatistics riffStats1, RiffStatistics riffStats2){
    // minimum acceptable difference
    double tolerance = .5d;
    return Math.abs(riffStats1.getDistanceFromStart() - riffStats2.getDistanceFromStart()) < tolerance;
  }
  
  public boolean sameIntermediateDistances(RiffStatistics riffStats1, RiffStatistics riffStats2){
    // minimum acceptable difference (this one greatly affects runtime)
    double tolerance = 1d;
    ArrayList<Double> intermediateDistances1 = riffStats1.getIntermediateDistances();
    ArrayList<Double> intermediateDistances2 = riffStats2.getIntermediateDistances();
    for(int i = 0; i < Math.min(intermediateDistances1.size(), intermediateDistances2.size()); i++){
      if(Math.abs(intermediateDistances1.get(i) - intermediateDistances2.get(i)) > tolerance){
        return false;
      }
    } 
    return true;
  }
}