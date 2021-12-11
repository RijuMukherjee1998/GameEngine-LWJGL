package Engine;
import Engine.Window.Window;

public interface IGameLogic {
    void init(Window window) throws Exception;
    void input(Window window);
    void update(float interval);
    void render(Window window);
    void cleanup();

}
