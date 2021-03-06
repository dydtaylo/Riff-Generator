import java.util.ArrayList;

public class RiffStatistics{
  
  private Riff riff;
  Attributor attributor;
  NoteDistanceCalculator distanceCalculator;
  ArrayList<String> attributes;
  private int numNotes;
  private int numStrings;
  private int fretRange;
  private int stringRange;
  private ArrayList<Integer> noteRepeatFrequency; // number of notes between repeated notes
                                         //// where the index is how many notes it takes to repeat
                                         //// and the value is how many times it occurs
  private int nonNoteRepeatFrequency; // number of times a note doesn't repeat
  private ArrayList<Double> noteRepeatRate; // percentage representation of noteRepeatFrequency
  private ArrayList<Integer> stringRepeatFrequency; // number of notes between repeated strings
                                           //// where the index is how many notes it takes to repeat
                                           //// and the value is how many times it occurs
  private int nonStringRepeatFrequency; // number of times a string doesn't repeat
  private ArrayList<Double> stringRepeatRate; // percentage representation of stringRepeatFrequency
  private double averageDistance; // "distance" between each note
                                  //// "distance" is calculated as if the fretboard were a cartesian grid
                                  //// with x-cord: fret-number and y-cord: string-number
                                  //// however, the string number has extra weight since it has greater effect
  private double distanceFromStart;
  private ArrayList<Double> intermediateDistances; // the distance between notes
  
  public RiffStatistics(Riff r, Attributor a){
    riff = r;
    attributor = a;
    distanceCalculator = new NoteDistanceCalculator();
    attributes = new ArrayList<String>();
    numNotes = 0;
    numStrings = 0;
    fretRange = 0;
    stringRange = 0;
    noteRepeatFrequency = new ArrayList<Integer>();
    nonNoteRepeatFrequency = 0;
    noteRepeatRate = new ArrayList<Double>();
    stringRepeatFrequency = new ArrayList<Integer>();
    nonStringRepeatFrequency = 0;
    stringRepeatRate = new ArrayList<Double>();
    averageDistance = 0d;
    distanceFromStart = 0d;
    intermediateDistances = new ArrayList<Double>();
  }
  public RiffStatistics(Riff r){
    this(r, null);
  }
  
  public void determineAttributes(){
    attributes = attributor.giveAttributes(this);
  }
  
  public void conductStatistics(){
    ArrayList<Note> notes = riff.getNotes();
      
    // count num notes
    this.setNumNotes(notes.size());
      
    // count num strings
    ArrayList<Integer> seenStrings = new ArrayList<Integer>();
    ArrayList<Integer> strings = null;
    for(Note n: notes){
      strings = n.getStrings();
      for(Integer s: strings){
        if(!seenStrings.contains(s)){
          seenStrings.add(s);
        }
      }
    }
    this.setNumStrings(seenStrings.size());
    
    // compute fret range
    int smallestFret = Integer.MAX_VALUE;
    int largestFret = Integer.MIN_VALUE;
    for(Note n: notes){
      for(StringFret sf: n.getStringFrets()){
        smallestFret = Math.min(smallestFret, sf.getFret());
        largestFret = Math.max(largestFret, sf.getFret());
      }
    }
    fretRange = largestFret - smallestFret;
    
    // compute string range
    int smallestString = Integer.MAX_VALUE;
    int largestString = Integer.MIN_VALUE;
    for(Note n: notes){
      for(StringFret sf: n.getStringFrets()){
        smallestString = Math.min(smallestString, sf.getString());
        largestString = Math.max(largestString, sf.getString());
      }
    }
    stringRange = largestString - smallestString;
    
    // compute noteRepeatFrequency
    for(int i = 0; i < numNotes; i++){
      noteRepeatFrequency.add(0);
      noteRepeatRate.add(0d);
    }
    Note outerNote = null, innerNote = null;
    for(int i = 0; i < notes.size() - 1; i++){
      outerNote = notes.get(i);
      for(int j = i + 1; j < notes.size(); j++){
        innerNote = notes.get(j);
        if(outerNote.equals(innerNote)){
          noteRepeatFrequency.set(j - i, noteRepeatFrequency.get(j - i) + 1);
          break;
        }
        if(j == notes.size() - 1){
          nonNoteRepeatFrequency++; // if the note doesn't repeat
        }
      }
    }
    for(int i = 0; i < noteRepeatRate.size(); i++){
      noteRepeatRate.set(i, (double) noteRepeatFrequency.get(i)/ (double) numNotes);
    }
      
    // compute stringRepeatFrequency
    for(int i = 0; i < numNotes; i++){
      stringRepeatFrequency.add(0);
      stringRepeatRate.add(0d);
    }
    outerNote = null;
    innerNote = null;
    for(int i = 0; i < notes.size() - 1; i++){
      outerNote = notes.get(i);
      for(int j = i + 1; j < notes.size(); j++){
        innerNote = notes.get(j);
        if(outerNote.sameStrings(innerNote)){
          stringRepeatFrequency.set(j - i, stringRepeatFrequency.get(j - i) + 1);
          break;
        }
        if(j == notes.size() - 1){
          nonStringRepeatFrequency++;
        }
      }
    }
    for(int i = 0; i < stringRepeatRate.size(); i++){
      stringRepeatRate.set(i, (double) stringRepeatFrequency.get(i) / (double) numNotes);
    }
    
    // compute averageDistance
    Note previousNote = riff.getNotes().get(0), currentNote = null;
    for(int i = 1; i < numNotes; i++){
      currentNote = riff.getNotes().get(i);
      averageDistance += distanceCalculator.distance(previousNote, currentNote);
      previousNote = currentNote;
    }
    averageDistance /= (numNotes - 1);
    
    // distanceFromStart
    Note firstNote = notes.get(0);
    Note lastNote = notes.get(notes.size() - 1); 
    distanceFromStart = distanceCalculator.distance(firstNote, lastNote);
  
    // intermediateDistances
    previousNote = notes.get(0); // declared previously
    currentNote = null; // declared previously
    for(int i = 1; i < numNotes; i++){
      currentNote = riff.getNotes().get(i);
      intermediateDistances.add(distanceCalculator.distance(previousNote, currentNote));
      previousNote = currentNote;
    }
  }
  
  // returns a simplified riff where all non-single notes are averaged into single string-frets
  public ArrayList<StringFret> getDistanceRiff(){
    ArrayList<StringFret> distanceRiff = new ArrayList<StringFret>();
    
    ArrayList<Note> notes = riff.getNotes();
    double currentString = -1d, currentFret = -1d;
    for(int i = 0; i < notes.size(); i++){
      currentString = 0d;
      currentFret = 0d;
      for(StringFret sf: notes.get(i).getStringFrets()){
        currentString += sf.getString();
        currentFret += sf.getFret();
      }
      distanceRiff.add(new StringFret((int) Math.round(currentString/notes.get(i).getStringFrets().size()), (int) Math.round(currentFret/notes.get(i).getStringFrets().size())));
    }
    
    return distanceRiff;
  }
  
  public boolean hasAttribute(String type){
    for(String s: attributes){
      if(s.contains(type)){
        return true;
      }
    }
    return false;
  }
  public String getAttribute(String type){
    for(String s: attributes){
      if(s.contains(type)){
        return s;
      }
    }
    
    return null;
  }
  
  public Riff getRiff(){
    return riff;
  }
  
  public ArrayList<String> getAttributes(){
    return attributes;
  }
  
  public int getNumNotes(){
    return numNotes;
  }
  public void setNumNotes(int n){
    numNotes = n;
  }
  
  public int getNumStrings(){
    return numStrings;
  }
  public void setNumStrings(int n){
    numStrings = n;
  }
  
  public int getFretRange(){
    return fretRange;
  }
  public void setFretRange(int fr){
    fretRange = fr;
  }
  
  public int getStringRange(){
    return stringRange;
  }
  public void setStringRange(int sr){
    stringRange = sr;
  }
  
  public ArrayList<Integer> getNoteRepeatFrequency(){
    return noteRepeatFrequency;
  }
  public void setNoteRepeatFrequency(ArrayList<Integer> nrf){
    noteRepeatFrequency = nrf;
  }
  
  public ArrayList<Double> getNoteRepeatRate(){
    return noteRepeatRate;
  }
  
  public ArrayList<Integer> getStringRepeatFrequency(){
    return stringRepeatFrequency;
  }
  public void setStringRepeatFrequency(ArrayList<Integer> srf){
    stringRepeatFrequency = srf;
  }
  
  public ArrayList<Double> getStringRepeatRate(){
    return stringRepeatRate;
  }
  
  public double getAverageDistance(){
    return averageDistance;
  }
  public void setAverageDistance(double aD){
    averageDistance = aD;
  }
  
  public double getDistanceFromStart(){
    return distanceFromStart;
  }
  public void setDistanceFromStart(Double dfs){
    distanceFromStart = dfs;
  }
  
  public ArrayList<Double> getIntermediateDistances(){
    return intermediateDistances;
  }
  public void setIntermediateDistances(ArrayList<Double> id){
    intermediateDistances = id;
  }
  
  public String toString(){
    return "\n" +
      riff.getName() + 
      "\nAttribute: " + attributes +
      "\nNumber Notes: " + numNotes + 
      "\nNumber Strings: " + numStrings + 
      "\nNote Repeat Frequency: " + noteRepeatFrequency + " " + noteRepeatRate +
      "\n\tNon-Note Repeat Frequency: " + nonNoteRepeatFrequency +
      "\nString Repeat Frequency: " + stringRepeatFrequency + " " + stringRepeatRate + 
      "\n\tNon-String Repeat Frequency: " + nonStringRepeatFrequency +
      "\nAverage Distance: " + averageDistance +
      "\nIntermediate Distances: " + intermediateDistances +
      "\n";
  }
}