import javax.swing.JFrame;
// import java.awt.*;
/**
 * The javax.swing.JFrame class is a type of container which inherits the java.awt.Frame class. 
 * JFrame works like the main window where components like 
 * labels, buttons, textfields are added to create a GUI.
 * Unlike Frame, JFrame has the option to hide or close the 
 * window with the help of setDefaultCloseOperation(int) method.
 */
public class GameFrame extends JFrame {
    //constructor
    GameFrame() {
        // GamePanel panel = new GamePanel();
        // this.add(panel);
        this.add(new GamePanel()); // same as above but shorter
        this.setTitle("Snake");// window title
        // closes program when clicking x button
        // without it will close only window but program will be running
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        this.setResizable(false);
        this.pack(); 
        this.setVisible(true); // displays window app
        this.setLocationRelativeTo(null); // displays the window in the center of the screen
    }
}
