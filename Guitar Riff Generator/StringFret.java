public class StringFret{
  //// a string-fret is a string number and a fret number
  //// written: 2:2
  
  private int string;
  private int fret;
  
  public StringFret(String rawStringFret){
    String[] pieces = rawStringFret.split(":");
    try{
      string = Integer.parseInt(pieces[0]);
      fret = Integer.parseInt(pieces[1]);
    }
    catch(ArrayIndexOutOfBoundsException aioobe){
      string = -1;
      fret = -1;
    }
  }
  public StringFret(int string, int fret){
    this.string = string;
    this.fret = fret;
  }
  
  public int getString(){
    return string;
  }
  public int getFret(){
    return fret;
  }
  
  public boolean sameString(StringFret other){
    return this.string == other.string;
  }
  
  public boolean equals(StringFret other){
    return this.string == other.string && this.fret == other.fret;
  }
  
  public String toString(){
    return string + ":" + fret;
  }
}