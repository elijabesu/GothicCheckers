package gui;

import brain.Coordinate;
import brain.Pieces;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BoardPanel extends JPanel {
    private final int size;
    private final int squareSize = 50;
    private BufferedImage yellow;
    private BufferedImage purple;
//    private BufferedImage[][] coordinates;
    private final Pieces[][] coordinates;

    public BoardPanel(int size) {
        this.size = size;
//        coordinates = new BufferedImage[size][size];
        coordinates = new Pieces[size][size];
        try {
            yellow = ImageIO.read(getClass().getResource("resources/yellow.png"));
            purple = ImageIO.read(getClass().getResource("resources/purple.png"));
        } catch (IOException error) {
            System.out.println(error);
        }
//        generateMen();
    }

    private void generateMen() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (col < 2) {
                    coordinates[row][col] = Pieces.WHITE;
//                    coordinates[row][col] = yellow;
                } else if (col > 5) {
                    coordinates[row][col] = Pieces.BLACK;
//                    coordinates[row][col] = purple;
                } else coordinates[row][col] = Pieces.EMPTY;
            }
        }
    }

    public int getBoardSize() {
        return size;
    }

    public void placeMan(Pieces man, Coordinate coordinate) {
        coordinates[coordinate.getRow()][coordinate.getColumn()] = man;
    }

    public void removeMan(Coordinate coordinate) {
        coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.EMPTY;
    }

    public Pieces getCoordinate(Coordinate coordinate) {
        return coordinates[coordinate.getRow()][coordinate.getColumn()];
    }

    // initial + calling repaint()
    public void paintComponent(Graphics graphics) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (col % 2 == row % 2) graphics.setColor(Color.DARK_GRAY);
                else graphics.setColor(Color.LIGHT_GRAY);

                int rowSize = row * squareSize;
                int colSize = col * squareSize;

                graphics.fillRect(rowSize, colSize, squareSize, squareSize);
                if (coordinates[row][col] == Pieces.WHITE) graphics.drawImage(
                        yellow, rowSize + 5, colSize + 5,40, 40, this
                );
                if (coordinates[row][col] == Pieces.BLACK) graphics.drawImage(
                        purple, rowSize + 5, colSize + 5,40, 40, this
                );
//                if (coordinates[row][col] != null)
//                    graphics.drawImage(coordinates[row][col], rowSize + 5, colSize + 5,40, 40, this);
            }
        }
    }
}
