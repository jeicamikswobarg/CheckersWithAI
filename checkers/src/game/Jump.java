package game;

public class Jump {

    public BoardChecker enemyChecker;
    public Move move;
    public Jump nextJump;

    public Jump(Jump anotherJump) {
        this.enemyChecker = new BoardChecker(anotherJump.enemyChecker);
        this.move = new Move(anotherJump.move);
        if(anotherJump.nextJump != null) {
            this.nextJump = new Jump(anotherJump.nextJump);
        }
    }

    public Jump(Move move) {
        this.move = move;
    }

    public Jump(Move move, BoardChecker enemyChecker) {
        this.enemyChecker = enemyChecker;
        this.move = move;
    }

}
