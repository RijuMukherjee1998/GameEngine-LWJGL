package Engine;


import Engine.Window.Window;
import Engine.Timer.Timer;

import static org.lwjgl.opengl.GL11.*;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;
    private final Window window;
    private final Timer timer;
    private final IGameLogic gameLogic;

    public GameEngine(String windowTitle, int width, int height,
                      boolean vsSync, IGameLogic gameLogic)
    {
        window = new Window(windowTitle, width, height, vsSync);
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    @Override
    public void run() {
        try {
            init(window);
            gameLoop();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            cleanup();
        }
    }

    protected void init(Window window) throws Exception {
        window.init();
        timer.init();
        gameLogic.init(window);
        glEnable(GL_DEPTH_TEST);
    }

    protected void gameLoop()
    {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        while (!window.windowShouldClose())
        {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while(accumulator >= interval)
            {
                update(interval);
                accumulator -= interval;
            }

            render();

            if(!window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;

        while(timer.getTime() < endTime)
        {
            try{
                Thread.sleep(1);
            } catch (InterruptedException ie) {

            }
        }
    }

    protected void input() {
        gameLogic.input(window);
    }

    protected void update(float interval) {
        gameLogic.update(interval);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }

}
