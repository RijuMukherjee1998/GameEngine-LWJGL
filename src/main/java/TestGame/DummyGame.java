package TestGame;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import Engine.GameObjects.GameItem;
import Engine.IGameLogic;
import Engine.Mesh.Mesh;
import Engine.Window.Window;

public class DummyGame implements IGameLogic {

    private int direction = 0;

    private float color = 0.0f;

    private final Renderer renderer;

    private GameItem[] gameItems;

    public DummyGame() {
        renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        float[] positions = new float[]{
                // VO
                -0.5f, 0.5f, 0.5f,
// V1
                -0.5f, -0.5f, 0.5f,
// V2
                0.5f, -0.5f, 0.5f,
// V3
                0.5f, 0.5f, 0.5f,
// V4
                -0.5f, 0.5f, -0.5f,
// V5
                0.5f, 0.5f, -0.5f,
// V6
                -0.5f, -0.5f, -0.5f,
// V7
                0.5f, -0.5f, -0.5f,
        };

        int[] indices = new int[] {
                0, 1, 3, 3, 1, 2,
// Top Face
                4, 0, 3, 5, 4, 3,
// Right face
                3, 2, 7, 5, 3, 7,
// Left face
                6, 1, 0, 6, 0, 4,
// Bottom face
                2, 1, 6, 2, 6, 7,
// Back face
                7, 6, 4, 7, 4, 5,

        };

        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,

        };
        Mesh mesh = new Mesh(positions, colors, indices);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setPosition(0, 0, -2);
        gameItems = new GameItem[] {gameItem};
    }

    @Override
    public void input(Window window) {
        if ( window.isKeyPressed(GLFW_KEY_UP) ) {
            direction = 1;
        } else if ( window.isKeyPressed(GLFW_KEY_DOWN) ) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update(float interval) {
        float rotation = gameItems[0].getRotation().x + 1.5f;
        if(rotation > 360) {
            rotation = 0;
        }
        gameItems[0].setRotation(rotation, rotation, rotation);
    }

    @Override
    public void render(Window window)
    {
        renderer.render(window, gameItems);
        window.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }
    @Override
    public void cleanup(){
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
