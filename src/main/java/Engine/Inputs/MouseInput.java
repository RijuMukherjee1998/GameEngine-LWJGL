package Engine.Inputs;

import Engine.Window.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private Vector2d previousPos;
    private Vector2d currentPos;
    private Vector2f displVec;
    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0,0);
        displVec = new Vector2f();
        displVec.x = 0;
        displVec.y = 0;
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) ->
        {
           currentPos.x = xpos;
           currentPos.y = ypos;
        });

        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) ->
        {
                inWindow = entered;
        });

        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) ->
        {
            leftButtonPressed  = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });

    }
    public Vector2f getDisplVec()
    {
        return displVec;
    }

    public void input() {
        if(previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if(rotateX) {
                displVec.y = (float) deltaX;
            }
            if(rotateY) {
                displVec.x = (float) deltaY;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return  rightButtonPressed;
    }
}
