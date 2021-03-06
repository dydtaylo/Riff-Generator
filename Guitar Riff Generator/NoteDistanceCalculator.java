import java.util.ArrayList;

public class NoteDistanceCalculator{
  
  public double distance(Note firstNote, Note secondNote){
    ArrayList<StringFret> firstStringFrets = firstNote.getStringFrets();
    ArrayList<StringFret> secondStringFrets = secondNote.getStringFrets();
    int firstString = 0, firstFret = 0;
    int secondString = 0, secondFret = 0;
    for(StringFret sf: firstStringFrets){
      firstString += sf.getString();
      firstFret += sf.getFret();
    }
    for(StringFret sf: secondStringFrets){
      secondString += sf.getString();
      secondFret += sf.getFret();
    }
    // average all of the variabls
    firstString /= firstStringFrets.size();
    firstFret /= firstStringFrets.size();
    secondString /= secondStringFrets.size();
    secondFret /= secondStringFrets.size(); 
    
    return 2 * Math.abs(secondString - firstString) + slopeSign(firstString, firstFret, secondString, secondFret) * Math.abs(secondFret - firstFret);
  }
  
  public int slopeSign(int firstString, int firstFret, int secondString, int secondFret){
    double slope = noteSlope(firstString, firstFret, secondString, secondFret);
    if(Math.round(slope) == 0){
      return 1;
    }
    return (int) (slope/Math.abs(slope));
  }
  public double noteSlope(int firstString, int firstFret, int secondString, int secondFret){
    if(secondString == firstString){
      return 1;
    }
    return (double) (secondFret - firstFret)/(secondString - firstString);
  }
}