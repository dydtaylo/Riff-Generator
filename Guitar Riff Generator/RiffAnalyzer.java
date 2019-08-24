import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RiffAnalyzer{
  ArrayList<Riff> riffs;
  ArrayList<RiffStatistics> riffStatistics;
  Attributor attributor;
  
  double minNumNotes, maxNumNotes;
  double meanNumNotes;
  double stdDevNumNotes;
  
  double minNumStrings, maxNumStrings;
  double meanNumStrings;
  double stdDevNumStrings;
  
  double meanFretRange;
  double stdDevFretRange;
  
  double meanStringRange;
  double stdDevStringRange;
  
  ArrayList<Double> meanNoteRepeatRate;
  ArrayList<Double> meanStringRepeatRate;
  
  double smallestAvgDistance;
  double avgAvgDistance;
  double largestAvgDistance;
  
  
  public RiffAnalyzer(File sampleRiffs){
    riffs = new ArrayList<Riff>();
    riffStatistics = new ArrayList<RiffStatistics>();
    attributor = new Attributor(riffStatistics);
    ArrayList<File> riffFiles = new ArrayList<File>(Arrays.asList(sampleRiffs.listFiles()));
    Scanner riffReader = null;
    for(File riffFile: riffFiles){
      if(!riffFile.isDirectory()){
        try{
          System.out.println(riffFile.getName());
          riffReader = new Scanner(riffFile);
          riffs.add(new Riff(riffReader.nextLine(), riffFile.getName()));
          riffStatistics.add(new RiffStatistics(riffs.get(riffs.size() - 1), attributor));
        }
        catch(FileNotFoundException fnfe){
          System.out.println("couldn't find file: " + riffFile);
        }
      }
    }
    
    // System.out.println(riffs);
    conductStatistics();
  }
  public RiffAnalyzer(String filePath){
    this(new File(filePath));
  }
  
  public void conductStatistics(){
    for(RiffStatistics rf: riffStatistics){
      rf.conductStatistics();
    }
    
    for(RiffStatistics rf: riffStatistics){
      rf.determineAttributes();
      // System.out.println(rf);
    }
    
    // System.out.println(riffStatistics);
    conductMeansAndDeviations();
  }
  public void conductMeansAndDeviations(){
    // init all data
    minNumNotes = Integer.MAX_VALUE;
    maxNumNotes = Integer.MIN_VALUE;
    meanNumNotes = 0d;
    stdDevNumNotes = 0d;
    
    minNumStrings = Integer.MAX_VALUE;
    maxNumStrings = Integer.MIN_VALUE;
    meanNumStrings = 0d;
    stdDevNumStrings = 0d;
    
    meanFretRange = 0d;
    stdDevFretRange = 0d;
    
    meanStringRange = 0d;
    stdDevStringRange = 0d;
    
    meanNoteRepeatRate = new ArrayList<Double>();
    meanStringRepeatRate = new ArrayList<Double>();
    
    // meanNumNotes, meanNumStrings, fretRange, and stringRange
    int numNotes = -1, numStrings = -1;
    double fretRange = 0d, stringRange = 0d;
    for(RiffStatistics rs: riffStatistics){
      numNotes = rs.getNumNotes();
      numStrings = rs.getNumStrings();
      fretRange = rs.getFretRange();
      stringRange = rs.getStringRange();
      
      minNumNotes = Math.min(minNumNotes, numNotes);
      maxNumNotes = Math.max(maxNumNotes, numNotes);
      meanNumNotes += numNotes;
      
      minNumStrings = Math.min(minNumStrings, numStrings);
      maxNumStrings = Math.max(maxNumStrings, numStrings);
      meanNumStrings += numStrings;
      
      meanFretRange += fretRange;
      meanStringRange += stringRange;
    }
    meanNumNotes /= riffStatistics.size();
    meanNumStrings /= riffStatistics.size();
    meanFretRange /= riffStatistics.size();
    meanStringRange /= riffStatistics.size();
    
    // stdDevNumNotes, stdDevNumStrings, stdDevFretRange, and stdDevStringRange
    for(RiffStatistics rs: riffStatistics){
      stdDevNumNotes += Math.pow(meanNumNotes - rs.getNumNotes(), 2);
      stdDevNumStrings += Math.pow(meanNumStrings - rs.getNumStrings(), 2);
      stdDevFretRange += Math.pow(meanFretRange - rs.getFretRange(), 2);
      stdDevStringRange += Math.pow(meanStringRange - rs.getStringRange(), 2);
    }
    stdDevNumNotes = Math.sqrt(stdDevNumNotes)/Math.sqrt(riffStatistics.size());
    stdDevNumStrings = Math.sqrt(stdDevNumStrings)/Math.sqrt(riffStatistics.size());
    stdDevFretRange = Math.sqrt(stdDevFretRange)/Math.sqrt(riffStatistics.size());
    stdDevStringRange = Math.sqrt(stdDevStringRange)/Math.sqrt(riffStatistics.size());
    
    // System.out.println("Number Notes Mean and stdDev: " + meanNumNotes + ", " + stdDevNumNotes);
    // System.out.println("Number Strings Mean and stdDev: " + meanNumStrings + ", " + stdDevNumStrings);
    // System.out.println("Fret Range and stdDev: " + meanFretRange + ", " + stdDevFretRange);
    // System.out.println("String Range and stdDev: " + meanStringRange + ", " + stdDevStringRange);
    
    /* LIKELY TO BE REPLACE */
    // meanNoteRepeatRate and meanStringRepeatRate 
    ArrayList<Integer> currentNoteRepeatFrequency = null, currentStringRepeatFrequency = null;
    double totalNoteRepeats = 0, totalStringRepeats = 0;
    for(RiffStatistics rs: riffStatistics){
      currentNoteRepeatFrequency = rs.getNoteRepeatFrequency();
      currentStringRepeatFrequency = rs.getStringRepeatFrequency();
      for(int i = 0; i < currentNoteRepeatFrequency.size(); i++){
        while(i >= meanNoteRepeatRate.size()){
          meanNoteRepeatRate.add(0d);
        }
        meanNoteRepeatRate.set(i, meanNoteRepeatRate.get(i) + currentNoteRepeatFrequency.get(i));
        totalNoteRepeats += currentNoteRepeatFrequency.get(i);
      }
      for(int i = 0; i < currentStringRepeatFrequency.size(); i++){
        while(i >= meanStringRepeatRate.size()){
          meanStringRepeatRate.add(0d);
        }
        meanStringRepeatRate.set(i, meanStringRepeatRate.get(i) + currentStringRepeatFrequency.get(i));
        totalStringRepeats += currentStringRepeatFrequency.get(i);
      }
    }
    //// average out to turn frequencies into rates
    for(int i = 0; i < meanNoteRepeatRate.size(); i++){
      meanNoteRepeatRate.set(i, meanNoteRepeatRate.get(i)/totalNoteRepeats);
    }
    for(int i = 0; i < meanStringRepeatRate.size(); i++){
      meanStringRepeatRate.set(i, meanStringRepeatRate.get(i)/totalStringRepeats);
    }
    /* /LIKELY TO BE REPLACE */
    
    // System.out.println(meanNoteRepeatRate);
    // System.out.println(meanStringRepeatRate);
    
    // avgAvgDistance
    smallestAvgDistance = Double.MAX_VALUE;
    avgAvgDistance = 0d;
    largestAvgDistance = Double.MIN_VALUE;
    for(RiffStatistics rs: riffStatistics){
      smallestAvgDistance = Math.min(smallestAvgDistance, rs.getAverageDistance());
      avgAvgDistance += rs.getAverageDistance();
      largestAvgDistance = Math.max(largestAvgDistance, rs.getAverageDistance());
    }
    avgAvgDistance /= riffStatistics.size();
    
    // System.out.println(avgAvgDistance);
  }
  
  public static void main(String[] args){
    File sampleRiffs;
    try{
      sampleRiffs = new File(args[0]);
    }
    catch(ArrayIndexOutOfBoundsException ex){
      sampleRiffs = new File("Test Riffs");
    }
    
    new RiffAnalyzer(sampleRiffs);
  }
}