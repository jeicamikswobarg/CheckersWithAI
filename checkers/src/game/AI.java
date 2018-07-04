package game;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static game.Board.NUMBER_OF_FIELDS_IN_ROW;

public class AI {

    private GameController gc;
    private ArrayList<BoardChecker> AIcheckers;

    int DEPTH = 4;
    int PIECE = 10;
    int QUEEN = 15;
    int ON_SIDE = 1; //to za dużo nie zmienia
    int DIST = 5;
    int GAME_OVER = 1000;
    int CAPTURE = 5;
    double ALFA_WORST = State.UNDEFINED;
    double BETA_WORST = -State.UNDEFINED;
    int wezly1,wezly2 = 0;


    public AI(GameController gc) {
        this.gc = gc;
    }

    public void run() {
        State AImove1 = new State();
        State AImove2 = new State();
        AImove1.boardCheckers = gc.board.boardCheckers;
        AImove1.depth = 0;
        AImove2.boardCheckers = gc.board.boardCheckers;
        AImove2.depth = 0;
        wezly1 = 0;
        wezly2 = 0;
        long startTime = System.currentTimeMillis();
        this.getTree(AImove1, gc.player2);
          this.recursiveMinMax(AImove1, true);
        // this.recursiveAlfaBeta(AImove, true, ALFA_WORST, BETA_WORST);

        //this.recursiveAlfaBeta2(AImove, true, ALFA_WORST, BETA_WORST,gc.player2);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Czas minmax "+ elapsedTime);
        System.out.println("Liczba wezlow"+wezly1);

        startTime = System.currentTimeMillis();

        // this.recursiveAlfaBeta(AImove, true, ALFA_WORST, BETA_WORST);

        this.recursiveAlfaBeta2(AImove2, true, ALFA_WORST, BETA_WORST,gc.player2);
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Czas alfabeta "+ elapsedTime);
        System.out.println("Liczba wezlow "+wezly2);


        this.makeMove(AImove1);
        this.gc.nextTurn();
    }

    private double recursiveMinMax(State state, boolean max) {
        wezly1 +=1;
        if (state.depth == DEPTH) {
            return state.value;
        }
        double bestValue = State.UNDEFINED;
        double tempValue;
        for (State s : state.states) {
            tempValue = this.recursiveMinMax(s, !max);
            if (bestValue == State.UNDEFINED) {
                bestValue = tempValue;
                if (state.depth == 0) {
                    state.value = bestValue;
                    state.checker = s.checker;
                    state.jump = s.jump;
                }
            }
            if ((max && tempValue > bestValue) || (!max && tempValue < bestValue)) {
                bestValue = tempValue;
                if (state.depth == 0) {
                    state.value = bestValue;
                    state.checker = s.checker;
                    state.jump = s.jump;
                }

            }
        }

        state.value = bestValue;
        return state.value;
    }

    private double recursiveAlfaBeta(State state, boolean max, double alfa, double beta) {
        if (state.depth == DEPTH) {
            state.value = this.calculateValueOfBoardState(state.boardCheckers);
            return state.value;
        }
        double tempValue;
        state.value = (max) ? State.UNDEFINED : -State.UNDEFINED;
        for (State s : state.states) {
            if (max) {

                tempValue = this.recursiveAlfaBeta(s, !max, alfa, beta);
                if (tempValue > alfa) {
                    alfa = tempValue;
                    state.value = alfa;
                    if (state.depth == 0) {
                        state.checker = s.checker;
                        state.jump = s.jump;
                    }
                }
                if (alfa >= beta)
                    break;
            } else {
                tempValue = this.recursiveAlfaBeta(s, !max, alfa, beta);
                if (tempValue < beta) {
                    beta = tempValue;
                    state.value = beta;
                    if (state.depth == 0) {
                        state.checker = s.checker;
                        state.jump = s.jump;
                    }
                }
                if (alfa >= beta)
                    break;
            }
        }
        return state.value;
    }

    private State recursiveAlfaBeta2(State s, boolean max, double alfa, double beta,Player player) {
        wezly2 +=1;
        if (s.depth == DEPTH) {
            s.value = this.calculateValueOfBoardState(s.boardCheckers);
            return s;
        }

        State tempState;



        ArrayList<BoardChecker> checkersAI = Board.getByColor(player.color, s.boardCheckers);

        if (checkersAI.size() == 0) {
            s.value = this.calculateValueOfBoardState(s.boardCheckers);
            return s;
        }

        ArrayList<Jump> allPossibleMoves;
        boolean isAI;
        Color color;
        ArrayList<BoardChecker> mockedCheckersAI = new ArrayList<>();

        for (BoardChecker x : checkersAI) {
            if (this.hasCapture(x, s.boardCheckers)) {
                mockedCheckersAI.add(new BoardChecker(x));
            }
        }

        if (mockedCheckersAI.isEmpty()) {
            for (BoardChecker x : checkersAI) {
                mockedCheckersAI.add(new BoardChecker(x));
            }
        }


        for (BoardChecker ch : mockedCheckersAI) {
            allPossibleMoves = gc.getMovesForChecker(ch, s.boardCheckers);

            for (Jump move : allPossibleMoves) {

                State temp = new State();
                BoardChecker ch2 = new BoardChecker(ch);
                temp.checker = s.boardCheckers[ch.checker.getX()][ch.checker.getY()];

                temp.jump = move;
                temp.value = (max) ? -State.UNDEFINED : State.UNDEFINED;
                temp.boardCheckers = getBoardAfterPossibleJump(s.boardCheckers, move, ch2, player);
                temp.depth = s.depth + 1;
                isAI = player.color == Color.RED ? false : true;
                if (isAI)
                    color = Color.RED;
                else
                    color = Color.BLACK;

                if (max) {

                    tempState = this.recursiveAlfaBeta2(temp, !max, alfa, beta,new Player(color, gc, isAI));

                    if (tempState.value > alfa) {
                        alfa = tempState.value;
                        s.value = alfa;
                        if (s.depth == 0) {
                            s.checker = tempState.checker;
                            s.jump = tempState.jump;
                        }
                    }
                    if (alfa >= beta)
                        break;
                } else {
                    tempState = this.recursiveAlfaBeta2(temp, !max, alfa, beta,new Player(color, gc, isAI));
                    if (tempState.value < beta) {
                        beta = tempState.value;
                        s.value = beta;
                        if (s.depth == 0) {
                            s.checker = tempState.checker;
                            s.jump = tempState.jump;
                        }
                    }
                    if (alfa >= beta)
                        break;
                }

            }

        }
        return s;
    }





    public void makeMove(State state) {
        this.gc.makeMoveAI(gc.board.boardCheckers[state.checker.checker.getX()][state.checker.checker.getY()], state.jump);
    }


    public void getTree(State s, Player player) {
        if (s.depth == DEPTH) {
            s.value = this.calculateValueOfBoardState(s.boardCheckers);

            return;
        }
        ArrayList<BoardChecker> checkersAI = Board.getByColor(player.color, s.boardCheckers);

        if (checkersAI.size() == 0) {
            s.value = this.calculateValueOfBoardState(s.boardCheckers);
            return;
        }

        ArrayList<Jump> allPossibleMoves;
        boolean isAI;
        Color color;
        ArrayList<BoardChecker> mockedCheckersAI = new ArrayList<>();

        for (BoardChecker x : checkersAI) {
            if (this.hasCapture(x, s.boardCheckers)) {
                mockedCheckersAI.add(new BoardChecker(x));
            }
        }

        if (mockedCheckersAI.isEmpty()) {
            for (BoardChecker x : checkersAI) {
                mockedCheckersAI.add(new BoardChecker(x));
            }
        }


        for (BoardChecker ch : mockedCheckersAI) {
            allPossibleMoves = gc.getMovesForChecker(ch, s.boardCheckers);

            for (Jump move : allPossibleMoves) {
                State temp = new State();
                BoardChecker ch2 = new BoardChecker(ch);
                temp.checker = s.boardCheckers[ch.checker.getX()][ch.checker.getY()];

                temp.jump = move;

                temp.boardCheckers = getBoardAfterPossibleJump(s.boardCheckers, move, ch2, player);
                temp.depth = s.depth + 1;
                isAI = player.color == Color.RED ? false : true;
                if (isAI)
                    color = Color.RED;
                else
                    color = Color.BLACK;
                this.getTree(temp, new Player(color, gc, isAI));
                s.states.add(temp);

            }

        }
    }


    public BoardChecker[][] getBoardAfterPossibleJump(BoardChecker[][] boardCheckers, Jump jump, BoardChecker movingChecker, Player player) {
        BoardChecker[][] mockedBoard = this.gc.copyBoard(boardCheckers);
        this.gc.makeMoveAI(movingChecker, jump, mockedBoard, player);
        return mockedBoard;
    }


    public double calculateValueOfBoardState(BoardChecker[][] boardCheckers) {
        double result = 0;
        for (int i = 0; i < NUMBER_OF_FIELDS_IN_ROW; i++) {
            for (int j = 0; j < NUMBER_OF_FIELDS_IN_ROW; j++) {
                if (boardCheckers[i][j] != null) {
                    result += getValueOfChecker(boardCheckers[i][j], boardCheckers);
                }
            }
        }

        if (Board.getByColor(Color.RED, boardCheckers).size() == 0) {
            result -= GAME_OVER;
        } else if (Board.getByColor(Color.BLACK, boardCheckers).size() == 0) {
            result += GAME_OVER;
        }

        return result;
    }


    public double getValueOfChecker(BoardChecker ch, BoardChecker[][] boardCheckers) {
        double result = 0;

        result += ch.checker.isQueen() ? QUEEN : PIECE;
        result += checkIsOnSide(ch) ? ON_SIDE : 0;
        if (!ch.checker.isQueen() && ch.checker.getColor() == gc.player2.color)
            result += ch.checker.getY() * (1.00 / (Board.NUMBER_OF_FIELDS_IN_ROW - 1)) * DIST;

        if (!ch.checker.isQueen() && ch.checker.getColor() == gc.player1.color)
            result += (5 - ch.checker.getY() * (1.00 / (Board.NUMBER_OF_FIELDS_IN_ROW - 1)) * DIST);


//        if (hasCapture(ch, boardCheckers)) {
//            result += CAPTURE;
//        }
        if (ch.checker.getColor() != gc.player2.color) {
            result *= -1;
        }

        return result;
    }

    public boolean checkIsOnSide(BoardChecker ch) {

        if (ch.checker.getY() == 0 || ch.checker.getY() == NUMBER_OF_FIELDS_IN_ROW - 1 || ch.checker.getX() == 0 || ch.checker.getX() == NUMBER_OF_FIELDS_IN_ROW - 1) {
            return true;
        }

        return false;
    }

    public boolean hasCapture(BoardChecker ch, BoardChecker[][] boardCheckers) {
        //Check if chosen checker has capture
//        int enemyX[] = {ch.checker.getX()-1, ch.checker.getX()+1};
//        int enemyY[] = {ch.checker.getY()-1, ch.checker.getY()+1};
//
//        for(int i = 0; i < 2; i++) {
//            for(int j = 0;j<2;j++) {
//                try {
//                    this.gc.checkPosition(enemyX[i], enemyY[j], boardCheckers;
//
//
//                } catch (FieldTakenException e) {
//                }
//            }
//        }

        for (Jump j : this.gc.getMovesForChecker(ch, boardCheckers)) {
            if (j.enemyChecker != null) {
                return true;
            }
        }
        return false;
    }


    public void randomMove() {
        AIcheckers = gc.board.getByColor(gc.currentPlayer.color);
        ArrayList<Jump> allPossibleMoves = new ArrayList<>();
        boolean success = false;
        int bestCheckerOption = 0;

        if (AIcheckers.size() > 0) {
            while (!success) {
                while (allPossibleMoves.size() == 0) {
                    bestCheckerOption = ThreadLocalRandom.current().nextInt(0, AIcheckers.size());
                    allPossibleMoves = gc.getPossibleMoves(AIcheckers.get(bestCheckerOption));
                }
                int bestMoveOption = ThreadLocalRandom.current().nextInt(0, allPossibleMoves.size());

                if (gc.makeMoveAI(AIcheckers.get(bestCheckerOption), allPossibleMoves.get(bestMoveOption))) {
                    success = true;
                } else {
                    AIcheckers.remove(AIcheckers.get(bestCheckerOption));
                }
                allPossibleMoves.clear();
            }
        }
    }
}

