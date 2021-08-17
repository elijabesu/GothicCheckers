package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BoardPanel extends JPanel {
    private final int size;
    private final int squareSize;
    private BufferedImage yellow;
    private BufferedImage purple;

    public BoardPanel(int size) {
        this.size = size;
        squareSize = 50;
        try {
            yellow = ImageIO.read(getClass().getResource("resources/yellow.png"));
            purple = ImageIO.read(getClass().getResource("resources/purple.png"));
        } catch (IOException error) {
            System.out.println(error);
        }
    }

    public void paintComponent(Graphics graphics) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (col % 2 == row % 2) graphics.setColor(Color.DARK_GRAY);
                else graphics.setColor(Color.LIGHT_GRAY);

                int rowSize = row * squareSize;
                int colSize = col * squareSize;

                graphics.fillRect(rowSize, colSize, squareSize, squareSize);
                if (col < 2) graphics.drawImage(yellow, rowSize + 5, colSize + 5, 40, 40,this);
                if (col > 5) graphics.drawImage(purple, rowSize + 5, colSize + 5, 40, 40,this);
            }
        }
    }
}
