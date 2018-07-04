package game;

import java.awt.*;

import static game.Board.NUMBER_OF_FIELDS_IN_ROW;

public class Checker {

    private final static int size = 75;
    private int x;
    private int y;
    private Color color;
    private boolean isQueen = false;

    public Checker(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    //copy constructor
    public Checker(Checker anotherChecker ) {
        this.x = anotherChecker.getX();
        this.y = anotherChecker.getY();
        this.color = anotherChecker.getColor();
        this.isQueen = anotherChecker.isQueen();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static int getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }

    public boolean isQueen() {
        return isQueen;
    }

    public void setIsQueen(boolean queen) {
        this.isQueen = queen;
    }

    public void move(int x, int y){
        this.setY(y);
        this.setX(x);
        int enemyLine = this.getColor() == Color.RED ?  NUMBER_OF_FIELDS_IN_ROW-1 : 0;
        if(y == enemyLine){
            this.setIsQueen(true);
        }
    }
}
