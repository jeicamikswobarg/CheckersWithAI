package game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MoveCheckerListener implements MouseListener {

    private BoardChecker chosenOne = null;
    private GameController gameController;

    MoveCheckerListener(GameController gc) {
        this.gameController = gc;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameController.currentPlayer.ai == null) {
            int x = e.getX();
            int y = e.getY();
            Move move = new Move(gameController.parseBoardCoord(x), gameController.parseBoardCoord(y));
            try {
                if (chosenOne == null && gameController.board.boardCheckers[move.x][move.y].colorsEqual(gameController.currentPlayer.color)) {
                    chosenOne = gameController.board.boardCheckers[move.x][move.y];
                    if (chosenOne != null) {
                        gameController.board.paintPossibleMovesAncChecker(chosenOne);
                    }
                } else if (chosenOne != null) {
                    Checker temp = new Checker(chosenOne.checker);
                    boolean success = gameController.makeMove(chosenOne, move);
                    gameController.board.repaint();
                    chosenOne = null;
                    if(success){
                        if (gameController.board.boardCheckers[temp.getX()][temp.getY()] == null) {
                            gameController.nextTurn();
                        }
                    }

                }
            } catch (NullPointerException e1) {

            }

        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
