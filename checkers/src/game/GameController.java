package game;

import exception.FieldTakenException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static game.Board.FIELD_SIZE;
import static game.Board.NUMBER_OF_FIELDS_IN_ROW;


public class GameController {
    Player player1;
    Player player2;
    Board board;
    Player currentPlayer;
    boolean gameOver = false;

    public GameController(Board b) {
        this.board = b;
        player1 = new Player(Color.BLACK, this, false);
        player2 = new Player(Color.RED, this, true);
        currentPlayer = player1;
    }

    public int parseBoardCoord(int coord) {
        return coord / FIELD_SIZE;
    }

    public void nextTurn() {
        isGameOver();
        currentPlayer = (currentPlayer == player1 ? player2 : player1);
        if (currentPlayer.ai != null) {
            currentPlayer.ai.run();
        }
    }

    public boolean checkPosition(int x, int y) throws FieldTakenException {
        return checkPosition(x, y, board.boardCheckers);
    }


    public boolean checkPosition(int x, int y, BoardChecker[][] boardCheckers) throws FieldTakenException {
        if (x < 0 || x > NUMBER_OF_FIELDS_IN_ROW - 1 || y < 0 || y > NUMBER_OF_FIELDS_IN_ROW - 1) {
            return false;
        }
        if (boardCheckers[x][y] == null) {
            return true;
        }
        throw new FieldTakenException();
    }

    //Move checker on real board
    public void moveChecker(BoardChecker ch, Jump jump) {
        moveChecker(ch, jump.move.x, jump.move.y, board.boardCheckers);
    }

    //Move checker on mocked board
    public void moveChecker(BoardChecker ch, int x, int y, BoardChecker[][] mockCheckers) {
        mockCheckers[ch.checker.getX()][ch.checker.getY()] = null;
        mockCheckers[x][y] = ch;
        ch.move(x, y);
    }

    public void deleteChecker(BoardChecker checker) {
        this.deleteChecker(checker, board.boardCheckers);
    }

    public void deleteChecker(BoardChecker checker, BoardChecker[][] mockCheckers) {
        mockCheckers[checker.checker.getX()][checker.checker.getY()] = null;
    }


    public boolean makeMove(BoardChecker ch, Move move) {
        if(!isMandatoryCapture(ch)){
            System.out.println("Wrong move. You have mandatory jump.");
            return false;
        }
        for (Jump possibleJump : getPossibleMoves(ch)) {
            if (possibleJump.move.equals(move) && possibleJump.nextJump == null) { //Simple moves
                moveChecker(ch, possibleJump);
                if (possibleJump.enemyChecker != null) {
                    deleteChecker(possibleJump.enemyChecker);
                }
            } else if (possibleJump.nextJump != null) { //Multijump
                Jump tempJump = possibleJump;
                ArrayList<BoardChecker> checkersToDelete = new ArrayList<>();
                if (possibleJump.enemyChecker != null) {
                    checkersToDelete.add(possibleJump.enemyChecker);
                }
                while (tempJump.nextJump != null) {
                    tempJump = tempJump.nextJump;
                    if (tempJump.enemyChecker != null) {
                        checkersToDelete.add(tempJump.enemyChecker);
                    }
                }
                if (tempJump.move.equals(move)) {
                    moveChecker(ch, tempJump);
                    if (!checkersToDelete.isEmpty()) {
                        for (BoardChecker bc : checkersToDelete) {
                            deleteChecker(bc);
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean makeMoveAI(BoardChecker ch, Jump jump) {

        return this.makeMoveAI(ch, jump, board.boardCheckers, player2);
    }

    public boolean makeMoveAI(BoardChecker ch, Jump jump, BoardChecker[][] boardCheckers, Player player) {

//        if(!isMandatoryCapture(ch, boardCheckers, player)){
//            return false;
//        }

        if (jump.nextJump == null) { //Simple moves
            moveChecker(ch, jump.move.x, jump.move.y, boardCheckers);
            if (jump.enemyChecker != null) {
                deleteChecker(jump.enemyChecker, boardCheckers);
            }
        } else { //Multijump
            Jump tempJump = jump;
            ArrayList<BoardChecker> checkersToDelete = new ArrayList<>();
            if (jump.enemyChecker != null) {
                checkersToDelete.add(jump.enemyChecker);
            }
            while (tempJump.nextJump != null) {
                tempJump = tempJump.nextJump;
                if (tempJump.enemyChecker != null) {
                    checkersToDelete.add(tempJump.enemyChecker);
                }
            }
            moveChecker(ch, tempJump.move.x, tempJump.move.y, boardCheckers);
            if (!checkersToDelete.isEmpty()) {
                for (BoardChecker bc : checkersToDelete) {
                    deleteChecker(bc, boardCheckers);
                }
            }
        }


        return true;
    }

    public boolean isMandatoryCapture(BoardChecker ch, BoardChecker[][] boardCheckers, Player player) {
        //Check if chosen checker has capture
        for (Jump j:getMovesForChecker(ch, boardCheckers)) {
            if(j.enemyChecker != null) {
                return true;
            }
        }
        //Check if other has mandatory jump
        ArrayList<BoardChecker> playerCheckers = Board.getByColor(player.color, boardCheckers);
        playerCheckers.remove(ch);
        for (BoardChecker bc:playerCheckers) {
            for (Jump j:getMovesForChecker(bc, boardCheckers)) {
                if(j.enemyChecker != null) {
                    return false;
                }
            }
        }

        return true;
    }


    private boolean isMandatoryCapture(BoardChecker ch) {
        return this.isMandatoryCapture(ch, this.board.boardCheckers, this.currentPlayer);
    }


    private void isGameOver() {
        if (board.getByColor(Color.RED).size() == 0 || board.getByColor(Color.BLACK).size() == 0) {
            gameOver = true;
            if (board.getByColor(Color.RED).size() == 0) {
                JOptionPane.showMessageDialog(null, "Black player won!");
            } else {
                JOptionPane.showMessageDialog(null, "Red player won!");
            }
        }
    }

    public ArrayList<Jump> getPossibleMoves(BoardChecker ch) {
        return new ArrayList<>(getMovesForChecker(ch));
    }


    private Move[] getPossibleSimpleMoves(BoardChecker ch) {

        //Red checkers Y start = 0
        //Black checkers Y start = NUMBER_OF_FIELDS_IN_ROW-1

        int baseLine = ch.checker.getColor() == Color.RED ? 0 : NUMBER_OF_FIELDS_IN_ROW - 1;

        int checkerX = ch.checker.getX();
        int checkerY = ch.checker.getY();

        Move[] possibleSimpleMoves = {
                new Move(checkerX - 1, checkerY - 1),
                new Move(checkerX - 1, checkerY + 1),
                new Move(checkerX + 1, checkerY - 1),
                new Move(checkerX + 1, checkerY + 1)};

        if (ch.checker.isQueen()) {
            return possibleSimpleMoves;
        } else if (baseLine == 0) {
            return new Move[]{possibleSimpleMoves[1], possibleSimpleMoves[3]};
        } else {
            return new Move[]{possibleSimpleMoves[0], possibleSimpleMoves[2]};
        }
    }

    private ArrayList<Jump> getMovesForChecker(BoardChecker ch) {
        return getMovesForChecker(ch, board.boardCheckers);
    }

    public ArrayList<Jump> getMovesForChecker(BoardChecker ch, BoardChecker[][] boardCheckers) {
        int checkerX = ch.checker.getX();
        int checkerY = ch.checker.getY();
        boolean forceCapture = false;

        ArrayList<Jump> simpleMoves = new ArrayList<>();
        Move[] possibleSimpleMoves = getPossibleSimpleMoves(ch);

        for (Move move : possibleSimpleMoves) {
            try {
                checkPosition(move.x, move.y, boardCheckers);
            } catch (FieldTakenException e) { //check if there is possible capture
                if (!ch.colorsEqual(boardCheckers[move.x][move.y])) {
                    int fieldBehindCheckerX = checkerX - ((checkerX - move.x) * 2);
                    int fieldBehindCheckerY = checkerY - ((checkerY - move.y) * 2);
                    try {
                        if (checkPosition(fieldBehindCheckerX, fieldBehindCheckerY,boardCheckers)) {
                            Jump captureJump = new Jump(new Move(fieldBehindCheckerX, fieldBehindCheckerY), boardCheckers[move.x][move.y]);

                            BoardChecker[][] mockCheckers = this.copyBoard(boardCheckers);
                            mockCheckers[move.x][move.y] = null;
                            moveChecker(new BoardChecker(mockCheckers[checkerX][checkerY]), fieldBehindCheckerX, fieldBehindCheckerY, mockCheckers);

                            ArrayList<Jump> possibleJumps = checkMultiCapture(captureJump, mockCheckers, ch);
                            simpleMoves.addAll(possibleJumps);
                            forceCapture = true;
                        }

                    } catch (FieldTakenException e1) {
                    }
                }
            }
        }
        //add simple moves if there was no capture
        if (!forceCapture) {
            for (Move move : possibleSimpleMoves) {
                try {
                    if (checkPosition(move.x, move.y, boardCheckers)) {
                        simpleMoves.add(new Jump(move));
                    }
                } catch (FieldTakenException e) {
                }
            }
        }

        return simpleMoves;
    }

    private ArrayList<Jump> checkMultiCapture(Jump captureJump, BoardChecker[][] mockCheckers, BoardChecker ch) {

        int checkerX = captureJump.move.x;
        int checkerY = captureJump.move.y;
        ArrayList<Jump> possibleJumps = new ArrayList<>();
        boolean morePossibilities = false;

        BoardChecker[][] copiedBoard = copyBoard(mockCheckers);

        Move[] possibleSimpleMoves = getPossibleSimpleMoves(mockCheckers[captureJump.move.x][captureJump.move.y]);

        for (Move move : possibleSimpleMoves) {
            try {
                checkPosition(move.x, move.y, mockCheckers);
            } catch (FieldTakenException e) {
                if (!ch.colorsEqual(mockCheckers[move.x][move.y])) {
                    int fieldBehindCheckerX = checkerX - ((checkerX - move.x) * 2);
                    int fieldBehindCheckerY = checkerY - ((checkerY - move.y) * 2);
                    try {
                        if (checkPosition(fieldBehindCheckerX, fieldBehindCheckerY, mockCheckers)) {
                            morePossibilities = true;
                            Jump previousJump = new Jump(captureJump);
                            Jump nextJump = new Jump(new Move(fieldBehindCheckerX, fieldBehindCheckerY), mockCheckers[move.x][move.y]);
                            mockCheckers[move.x][move.y] = null;
                            moveChecker(new BoardChecker(mockCheckers[checkerX][checkerY]), fieldBehindCheckerX, fieldBehindCheckerY, mockCheckers);
                            ArrayList<Jump> possibleSubJumps = checkMultiCapture(nextJump, mockCheckers, ch);
                            if (possibleJumps.contains(nextJump)) {
                                previousJump.nextJump = nextJump;
                                possibleJumps.add(previousJump);
                            } else {
                                for (Jump j : possibleSubJumps) {
                                    previousJump.nextJump = j;
                                    possibleJumps.add(previousJump);
                                }
                            }
                            mockCheckers = copiedBoard;
                        }

                    } catch (FieldTakenException e1) {
                    }
                }
            }
        }

        if (!morePossibilities) {
            possibleJumps.add(captureJump);
        }
        return possibleJumps;
    }


    public BoardChecker[][] copyBoard(BoardChecker[][] anotherBoard) {
        BoardChecker[][] mockCheckers = new BoardChecker[NUMBER_OF_FIELDS_IN_ROW][NUMBER_OF_FIELDS_IN_ROW];
        for (int i = 0; i < NUMBER_OF_FIELDS_IN_ROW; i++) {
            for (int j = 0; j < NUMBER_OF_FIELDS_IN_ROW; j++) {
                mockCheckers[i][j] = (anotherBoard[i][j] == null) ? null : new BoardChecker(anotherBoard[i][j]);
            }
        }
        return mockCheckers;
    }
}
