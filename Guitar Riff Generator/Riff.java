import java.util.ArrayList;

import java.awt.Graphics;

public class Riff{
  // a riff is a sequence of notes
  //// a note is sequence of string-frets
  ////// a string-fret is a string number and a fret number
  ////// written: 2:2
  //// written: [2:2] or [2:2,3:2,4:5] for multi-fret notes
  // written: [2:2][1:3][2:2][2:3][3:0][1:3]
  
  ArrayList<Note> notes;
  String songName;
  String[] tuning;
  
  public Riff(String rawRiff, String sn){
    String[] noteStrings = rawRiff.split("]\\[");
    notes = new ArrayList<Note>();
    for(String ns: noteStrings){
      notes.add(new Note(ns));
    }
   
    songName = sn;
    tuning = new String[]{"E", "A", "D", "G", "B", "D"};
  }
  public Riff(String rawRiff){
    this(rawRiff, "title not found");
  }
  public Riff(ArrayList<Note> notes){
    this.notes = notes;
    songName = "title not found";
    tuning = new String[]{"E", "A", "D", "G", "B", "D"};
  }
  // only used by generator
  public Riff(){
    notes = new ArrayList<Note>();
    songName = "generated riff";
    tuning = new String[]{"E", "A", "D", "G", "B", "D"};
  }
  
  public void addNote(Note n){
    notes.add(n);
  }
  
  public String getName(){
    return songName;
  }
  
  public ArrayList<Note> getNotes(){
    return notes;
  }
  
  public String toString(){
    String s = songName + ": ";
    for(Note n: notes){
      s += n;
    }
    return s;
  }
  
  public void draw(Graphics g){
    final int DEFAULT_HORIZONTAL_SPACING = 40;
    final int DEFAULT_VERTICAL_SPACING = 20;
    g.drawString(songName, 50, 50);
    for(int i = 0; i < tuning.length; i++){
      g.drawString(tuning[i], 50, 220 - (i + 1) * DEFAULT_VERTICAL_SPACING);
      g.drawLine(75, 95 + i * DEFAULT_VERTICAL_SPACING, 1125, 95 + i * DEFAULT_VERTICAL_SPACING);
    }
    
    Note currentNote = null;
    ArrayList<StringFret> currentStringFrets = null;
    for(int i = 0; i < notes.size(); i++){
      currentNote = notes.get(i);
      currentStringFrets = currentNote.getStringFrets();
      for(StringFret sf: currentStringFrets){
        g.clearRect(75 + i * DEFAULT_HORIZONTAL_SPACING, 180 - (sf.getString() - 1) * DEFAULT_VERTICAL_SPACING, 20, 20);
        g.drawString(Integer.toString(sf.getFret()), 75 + i * DEFAULT_HORIZONTAL_SPACING, 220 - sf.getString() * DEFAULT_VERTICAL_SPACING);
      }
    }
  }
  
  public static void main(String[] args){
    String riffString = "";
    Riff riff = new Riff(riffString);
    System.out.println(riff);
  }
}