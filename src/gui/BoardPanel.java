package gui;

import gui.brain.Game;
import shared.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class BoardPanel extends JLayeredPane implements MouseListener, MouseMotionListener {
    private final int size;
    private final Game game;
    private final Player[] players;
//    private final Pieces[][] coordinates;
    private final JPanel boardPanel;

    private Player currentPlayer;
    private JLabel currentPiece;
    private Point currentPieceOriginalPosition;
    private int x;
    private int y;

    private ImageIcon white;
    private ImageIcon whiteKing;
    private ImageIcon black;
    private ImageIcon blackKing;

    public BoardPanel(int size, Game game, Player[] players) {
        this.game = game;
        this.size = size;
        this.players = players;
//        coordinates = new Pieces[size][size];

        prepareImages();
        game.setPieces(new ImageIcon[] {white, whiteKing, black, blackKing});

        Dimension boardSize = new Dimension(400, 400);
        setPreferredSize(boardSize);
        addMouseListener(this);
        addMouseMotionListener(this);

        boardPanel = new JPanel();
        this.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        boardPanel.setLayout(new GridLayout(size, size));
        boardPanel.setPreferredSize(boardSize);
        boardPanel.setBounds(0, 0, boardSize.width, boardSize.height);

        generateTiles();
        placeMen();

        currentPlayer = players[0];
    }

    private void prepareImages() {
        Image resizedWhite = new ImageIcon(getClass().getResource("resources/white.png"))
                .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        Image resizedBlack = new ImageIcon(getClass().getResource("resources/black.png"))
                .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        Image resizedWhiteKing = new ImageIcon(getClass().getResource("resources/whiteKing.png"))
                .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        Image resizedBlackKing = new ImageIcon(getClass().getResource("resources/blackKing.png"))
                .getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);

        white = new ImageIcon(resizedWhite);
        black = new ImageIcon(resizedBlack);
        whiteKing = new ImageIcon(resizedWhiteKing);
        blackKing = new ImageIcon(resizedBlackKing);
    }

    private void generateTiles() {
        for (int i = 0; i < size * size; i++) {
            JPanel tile = new JPanel(new BorderLayout());
            boardPanel.add(tile);

            int row = (i / size) % 2;
//            int col = ((i / size) % 2) * 10;
//            if (col > 0) col -= 10;
            if (row == 0) tile.setBackground(i % 2 == 0 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
            else tile.setBackground(i % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        }
    }

    private void placeMen() {
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                JPanel tile = (JPanel) boardPanel.getComponent(row * 8 + column);
                JLabel piece = null;
                if (row < 2) {
//                    coordinates[row][column] = Pieces.WHITE;
                    piece = new JLabel(black);
                } else if (row > 5) {
//                    coordinates[row][column] = Pieces.BLACK;
                    piece = new JLabel(white);
                }
//                } else coordinates[row][column] = Pieces.EMPTY;
                if (piece != null) tile.add(piece);
            }
        }
    }

    public int getBoardSize() {
        return size;
    }

    private void switchPlayers() {
        if (currentPlayer == players[0]) currentPlayer = players[1];
        else currentPlayer = players[0];
    }

//    public void placeMan(Pieces man, Coordinate coordinate) {
//        coordinates[coordinate.getRow()][coordinate.getColumn()] = man;
//        }
//    }
//
//    public void removeMan(Coordinate coordinate) {
//        coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.EMPTY;
//    }
//
//    public Pieces getCoordinate(Coordinate coordinate) {
//        return coordinates[coordinate.getRow()][coordinate.getColumn()];
//    }
//
//    public boolean isOccupied(Coordinate coordinate) {
//        return (coordinates[coordinate.getRow()][coordinate.getColumn()] != Pieces.EMPTY);
//    }
//
//    public void moveOrJump(Move move) {
//        if (move.isJump()) jump(Utils.convertMoveIntoJump(move));
//        else move(move);
//    }
//
//    public void undoMoveOrJump(Move move) {
//        if (move.isJump()) undoJump(Utils.convertMoveIntoJump(move));
//        else undoMove(move);
//    }
//
//    private void move(Move move) {
//        removeMan(move.getOriginal());
//        placeMan(move.getMan(), move.getNew());
//    }
//
//    private void undoMove(Move move) {
//        removeMan(move.getNew());
//        placeMan(move.getMan(), move.getOriginal());
//    }
//
//    private void jump(Jump jump) {
//        removeMan(jump.getOriginal());
//        removeMan(jump.getJumped());
//        placeMan(jump.getMan(), jump.getNew());
//    }
//
//    private void undoJump(Jump jump) {
//        placeMan(jump.getMan(), jump.getOriginal());
//        placeMan(jump.getJumpedMan(), jump.getJumped());
//        removeMan(jump.getNew());
//    }
//
//    public void promote(Pieces man, Coordinate coordinate) {
//        if (man.isWhite()) coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.WHITE_KING;
//        else coordinates[coordinate.getRow()][coordinate.getColumn()] = Pieces.BLACK_KING;
//    }
//
//    public int getNumberOf(boolean white, boolean king) {
//        int result = 0;
//        for (int row = 0; row < size; row++) {
//            for (int column = 0; column < size; column++) {
//                Pieces currentPiece = getCoordinate(new Coordinate(row, column));
//                if (white == currentPiece.isWhite() && king == currentPiece.isKing()) result++;
//            }
//        }
//        return result;
//    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentPiece = null;

        // get the component where mouse was pressed
        Component componentAt = boardPanel.findComponentAt(e.getX(), e.getY());

        // we only need JLabels
        if (componentAt instanceof JPanel) return;
        if (!game.canPlayerMoveThis(
                (ImageIcon) ((JLabel) componentAt).getIcon(),
                currentPlayer)
        ) return;

        Point parentLocation = componentAt.getParent().getLocation();
        x = parentLocation.x - e.getX();
        y = parentLocation.y - e.getY();

        // from Component object to JLabel
        currentPiece = (JLabel) componentAt;
        currentPieceOriginalPosition = parentLocation;
        currentPiece.setLocation(e.getX() + x, e.getY() + y);
        currentPiece.setSize(currentPiece.getWidth(), currentPiece.getHeight());

        // allow JLabel to be dragged
        this.add(currentPiece, JLayeredPane.DRAG_LAYER);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentPiece == null) return;

        currentPiece.setVisible(false);
        Component componentAt = boardPanel.findComponentAt(e.getX(), e.getY());

        Container parent;
        boolean valid = true;
        if (componentAt instanceof JLabel) {
            if (!game.checkValidityAndMove(
                    (ImageIcon) ((JLabel) componentAt).getIcon(),
                    (ImageIcon) currentPiece.getIcon(),
                    currentPieceOriginalPosition,
                    componentAt.getLocation(),
                    currentPlayer
                    )) {
                valid = false;
                parent = (Container) boardPanel.findComponentAt(
                        currentPieceOriginalPosition.x, currentPieceOriginalPosition.y
                );
            } else {
                parent = componentAt.getParent();
                parent.remove(0);
            }
        } else {
            parent = (Container) componentAt;
        }

        parent.add(currentPiece);

        currentPiece.setVisible(true);
        if (valid) switchPlayers();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentPiece == null) return;
        currentPiece.setLocation(e.getX() + x, e.getY() + y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
