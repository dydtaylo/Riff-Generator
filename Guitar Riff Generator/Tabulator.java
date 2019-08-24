import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Tabulator extends JPanel{
  public static void main(String[] args){
    JFrame frame = new JFrame();
    JPanel tabulator = new Tabulator("Test Riffs/Layla.txt");
    frame.setSize(1200, 800);
    frame.add(tabulator);
    frame.setVisible(true);
  }
  
  Riff riff;
  public Tabulator(Riff riff){
    this.riff = riff;
  }
  public Tabulator(File file){
    Scanner scan = null;
    try{
      scan = new Scanner(file);
      riff = new Riff(scan.nextLine(), file.getName().replace(".txt", ""));
    }
    catch(FileNotFoundException fnfe){
      scan = new Scanner("FILE_NOT_FOUND");
      riff = new Riff();
    } 
  }
  public Tabulator(String filePath){
    this(new File(filePath));
  }
  
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, 2000, 2000);
    g.setColor(Color.BLACK);
    g.setFont(new Font("TimesRoman", Font.BOLD, 20));
    riff.draw(g);
  }
}