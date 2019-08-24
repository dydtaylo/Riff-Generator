import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class RiffGenerator{
  RiffAnalyzer analyzer;
  NoteDistanceCalculator distanceCalculator;
  PrintWriter output;
  Random gen;
  
  public RiffGenerator(File sampleRiffs){
    analyzer = new RiffAnalyzer(sampleRiffs);
    distanceCalculator = new NoteDistanceCalculator();
    try{
      new File(sampleRiffs.getName() + "\\generated riffs").mkdirs();
      output = new PrintWriter(sampleRiffs.getName() + "\\generated riffs\\output.txt");
    }
    catch(FileNotFoundException fnfe){
      System.out.println("Something went wrong");
    }
    
    gen = new Random();
    generateAcceptableRiff();
  }
  public RiffGenerator(String filePath){
    this(new File(filePath));
  }
  
  // prints to "../generated riffs/output.txt"
  public void generateAcceptableRiff(){
    Riff generatedRiff = null;
    for(int i = 0; i < 10; i++){
      generatedRiff = null;
      while(!conformsToSampleRiffs(generatedRiff)){
        generatedRiff = generateRiff();
      }
      output.print(generatedRiff);
      output.println();
    }
    output.close();
    // new TabulatorDriver(generatedRiff);
  }
  
  public boolean conformsToSampleRiffs(Riff riff){
    if(riff == null){
      return false;
    }
    
    RiffStatistics riffStats = new RiffStatistics(riff, analyzer.attributor);
    riffStats.conductStatistics();
    riffStats.determineAttributes();
    
    // has an acceptable fret range
    if(Math.abs(riffStats.getFretRange() - analyzer.meanFretRange) > analyzer.stdDevFretRange){
      return false;
    }
    
    // has an acceptable string range
    if(Math.abs(riffStats.getStringRange() - analyzer.meanStringRange) > analyzer.stdDevStringRange){
      return false;
    }
    
    // has same attributes as a known good riff
    for(RiffStatistics rs: analyzer.riffStatistics){
      if(analyzer.attributor.sameAttributes(riffStats, rs)){
        return true;
      }
    }
    return false;
  }
  
  public Riff generateRiff(){
    Riff generatedRiff = new Riff();
    ArrayList<Note> candidates = null;
    long numNotes = Math.round(analyzer.meanNumNotes + (gen.nextInt(2) - 1)*analyzer.stdDevNumNotes);
    for(int i = 0; i < numNotes; i++){
      // generate next note
      candidates = new ArrayList<Note>();
      if(generatedRiff.getNotes().size() == 0){
        // set to only start on frets 0 - 12
        candidates.add(new Note(new ArrayList<StringFret>(Arrays.asList(new StringFret(gen.nextInt(6) + 1, gen.nextInt(12) + 1)))));
      }
      else{
        Note lastNote = generatedRiff.getNotes().get(i - 1);
        ArrayList<StringFret> lastStringFrets = lastNote.getStringFrets();
        int lastString = 0, lastFret = 0;
        for(StringFret sf: lastStringFrets){
          lastString += sf.getString();
          lastFret += sf.getFret();
        }
        lastString = Math.round(lastString/lastStringFrets.size());
        lastFret = Math.round(lastFret/lastStringFrets.size());
        
        int currentString = Integer.MAX_VALUE;
        int currentFret = Integer.MAX_VALUE;
        double distance = Integer.MAX_VALUE;
        while(!(analyzer.smallestAvgDistance < distance && distance < analyzer.largestAvgDistance)){
          currentString = gen.nextInt(6) + 1;
          currentFret = gen.nextInt(15) + 1;
          Note currentNote = new Note();
          currentNote.addStringFret(currentString, currentFret);
          distance = distanceCalculator.distance(lastNote, currentNote);
        }
        candidates.add(new Note(new ArrayList<StringFret>(Arrays.asList(new StringFret(currentString, currentFret)))));
      }
      
      generatedRiff.addNote(candidates.get(gen.nextInt(candidates.size())));
    }
    return generatedRiff;
  }
  
  public static void main(String[] args){
    File sampleRiffs = null;
    try{
      sampleRiffs = new File(args[0]);
    }
    catch(ArrayIndexOutOfBoundsException aioobe){
      sampleRiffs = new File("Test Riffs");
    }
    
    new RiffGenerator(sampleRiffs);
  }
    
}