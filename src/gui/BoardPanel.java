package gui;

import shared.*;

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
    private final Pieces[][] coordinates;

    public BoardPanel(int size) {
        this.size = size;
        coordinates = new Pieces[size][size];
        try {
            yellow = ImageIO.read(getClass().getResource("resources/yellow.png"));
            purple = ImageIO.read(getClass().getResource("resources/purple.png"));
        } catch (IOException error) {
            System.out.println(error);
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

    public boolean isOccupied(Coordinate coordinate) {
        return (coordinates[coordinate.getRow()][coordinate.getColumn()] != Pieces.EMPTY);
    }

    public void moveOrJump(Move move) {
        if (move.isJump()) jump(Utils.convertMoveIntoJump(move));
        else move(move);
    }

    public void undoMoveOrJump(Move move) {
        if (move.isJump()) undoJump(Utils.convertMoveIntoJump(move));
        else undoMove(move);
    }

    private void move(Move move) {
        removeMan(move.getOriginal());
        placeMan(move.getMan(), move.getNew());
    }

    private void undoMove(Move move) {
        removeMan(move.getNew());
        placeMan(move.getMan(), move.getOriginal());
    }

    private void jump(Jump jump) {
        removeMan(jump.getOriginal());
        removeMan(jump.getJumped());
        placeMan(jump.getMan(), jump.getNew());
    }

    private void undoJump(Jump jump) {
        placeMan(jump.getMan(), jump.getOriginal());
        placeMan(jump.getJumpedMan(), jump.getJumped());
        removeMan(jump.getNew());
    }

    public void promote(Pieces man, Coordinate coordinate) {
        if (man.isWhite()) coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.WHITE_KING;
        else coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.BLACK_KING;
    }

    public int getNumberOf(boolean white, boolean king) {
        int result = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Pieces currentPiece = getCoordinate(new Coordinate(row, column));
                if (white == currentPiece.isWhite() && king == currentPiece.isKing()) result++;
            }
        }
        return result;
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
                if (coordinates[row][col] == Pieces.WHITE) {
                    graphics.drawImage(
                            yellow, rowSize + 5, colSize + 5, 40, 40, this
                    );
                } else if (coordinates[row][col] == Pieces.BLACK) {
                    graphics.drawImage(
                            purple, rowSize + 5, colSize + 5, 40, 40, this
                    );
                } else if (coordinates[row][col] == Pieces.WHITE_KING) {
                    // TODO white king drawing
                } else if (coordinates[row][col] == Pieces.BLACK_KING) {
                    // TODO black king drawing
                }
            }
        }
    }
}
