import javax.swing.JFrame;
import javax.swing.JPanel;

public class TabulatorDriver extends JFrame{
  
  JPanel tabulator;
  public TabulatorDriver(Riff riff){
    super();
    tabulator = new Tabulator(riff);
    this.setSize(1200, 800);
    this.add(tabulator);
    this.setVisible(true);
  }
  
  public static void main(String[] args){
    new TabulatorDriver(new Riff("[5:10][5:13][6:10][6:13][6:10][5:13][6:10][6:13][6:13][6:12][5:13][6:10]"));
  }
}