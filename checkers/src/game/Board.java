package game;

import exception.FieldTakenException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Board extends JComponent {


    static final int NUMBER_OF_FIELDS_IN_ROW = 8;
    static final int FIELD_SIZE = (int) (Checker.getSize() * 1.25);
    static final int BOARD_SIZE = NUMBER_OF_FIELDS_IN_ROW * FIELD_SIZE;

    GameController gc = new GameController(this);

    BoardChecker[][] boardCheckers = new BoardChecker[NUMBER_OF_FIELDS_IN_ROW][NUMBER_OF_FIELDS_IN_ROW];

    private Dimension boardDimension = new Dimension(BOARD_SIZE, BOARD_SIZE);

    Board() {
        initializeStateArray();
        fillWithCheckers();
        addMouseListener(new MoveCheckerListener(gc));
    }

    private void initializeStateArray() {
        for (int i = 0; i < NUMBER_OF_FIELDS_IN_ROW; i++) {
            for (int j = 0; j < NUMBER_OF_FIELDS_IN_ROW; j++) {
                boardCheckers[i][j] = null;
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        this.paintBoard(g);

        for (int i = 0; i < NUMBER_OF_FIELDS_IN_ROW; i++) {
            for (int j = 0; j < NUMBER_OF_FIELDS_IN_ROW; j++) {
                if (boardCheckers[i][j] != null) {
                    boardCheckers[i][j].draw(g);
                }
            }
        }
    }

    private void paintBoard(Graphics g) {
        for (int row = 0; row < NUMBER_OF_FIELDS_IN_ROW; row++) {
            g.setColor(((row & 1) != 0) ? Color.BLACK : Color.WHITE);
            for (int col = 0; col < NUMBER_OF_FIELDS_IN_ROW; col++) {
                g.fillRect(col * FIELD_SIZE, row * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
                g.setColor((g.getColor() == Color.BLACK) ? Color.WHITE : Color.BLACK);
            }
        }
    }

    public void paintPossibleMovesAncChecker(BoardChecker ch) {


        Graphics2D g = (Graphics2D) this.getGraphics();
        g.setColor(Color.YELLOW);
        g.fillRect(ch.checker.getX() * FIELD_SIZE, ch.checker.getY() * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
        ch.draw(this.getGraphics());
        g.setStroke(new BasicStroke(3));
        for(Jump jump: gc.getPossibleMoves(ch)) {
            if (jump.nextJump == null) {
                g.setColor(Color.GREEN);
            } else {
                Jump tempJump = jump;
                while(tempJump.nextJump != null) {
                    if(tempJump.nextJump.nextJump == null){
                        g.setColor(Color.GREEN);
                    } else {
                        g.setColor(Color.YELLOW);
                    }
                    g.fillRect(tempJump.nextJump.move.x * FIELD_SIZE, tempJump.nextJump.move.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);
                    tempJump = tempJump.nextJump;
                }
                g.setColor(Color.YELLOW);
            }
            g.fillRect(jump.move.x * FIELD_SIZE, jump.move.y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);

        }
    }

    @Override
    public Dimension getPreferredSize() {
        return boardDimension;
    }


    private void fillWithCheckers() {
        Color color = Color.RED;
        Color color1 = Color.BLACK;
//       Do testowania na 8x8
        this.add(new Checker(color, 0, 0));
        this.add(new Checker(color, 2, 0));
        this.add(new Checker(color, 4, 0));
        this.add(new Checker(color, 6, 0));
        this.add(new Checker(color, 1, 1));
        this.add(new Checker(color, 3, 1));
        this.add(new Checker(color, 5, 1));
        this.add(new Checker(color, 7, 1));
        this.add(new Checker(color, 0, 2));
        this.add(new Checker(color, 2, 2));
        this.add(new Checker(color, 4, 2));
        this.add(new Checker(color, 6, 2));
        this.add(new Checker(color1, 1, NUMBER_OF_FIELDS_IN_ROW - 1));
        this.add(new Checker(color1, 3, NUMBER_OF_FIELDS_IN_ROW - 1));
        this.add(new Checker(color1, 5, NUMBER_OF_FIELDS_IN_ROW - 1));
        this.add(new Checker(color1, 7, NUMBER_OF_FIELDS_IN_ROW - 1));
        this.add(new Checker(color1, 0, NUMBER_OF_FIELDS_IN_ROW - 2));
        this.add(new Checker(color1, 2, NUMBER_OF_FIELDS_IN_ROW - 2));
        this.add(new Checker(color1, 4, NUMBER_OF_FIELDS_IN_ROW - 2));
        this.add(new Checker(color1, 6, NUMBER_OF_FIELDS_IN_ROW - 2));
        this.add(new Checker(color1, 1, NUMBER_OF_FIELDS_IN_ROW - 3));
        this.add(new Checker(color1, 3, NUMBER_OF_FIELDS_IN_ROW - 3));
        this.add(new Checker(color1, 5, NUMBER_OF_FIELDS_IN_ROW - 3));
        this.add(new Checker(color1, 7, NUMBER_OF_FIELDS_IN_ROW - 3));

     /*   if (NUMBER_OF_FIELDS_IN_ROW <= 8) {
            for (int col = 0; col < NUMBER_OF_FIELDS_IN_ROW; col += 2) {
                this.add(new Checker(Color.RED, col, 0));
                if (NUMBER_OF_FIELDS_IN_ROW % 2 == 0) {
                    this.add(new Checker(Color.BLACK, col + 1, NUMBER_OF_FIELDS_IN_ROW - 1));
                } else {
                    this.add(new Checker(Color.BLACK, col, NUMBER_OF_FIELDS_IN_ROW - 1));
                }
            }
        } else if (NUMBER_OF_FIELDS_IN_ROW < 8) {
            //@TODO
        } else {
            //@TODO
        }
*/
    }

    private void add(Checker checker) {
        try {
            this.gc.checkPosition(checker.getX(), checker.getY());
        } catch (FieldTakenException e) {
            System.out.println(e.getMessage());
        }

        BoardChecker bChecker = new BoardChecker();
        bChecker.checker = checker;
        bChecker.move(checker.getX(), checker.getY());
        boardCheckers[checker.getX()][checker.getY()] = bChecker;

    }


    public ArrayList<BoardChecker> getByColor(Color color) {
        return getByColor(color, this.boardCheckers);
    }

    public static ArrayList<BoardChecker> getByColor(Color color, BoardChecker[][] b) {

        ArrayList<BoardChecker> myCheckers = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_FIELDS_IN_ROW; i++) {
            for (int j = 0; j < NUMBER_OF_FIELDS_IN_ROW; j++) {
                if (b[i][j] != null) {
                    if (b[i][j].checker.getColor() == color) {
                        myCheckers.add(b[i][j]);
                    }
                }
            }
        }
        return myCheckers;
    }


}
