package game;

public class Move {
    public int x;
    public int y;

    public Move(Move anotherMove) {
        this.x = anotherMove.x;
        this.y = anotherMove.y;
    }

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Move move) {
        return (move.x == this.x && move.y == this.y);
    }
}
