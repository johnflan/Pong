package com.johnflan;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Pong extends JPanel {

    public static final int WINDOW_HEIGHT = 600;
    public static final int WINDOW_WIDTH = 400;

    public static final int PUCK_HEIGHT = 15;
    public static final int PUCK_WIDTH = 100;

    public static final int BALL_SIZE = 15;
    public static final int PIXEL_MOVE_PER_FRAME = 2;

    public boolean BALL_DIRECTION_DOWN = true;
    public boolean BALL_DIRECTION_RIGHT = true;

    public int awayPenalty = 0;
    public int homePenalty = 0;

    int ballX = 0;
    int ballY = WINDOW_HEIGHT / 2;

    int homePuckX = 0;
    int awayPuckX = 0;

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
            //15 ms is close to 60fps
            Thread.sleep(10);
        }
    }

    private void moveBall() {

        //Determine ball horizontal direction
        if (ballX + BALL_SIZE > WINDOW_WIDTH){
            BALL_DIRECTION_RIGHT = false;
        }

        if (ballX < 0){
            BALL_DIRECTION_RIGHT = true;
        }

        if (BALL_DIRECTION_RIGHT){
            ballX = ballX + PIXEL_MOVE_PER_FRAME;
        } else {
            ballX = ballX - PIXEL_MOVE_PER_FRAME;
        }


        //Determine away ball puck bounce
        if(ballY == 28 && BALL_DIRECTION_DOWN == false && (awayPuckX - 50) < ballX  && (awayPuckX + 50) > ballX ){
            BALL_DIRECTION_DOWN = !BALL_DIRECTION_DOWN;
        }

        //Determine home ball puck bounce
        if(ballY == WINDOW_HEIGHT - 30 && BALL_DIRECTION_DOWN == true && (homePuckX - 50 - (PUCK_WIDTH / 2)) <= ballX  && (homePuckX + 50) > ballX ){
            BALL_DIRECTION_DOWN = !BALL_DIRECTION_DOWN;
        }

        //Determine ball vertical direction
        if(ballY > WINDOW_HEIGHT){
            BALL_DIRECTION_DOWN = false;
            homePenalty = 255;
        }

        if (ballY < 0){
            BALL_DIRECTION_DOWN = true;
            awayPenalty = 255;
        }

        if(BALL_DIRECTION_DOWN){
            ballY = ballY + PIXEL_MOVE_PER_FRAME;
        } else {
            ballY = ballY - PIXEL_MOVE_PER_FRAME;
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

        //AWAY PUCK
        g2d.fillRect(awayPuckX - (PUCK_WIDTH / 2), 15, PUCK_WIDTH, PUCK_HEIGHT);

        //HOME PUCK
        g2d.fillRect(homePuckX - (PUCK_WIDTH / 2), WINDOW_HEIGHT - 15, PUCK_WIDTH, PUCK_HEIGHT);

        //BALL
        g2d.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        drawInterlacePattern(g2d);
        drawPitchHalfwayLine(g2d);

        //AWAY PENALTY
        if(awayPenalty > 0){
            drawAwayPenalty(g2d);
        }

        //HOME PENALTY
        if(homePenalty > 0){
            drawHomePenalty(g2d);
        }
    }

    private void drawHomePenalty(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 0, homePenalty));
        g2d.fillRect(0, (WINDOW_HEIGHT / 2) + PIXEL_MOVE_PER_FRAME, WINDOW_WIDTH, (WINDOW_HEIGHT / 2) + 20);
    }

    private void drawAwayPenalty(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 0, awayPenalty));
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
            g2d.fillRect(currentWidth, WINDOW_HEIGHT / 2, 15, PIXEL_MOVE_PER_FRAME);
            currentWidth += 30;
        }
    }
}
