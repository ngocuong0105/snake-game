import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * The JPanel is a simplest container class. It provides space in which an application 
 * can attach any other component. It inherits the JComponents class. It doesn't have title bar.
 */

public class GamePanel extends JPanel implements Runnable {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 20; // measure size of objects, similar to pixel size
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int FPS = 20; // controls snake speed

    static final Color APPLE_COLOR = new Color(255,226,119); 
    static final Color SNAKE_BODY_COLOR = new Color(56,176,0); 
    static final Color SNAKE_HEAD_COLOR = Color.green; 
    static final Color GAME_OVER_COLOR = Color.red; 
    
    // holds x,y coordinates of all body parts of the snake
    final int x[] = new int[GAME_UNITS]; //(x[0],y[0]) is the head of the snake
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R'; // initialize snake to go right
    boolean running = false;
    Random random;
    Thread gameThread;

    // constructor
    GamePanel(){
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame() {
        running = true;
        random = new Random();
        newApple();
        gameThread = new Thread(this);
        gameThread.start();
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
            g.setColor(APPLE_COLOR);
            g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);

            for(int i = 0; i<bodyParts; i++) {
                // body
                if (i==0) {
                    g.setColor(SNAKE_HEAD_COLOR);
                    g.fillRect(x[i],y[i],UNIT_SIZE, UNIT_SIZE);
                }
                // head
                else {
                    g.setColor(SNAKE_BODY_COLOR);
                    // g.setColor(new Color(random.nextInt(255),180,0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(APPLE_COLOR);
            g.setFont(new Font("Ink Free", Font.ITALIC, 30));
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
        if ((x[0]<0) || (x[0] >= SCREEN_WIDTH)) {
            x[0] = (x[0] + SCREEN_WIDTH) % SCREEN_WIDTH;
        }
        if ((y[0]<0) || (y[0] >= SCREEN_HEIGHT)) {
            y[0] = (y[0] + SCREEN_HEIGHT) % SCREEN_HEIGHT;
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

        // Score txt
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.ITALIC, 40));
        FontMetrics metricsScore = getFontMetrics(g.getFont());
        String textScore = "Score:" + applesEaten;
        g.drawString(textScore,(SCREEN_WIDTH - metricsScore.stringWidth(textScore))/2 ,g.getFont().getSize());
        
        // Reset game text
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.ITALIC, 20));
        FontMetrics metricsReset = getFontMetrics(g.getFont());
        String textReset = "click ESC to reset";
        g.drawString(
            textReset,
            (SCREEN_WIDTH - metricsReset.stringWidth(textReset))/2,
            3*SCREEN_HEIGHT/4
            );
    }
    public void resetGame() {
        bodyParts = 6;
        running = true;
        direction = 'R';
        x[0] = 0;
        y[0] = 0;
        startGame();
    }

    // Method from Runnable interface
    public void run() {
        
        // simple game loop
        while (running) {
            long start = System.nanoTime();

            move(); // calls move method of panel
            checkApple();
            checkCollisions();
            repaint();

            long stop = System.nanoTime();
            long elapsed = stop - start;
            long fps = FPS; // frame rate is the frequency at which consecutive images are captured or displayed. 
            long targetTime = 1000 / fps; // 1000 miliseconds = 1 second
            long wait = targetTime - elapsed / 10000000; // convert to milliseconds
            if (wait > 0) {
                try {
                    Thread.sleep(wait); // measured in milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

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
            case KeyEvent.VK_ESCAPE:
                if (!running) {
                    resetGame();
                }
                break;
            }
        }
    }

}
