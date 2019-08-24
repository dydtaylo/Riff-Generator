import java.util.ArrayList;

public class Note{
  // a note is sequence of string-frets
  //// a string-fret is a string number and a fret number
  //// written: 2:2
  // written: [2:2] or [2:2,3:2,4:5] for multi-fret notes

  ArrayList<StringFret> stringFrets;
  
  public Note(String rawNote){
    String[] stringFretStrings = rawNote.replace("[", "").replace("]", "").split(",");
    stringFrets = new ArrayList<StringFret>();
    for(String sfs: stringFretStrings){
      stringFrets.add(new StringFret(sfs));
    }
  }
  public Note(ArrayList<StringFret> stringFrets){
    this.stringFrets = stringFrets;
  }
  public Note(){
    this(new ArrayList<StringFret>());
  }
  
  public void addStringFret(StringFret sf){
    stringFrets.add(sf);
  }
  public void addStringFret(int string, int fret){
    addStringFret(new StringFret(string, fret));
  }
  
  public ArrayList<StringFret> getStringFrets(){
    return stringFrets;
  }
  
  public ArrayList<Integer> getStrings(){
    ArrayList<Integer> strings = new ArrayList<Integer>();
    for(StringFret sf: stringFrets){
      strings.add(sf.getString());
    }
    return strings;
  }
  
  public boolean sameStrings(Note other){
    if(stringFrets.size() != other.getStringFrets().size()){
      return false;
    }
    for(int i = 0; i < stringFrets.size(); i++){
      if(!this.stringFrets.get(i).sameString(other.stringFrets.get(i))){
        return false;
      }
    }
    return true;
  }
  
  public boolean equals(Note other){
    if(stringFrets.size() != other.getStringFrets().size()){
      return false;
    }
    for(int i = 0; i < stringFrets.size(); i++){
      if(!this.stringFrets.get(i).equals(other.stringFrets.get(i))){
        return false;
      }
    }
    return true;
  }
  
  public String toString(){
    String s = "";
    for(StringFret sf: stringFrets){
      s += sf + ", ";
    }
    return "[" + s.substring(0, s.length() - 2) + "]";
  }
}