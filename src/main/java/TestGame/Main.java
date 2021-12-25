package TestGame;
import Engine.GameEngine;
import Engine.IGameLogic;

public class Main {
    public static void main(String args[]) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            GameEngine gameEng = new GameEngine("DummyGame", 600, 480, vSync, gameLogic);
            gameEng.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}