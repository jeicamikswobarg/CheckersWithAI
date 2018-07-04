package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class CheckersLauncher extends JFrame {

    private String path = "C:\\Users\\Dell\\Desktop\\AlfaBeta\\checkers\\queen.png"; //zmienic
    public static Image QUEEN_IMG;

    Board board;

    private CheckersLauncher()
    {
        try {
            QUEEN_IMG = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Wrong path for image.");
        }
        initGame();
    }

    private void initGame()
    {
        setTitle("Checkers");
        setLocationRelativeTo(null);
        board = new Board();
        setContentPane(board);
        pack();
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(() -> {
            CheckersLauncher game = new CheckersLauncher();
            game.setVisible(true);
        });

    }
}
