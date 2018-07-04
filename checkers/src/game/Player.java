package game;

import java.awt.*;

public class Player {
    GameController gc;
    Color color;
    AI ai;


    public Player(Color color, GameController gc, boolean isAi) {
        this.gc = gc;
        this.color = color;

        if(isAi){
            this.ai = new AI(gc);
        }

    }

}
