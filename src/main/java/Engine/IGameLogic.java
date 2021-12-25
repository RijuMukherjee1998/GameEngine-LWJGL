package Engine;
import Engine.Inputs.MouseInput;
import Engine.Window.Window;

public interface IGameLogic {
    void init(Window window) throws Exception;
    void input(Window window, MouseInput mouseInput);
    void update(float interval, MouseInput mouseInput);
    void render(Window window);
    void cleanup();

}
