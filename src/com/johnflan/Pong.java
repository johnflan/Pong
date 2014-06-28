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

    public boolean BALL_DIRECTION_DOWN = true;
    public boolean BALL_DIRECTION_RIGHT = true;

    public int awayPenalty = 0;
    public int homePenalty = 0;

    int x = 0;
    int y = 0;

    private void moveBall() {

        if (x + BALL_SIZE > WINDOW_WIDTH){
            BALL_DIRECTION_RIGHT = false;
        }

        if (x < 0){
            BALL_DIRECTION_RIGHT = true;
        }

        if (BALL_DIRECTION_RIGHT){
            x = x + 1;
        } else {
            x = x - 1;
        }


        if(y > WINDOW_HEIGHT){
            BALL_DIRECTION_DOWN = false;
            homePenalty = 255;
        }

        if (y < 0){
            BALL_DIRECTION_DOWN = true;
            awayPenalty = 255;
        }

        if(BALL_DIRECTION_DOWN){
            y = y + 1;
        } else {
            y = y - 1;
        }

        if (homePenalty - 2 > 0){
            homePenalty -= 2;
        }

        if (awayPenalty - 2 > 0){
            awayPenalty -= 2;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //AWAY PENALTY
        g2d.setColor(new Color(255, 0, 0, awayPenalty));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT / 2);

        //HOME PENALTY
        g2d.setColor(new Color(255, 0, 0, homePenalty));
        g2d.fillRect(0, (WINDOW_HEIGHT / 2) + 1, WINDOW_WIDTH, (WINDOW_HEIGHT / 2) + 20);

        g2d.setColor(Color.WHITE);

        //AWAY PUCK
        g2d.fillRect(x - (PUCK_WIDTH / 2), 15, PUCK_WIDTH, PUCK_HEIGHT);

        //HOME PUCK
        g2d.fillRect(MouseInfo.getPointerInfo().getLocation().x - (PUCK_WIDTH / 2), WINDOW_HEIGHT - 15, PUCK_WIDTH, PUCK_HEIGHT);

        drawPitch(g);

        //BALL
        g2d.fillOval(x, y, BALL_SIZE, BALL_SIZE);
    }

    public static void main(String[] args) throws InterruptedException {

        JFrame frame = new JFrame("Pong");
        Pong game = new Pong();
        game.setBackground(Color.BLACK);

        frame.add(game);
        frame.setBackground(Color.RED);
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
            Thread.sleep(5);
        }
    }

    private void drawPitch(Graphics g) {

        int currentWidth = 10;
        while(WINDOW_WIDTH > currentWidth){
            g.fillRect(currentWidth, WINDOW_HEIGHT / 2, 15, 1);
            currentWidth += 30;
        }
    }
}
