package brickBreaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// KeyListener for detecting arrow key to control slider
// ActionListener for moving the ball
public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;   // when screen starts over, game shouldn't play by itself
    private int score = 0;          // score starts at 0

    private int totalBricks = 21;

    private Timer timer;            // how fast the ball moves
    private int delay = 8;          // the speed to timer

    private int playerX = 310;      // starting position of slider

    private int ballPosX = 120;     // starting position of ball
    private int ballPosY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;

    private MapGenerator map;           // the 1st map in (map.map.length)

    public Gameplay() {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // drawing map
        map.draw((Graphics2D) g);

        // borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // show score
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30);

        // the paddle
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);

        // the ball
        g.setColor(Color.yellow);
        g.fillOval(ballPosX, ballPosY, 20, 20);

        // for when you win
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You won! Your Score " + score, 260, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        // for when you lose
        if (ballPosY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over! Your Score: " + score, 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        // to get ball to move
        if (play) {

            // to get ball to bounce off paddle
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            }


            A:
            for (int i = 0; i < map.map.length; i++) {       // 1st map, line 28. 2nd map, is multi-dim array in MapGenerator
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        // create rectangle around brick
                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);

                        // create rectangle around ball
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

                        Rectangle brickRect = rect;

                        // to make ball actually hit bricks
                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;          // bricks will disappear when hit
                            score += 5;             // score will increase by 5

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }
                            break A;      // to take compiler out of outer loop; Line 76
                        }
                    }
                }
            }

            ballPosX += ballXdir;
            ballPosY += ballYdir;

            if (ballPosX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballPosY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballPosX > 670) {
                ballXdir = -ballXdir;
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        // to restart game, pressing the Enter button
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballPosX = 120;
                ballPosY = 350;
                ballXdir = -1;
                ballYdir = -2;
                playerX = 310;
                score = 0;
                totalBricks = 21;
                map = new MapGenerator(3, 7);   // want to regenerate map

                repaint();
            }
        }
    }

    public void moveRight() {
        play = true;
        playerX += 20;
    }

    public void moveLeft() {
        play = true;
        playerX -= 20;
    }


}
