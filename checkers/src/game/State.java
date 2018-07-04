package game;

import java.util.ArrayList;

public class State {

    public static final int UNDEFINED = -123456;

    Jump jump;
    double value = UNDEFINED;
    BoardChecker checker;
    BoardChecker[][] boardCheckers;
    int depth;
    ArrayList<State> states = new ArrayList<>();

}
