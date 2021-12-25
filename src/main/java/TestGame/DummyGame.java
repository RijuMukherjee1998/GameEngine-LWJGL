package TestGame;

import Engine.Camera.Camera;
import Engine.GameObjects.GameItem;
import Engine.GameObjects.OBJLoader;
import Engine.IGameLogic;
import Engine.Inputs.MouseInput;
import Engine.Light.PointLight;
import Engine.Materials.Material;
import Engine.Mesh.Mesh;
import Engine.Textures.Texture;
import Engine.Window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

    private final Renderer renderer;

    private final Camera camera;

    private final Vector3f cameraInc;

    private GameItem[] gameItems;

    private Vector3f ambientLight;
    private PointLight pointLight;

    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    public DummyGame() {

        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        float reflectance = 1.0f;
        Mesh mesh = OBJLoader.loadMesh("C:\\Dev\\GameEngine\\src\\main\\resources\\Models\\cube.obj");
        Texture texture = new Texture("C:\\Dev\\GameEngine\\src\\main\\resources\\Textures\\grass_block.png");
        Material material = new Material(texture);

        mesh.setMaterial(material);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0,0,-2);
        gameItems = new GameItem[] {gameItem};

        ambientLight = new Vector3f(0.3f,0.3f,0.3f);
        Vector3f lightColour = new Vector3f(1,1,1);
        Vector3f lightPosition = new Vector3f(0,0,-1);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f,0.0f,1.0f);
        pointLight.setAttenuation(att);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0,0,0);
        if ( window.isKeyPressed(GLFW_KEY_W) ) {
            cameraInc.z = -1;
        } else if ( window.isKeyPressed(GLFW_KEY_S) ) {
            cameraInc.z =  1;
        }
        if(window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if(window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if(window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

        if(mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            System.out.println(mouseInput.getDisplVec());
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        //Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y  * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        //Update camera based on mouse

    }

    @Override
    public void render(Window window)
    {
        renderer.render(window,camera,gameItems,ambientLight,pointLight);
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
