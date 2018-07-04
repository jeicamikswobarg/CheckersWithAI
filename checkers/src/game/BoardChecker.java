package game;

import java.awt.*;

import static game.Board.FIELD_SIZE;

public class BoardChecker {
    public Checker checker;
    public int x;
    public int y;


    BoardChecker() {
    }

    //copy constructor
    BoardChecker(BoardChecker anotherBoardChecker) {
        this.x = anotherBoardChecker.x;
        this.y = anotherBoardChecker.y;
        this.checker = new Checker(anotherBoardChecker.checker);

    }

    public void move(int x, int y) {
        this.x = x * FIELD_SIZE + FIELD_SIZE / 2;
        this.y = y * FIELD_SIZE + FIELD_SIZE / 2;
        checker.move(x,y);
    }

    public void draw(Graphics g)
    {
        int x = this.x - Checker.getSize() / 2;
        int y = this.y - Checker.getSize() / 2;

        g.setColor(checker.getColor());

        g.fillOval(x, y, Checker.getSize(), Checker.getSize());
        g.setColor(Color.WHITE);
        g.drawOval(x, y, Checker.getSize(), Checker.getSize());

        if (checker.isQueen()) {
            g.drawImage(CheckersLauncher.QUEEN_IMG, x, y, Checker.getSize(), Checker.getSize(), null);
        }
    }

    public boolean colorsEqual(BoardChecker anotherChecker) {
        return this.checker.getColor() == anotherChecker.checker.getColor();
    }

    public boolean colorsEqual(Color anotherColor) {
        return this.checker.getColor() == anotherColor;
    }
}
