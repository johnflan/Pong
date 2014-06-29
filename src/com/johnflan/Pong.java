package com.johnflan;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Pong extends JPanel {

    private AudioManager audioManager;

    //CONSTANTS
    public static final int WINDOW_HEIGHT = 600;
    public static final int WINDOW_WIDTH = 400;
    public static final int PUCK_HEIGHT = 15;
    public static final int PUCK_WIDTH = 100;
    public static final int BALL_SIZE = 15;

    //GAME STATE
    public static final int GAME_SPEED = 4;

    public boolean BALL_DIRECTION_DOWN = true;
    public boolean BALL_DIRECTION_RIGHT = true;

    public int awayPenalty = 0;
    public int homePenalty = 0;

    int ballX = 0;
    int ballY = WINDOW_HEIGHT / 6;

    int homePuckX = 0;
    int awayPuckX = 0;

    int homeScore = 0;
    int awayScore = 0;

    static boolean initialFrame = true;

    public static void main(String[] args) throws InterruptedException {

        JFrame frame = new JFrame("Pong");
        Pong game = new Pong();
        game.setBackground(Color.BLACK);

        frame.add(game);
        frame.setBackground(Color.BLACK);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT + 45);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        frame.setCursor(blankCursor);

        while (true) {

            game.moveBall();
            game.repaint();

            if (initialFrame){
                Thread.sleep(3000);
                initialFrame = false;
            } else {
                //15 ms is close to 60fps
                Thread.sleep(10);
            }
        }
    }

    public Pong(){
        audioManager = new AudioManager();
    }

    private void moveBall() {

        //Determine ball horizontal direction
        if (ballX + BALL_SIZE > WINDOW_WIDTH){
            BALL_DIRECTION_RIGHT = false;
            audioManager.playWallHit();
        }

        if (ballX < 0){
            BALL_DIRECTION_RIGHT = true;
            audioManager.playWallHit();
        }

        if (BALL_DIRECTION_RIGHT){
            ballX = ballX + GAME_SPEED;
        } else {
            ballX = ballX - GAME_SPEED;
        }

        //Determine away ball puck bounce
        if((ballY <= 30 &&  ballY >= 15) && BALL_DIRECTION_DOWN == false &&
                (awayPuckX - 50) < ballX  && (awayPuckX + 50) > ballX ){
            BALL_DIRECTION_DOWN = !BALL_DIRECTION_DOWN;
            audioManager.playPaddleHit();
        }

        //Determine home ball puck bounce
        if((ballY >= WINDOW_HEIGHT - 30 &&  ballY <= WINDOW_HEIGHT - 15) && BALL_DIRECTION_DOWN == true &&
                (homePuckX - 60 ) <= ballX  && (homePuckX + 50) > ballX ){
            BALL_DIRECTION_DOWN = !BALL_DIRECTION_DOWN;
            audioManager.playPaddleHit();
        }

        //Determine ball vertical direction
        if(ballY > WINDOW_HEIGHT){
            BALL_DIRECTION_DOWN = false;
            homePenalty = 255;
            awayScore += 1;
            audioManager.playScore();
        }

        if (ballY < 0){
            BALL_DIRECTION_DOWN = true;
            awayPenalty = 255;
            homeScore += 1;
            audioManager.playScore();
        }

        if(BALL_DIRECTION_DOWN){
            ballY = ballY + GAME_SPEED;
        } else {
            ballY = ballY - GAME_SPEED;
        }

        //Determine penalty
        if (homePenalty - 5 > 0){
            homePenalty -= 5;
        }

        if (awayPenalty - 5 > 0){
            awayPenalty -= 5;
        }

        //update puck position
        awayPuckX = ballX;
        homePuckX = MouseInfo.getPointerInfo().getLocation().x;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawFrame(g2d);
    }

    private void drawFrame(Graphics2D g2d) {

        g2d.setColor(Color.WHITE);

        drawPucks(g2d);
        drawBall(g2d);
        drawScores(g2d);
        drawInterlacePattern(g2d);
        drawPitchHalfwayLine(g2d);
        drawPenalyIfExists(g2d);
    }

    private void drawPenalyIfExists(Graphics2D g2d) {
        //AWAY PENALTY
        if(awayPenalty > 0){
            drawAwayPenalty(g2d);
        }

        //HOME PENALTY
        if(homePenalty > 0){
            drawHomePenalty(g2d);
        }
    }

    private void drawBall(Graphics2D g2d) {
        //BALL
        g2d.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
    }

    private void drawPucks(Graphics2D g2d) {
        //AWAY PUCK
        g2d.fillRect(awayPuckX - (PUCK_WIDTH / 2), 15, PUCK_WIDTH, PUCK_HEIGHT);

        //HOME PUCK
        g2d.fillRect(homePuckX - (PUCK_WIDTH / 2), WINDOW_HEIGHT - 15, PUCK_WIDTH, PUCK_HEIGHT);
    }

    private void drawScores(Graphics2D g2d) {
        Font exFont = new Font(Font.MONOSPACED ,Font.PLAIN,20);

        g2d.setFont(exFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(awayScore), 12, 290);
        g2d.drawString(String.valueOf(homeScore), 12, 325);
    }

    private void drawHomePenalty(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, homePenalty));
        g2d.fillRect(0, (WINDOW_HEIGHT / 2) + 2, WINDOW_WIDTH, (WINDOW_HEIGHT / 2) + 20);
    }

    private void drawAwayPenalty(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, awayPenalty));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT / 2);
    }

    private void drawInterlacePattern(Graphics2D g2d) {
        int interlaceLines = 0;
        g2d.setColor(new Color(0, 0, 0, 70));
        while (interlaceLines < WINDOW_HEIGHT){
            interlaceLines += 2;
            g2d.fillRect(0, interlaceLines, WINDOW_WIDTH, 1);
        }
    }

    private void drawPitchHalfwayLine(Graphics2D g2d) {
        int currentWidth = 12;
        g2d.setColor(Color.WHITE);
        while(WINDOW_WIDTH > currentWidth){
            g2d.fillRect(currentWidth, WINDOW_HEIGHT / 2, 15, 2);
            currentWidth += 30;
        }
    }
}
