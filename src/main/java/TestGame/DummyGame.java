package TestGame;

import Engine.Camera.Camera;
import Engine.GameObjects.GameItem;
import Engine.GameObjects.OBJLoader;
import Engine.IGameLogic;
import Engine.Inputs.MouseInput;
import Engine.Light.DirectionalLight;
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

    private  Vector3f lightPosInc;

    private GameItem[] gameItems;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private DirectionalLight directionalLight;

    private float lightPosZ;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    public DummyGame() {

        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f();
        lightAngle = -90;
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
        Vector3f lightPosition = new Vector3f(0,0,2);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f,0.0f,1.0f);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(-1, 0,0);
        lightColour = new Vector3f(1,1,1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);
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

        lightPosZ = pointLight.getPosition().z;
        if(window.isKeyPressed(GLFW_KEY_N))
        {
            lightPosZ = lightPosZ + 0.1f;
        }
        else if(window.isKeyPressed(GLFW_KEY_M))
        {
            lightPosZ = lightPosZ - 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        //Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y  * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        //Update camera based on mouse
        if(mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            System.out.println(mouseInput.getDisplVec());
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        //Update point light position
        lightPosInc = new Vector3f(pointLight.getPosition().x, pointLight.getPosition().y, lightPosZ);
        pointLight.setPosition(lightPosInc);

        //Update Directional light
        lightAngle += 1.1f;
        if(lightAngle > 90) {
            directionalLight.setIntensity(0);
            if(lightAngle >= 360) {
                lightAngle = -90;
            }
        }
        else if(lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angleRadian = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angleRadian);
        directionalLight.getDirection().y = (float) Math.cos(angleRadian);
    }

    @Override
    public void render(Window window)
    {
        renderer.render(window,camera,gameItems,ambientLight,pointLight,directionalLight);
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
