package TestGame;

import Engine.Camera.Camera;
import Engine.GameObjects.GameItem;
import Engine.GameObjects.OBJLoader;
import Engine.IGameLogic;
import Engine.Inputs.MouseInput;
import Engine.Light.DirectionalLight;
import Engine.Light.PointLight;
import Engine.Light.SceneLight;
import Engine.Light.SpotLight;
import Engine.Materials.Material;
import Engine.Mesh.Mesh;
import Engine.Render.Renderer;
import Engine.Textures.Texture;
import Engine.Window.Window;
import TestGame.GameItemGenerators.BunnyGenrator.BunnyGenerator;
import TestGame.GameItemGenerators.TerrainGenerator.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

    private final Renderer renderer;

    private final Camera camera;

    private final Vector3f cameraInc;

    private  Vector3f lightPosInc;

    private List<GameItem> gameItems;

    private SceneLight sceneLight;

    private Vector3f ambientLight;

    private List<PointLight> pointLightList;

    private List<SpotLight> spotLightList;

    private DirectionalLight directionalLight;

    private Hud hud;
    private float lightPosZ;

    private float lightAngle;

    private float spotAngle = 0;
    private float spotInc = 1;

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
        Mesh cubeMesh = OBJLoader.loadMesh("C:\\Dev\\GameEngine\\src\\main\\resources\\Models\\cube.obj");
        Texture texture = new Texture("C:\\Dev\\GameEngine\\src\\main\\resources\\Textures\\grass_block.png");
        Material material = new Material(texture);

        int terrainSize = 1;
        cubeMesh.setMaterial(material);
//        GameItem gameItem = new GameItem(mesh);
//        gameItem.setScale(1.0f);
//        gameItem.setPosition(0f,0,-2);
//        gameItems = new GameItem[] {gameItem};
        Terrain terrain = new Terrain(cubeMesh, terrainSize);
        gameItems = terrain.generateTerrain();
        Mesh bunnyMesh = OBJLoader.loadMesh("C:\\Dev\\GameEngine\\src\\main\\resources\\Models\\bunny.obj");
        BunnyGenerator bg = new BunnyGenerator(bunnyMesh, terrainSize);
        gameItems.addAll(bg.generateBunnies());

        sceneLight = new SceneLight();

        //Set Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f,0.3f));

        //Point Light 1
        Vector3f lightColour = new Vector3f(0.1f,0.1f,0.9f);
        Vector3f lightPosition = new Vector3f(0.3f,0,2);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f,0.0f,1.0f);
        pointLight.setAttenuation(att);

        //Point Light 1
        lightColour = new Vector3f(0.9f,0.1f,0.1f);
        lightPosition = new Vector3f(-0.3f,0,2);
        lightIntensity = 1.0f;
        PointLight pointLight1 = new PointLight(lightColour, lightPosition, lightIntensity);
        pointLight1.setAttenuation(att);
        pointLightList = new Vector<PointLight>();
        pointLightList.add(pointLight);
        pointLightList.add(pointLight1);
        sceneLight.setPointLightList(pointLightList);


        //Spot Light
        lightPosition = new Vector3f(0, 0, 10);
        PointLight sl_pointLight = new PointLight(new Vector3f(1,1,1),lightPosition, 10);
        att = new PointLight.Attenuation(0.0f,0.0f,0.02f);
        sl_pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0,0,-1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(sl_pointLight, coneDir, cutoff);
        spotLightList = new Vector<SpotLight>();
        spotLightList.add(spotLight);
        sceneLight.setSpotLightList(spotLightList);

        //Directional Light
        lightPosition = new Vector3f(-1, 0,0);
        lightColour = new Vector3f(1,1,1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);

        hud = new Hud(window,"BUNNY");
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

        float lightPos = pointLightList.get(0).getPosition().z;

        if(window.isKeyPressed(GLFW_KEY_N))
        {
            this.pointLightList.get(0).getPosition().z = lightPos + 0.001f;
            this.pointLightList.get(1).getPosition().z = lightPos + 0.001f;
        }
        else if(window.isKeyPressed(GLFW_KEY_M))
        {
            this.pointLightList.get(0).getPosition().z = lightPos - 0.001f;
            this.pointLightList.get(1).getPosition().z = lightPos - 0.001f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        //Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y  * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        //Update camera based on mouse
        if(mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        //Update point light position
         //lightPosInc = new Vector3f(spotLight.getPointLight().getPosition().x, spotLight.getPointLight().getPosition().y, lightPosZ);
         //spotLight.getPointLight().setPosition(lightPosInc);

        //Update the SpotLight
        spotAngle += spotInc * 0.05f;
        if(spotAngle > 2) {
            spotInc = -1;
        }
        else if(spotAngle < -2)
        {
            spotInc = 1;
        }
        double spotAngleRad = Math.toRadians(spotAngle);
        Vector3f coneDir = spotLightList.get(0).getConeDirection();
        coneDir.y = (float) Math.sin(spotAngleRad);

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
        renderer.render(window, camera, gameItems, sceneLight, hud);
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
