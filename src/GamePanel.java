import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.Timer;

/**
 * The JPanel is a simplest container class. It provides space in which an application 
 * can attach any other component. It inherits the JComponents class. It doesn't have title bar.
 */

public class GamePanel extends JPanel implements ActionListener{

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25; // measure size of objects, similar to pixel size
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 74; // snake speed
    
    // holds x,y coordinates of all body parts of the snake
    final int x[] = new int[GAME_UNITS]; //(x[0],y[0]) is the head of the snake
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R'; // initialize snake to go right
    boolean running = false;
    Timer timer;
    Random random;
    
    // constructor
    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);//????
        this.addKeyListener(new MyKeyAdapter());
        startGame();

    }

    public void startGame() {
        newApple();
        running = true;
        /**
         * A javax.swing.Timer object calls an action listener at regular intervals or only once. 
         * For example, it can be used to show frames of an animation many times per second, 
         * repaint a clock every second, or check a server every hour. 
         * Fires one or more ActionEvents (defined inactionPerfomed method) at specified intervals
         */
        //
        timer = new Timer(DELAY,this); //milliseconds, ActionListener
		timer.start();

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g); // set color background in effect
        draw(g); // draw grid in the window
    }

    public void draw(Graphics g){

        if(running) {
            /*
            for (int i = 0; i<SCREEN_HEIGHT/UNIT_SIZE; i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE , SCREEN_HEIGHT); // vertical lines in the grid
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE); // horizontal lines in the grid
            }
            */
            // draw apple
            g.setColor(Color.red);
            g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);

            for(int i = 0; i<bodyParts; i++) {
                // body
                if (i==0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i],y[i],UNIT_SIZE, UNIT_SIZE);
                }
                // head
                else {
                    g.setColor(new Color(45,180,0));
                    g.setColor(new Color(random.nextInt(255),180,0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.ITALIC, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String text = "Score:" + applesEaten;
            g.drawString(text,(SCREEN_WIDTH - metrics.stringWidth(text))/2 ,g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }
    
    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i>0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1]; //shifts to the left of the array as index 0 is the head of the snake
        }
        /**
         * The Java switch statement executes one statement from multiple conditions
         */
        switch(direction) {
        case 'U':
            y[0] = y[0] - UNIT_SIZE;
            break;
        case 'D':
            y[0] = y[0] + UNIT_SIZE;
            break;
        case 'L':
            x[0] = x[0] - UNIT_SIZE;
            break;
        case 'R':
            x[0] = x[0] + UNIT_SIZE;
            break;
        }
    }

    // check if apple is eaten
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // checks if head touches body
        for(int i = bodyParts; i>0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }

        // check head touches borders 
        if ((x[0]<0) || (x[0] > SCREEN_WIDTH)) {
            x[0] = (x[0]+SCREEN_WIDTH)%SCREEN_WIDTH;
        }
        if ((y[0]<0) || (y[0] > SCREEN_HEIGHT)) {
            y[0] = (y[0]+SCREEN_HEIGHT)%SCREEN_HEIGHT;
        }
        if(!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Game Over Text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.ITALIC, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String text = "Game Over";
        g.drawString(
            text,
            (SCREEN_WIDTH - metrics.stringWidth(text))/2,
            SCREEN_HEIGHT/2
            );

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.ITALIC, 40));
        FontMetrics metricsScore = getFontMetrics(g.getFont());
        String textScore = "Score:" + applesEaten;
        g.drawString(textScore,(SCREEN_WIDTH - metricsScore.stringWidth(textScore))/2 ,g.getFont().getSize());
    
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // method of AtionListener Interface
        // tasks to be performed by action listener
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint(); // recolor cells (snake is moving)
    }
    
    // subclass which controls snake movement
    public class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if(direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if(direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_UP:
                if(direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if(direction != 'U') {
                    direction = 'D';
                }
                break;
            }
        }
    }

}
