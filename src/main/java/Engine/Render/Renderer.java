package Engine.Render;
import Engine.Camera.Camera;
import Engine.GameObjects.GameItem;
import Engine.HUD.IHud;
import Engine.Light.DirectionalLight;
import Engine.Light.PointLight;
import Engine.Light.SceneLight;
import Engine.Light.SpotLight;
import Engine.Mesh.Mesh;
import Engine.Shader.ShaderProgram;
import Engine.Transformations.Transformation;
import Engine.Utils.LoadResource;
import Engine.Window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private Transformation transformation;
    private ShaderProgram shaderProgram;
    private ShaderProgram sceneShaderProgram;
    private ShaderProgram hudShaderProgram;
    private float specularPower;

    public Renderer() {

        transformation = new Transformation();
        specularPower = 10.0f;
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.CreateVertexShader(LoadResource.loadResource("C:\\Dev\\GameEngine\\src\\main\\resources\\Shaders\\hud_vertex.vs.glsl"));
        hudShaderProgram.CreateFragmentShader(LoadResource.loadResource("C:\\Dev\\GameEngine\\src\\main\\resources\\Shaders\\hud_fragment.fs.glsl"));
        hudShaderProgram.Link();

        //Create Uniforms for Orthographic projection and base colour
        hudShaderProgram.CreateUniform("projModelMatrix");
        hudShaderProgram.CreateUniform("colour");
        hudShaderProgram.CreateUniform("hasTexture");
    }
    private void setupSceneShader() throws Exception {
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.CreateVertexShader(LoadResource.loadResource("C:\\Dev\\GameEngine\\src\\main\\resources\\Shaders\\vertex.vs.glsl"));
        sceneShaderProgram.CreateFragmentShader(LoadResource.loadResource("C:\\Dev\\GameEngine\\src\\main\\resources\\Shaders\\fragment.fs.glsl"));
        sceneShaderProgram.Link();

        sceneShaderProgram.CreateUniform("projectionMatrix");
        sceneShaderProgram.CreateUniform("modelViewMatrix");
        sceneShaderProgram.CreateUniform("texture_sampler");

        //Create Uniform for Materials
        sceneShaderProgram.CreateMaterialUniform("material");
        //Create lighting related uniforms
        sceneShaderProgram.CreateUniform("specularPower");
        sceneShaderProgram.CreateUniform("ambientLight");
        sceneShaderProgram.CreatePointLightListUniform("pointLights", MAX_POINT_LIGHTS );
        sceneShaderProgram.CreateSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.CreateDirectionalLightUniform("directionalLight");
    }
    public void init(Window window) throws Exception {
        setupSceneShader();
        setupHudShader();
    }

    public void render(Window window, Camera camera, List<GameItem> gameItems, SceneLight sceneLight, IHud hud)
    {
        clear();

        if(window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        System.out.println(sceneLight);
        renderScene(window, camera, gameItems, sceneLight);

        renderHud(window, hud);
    }

    private void renderScene(Window window, Camera camera, List<GameItem> gameItems, SceneLight sceneLight)
    {
        sceneShaderProgram.Bind();
        //Update the Projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(
                FOV,
                window.getWidth(),
                window.getHeight(),
                Z_NEAR, Z_FAR);
        sceneShaderProgram.SetUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        renderLights(viewMatrix, sceneLight);

        for(GameItem gameItem : gameItems) {
            //Set world matrix for this item
            Mesh mesh = gameItem.getMesh();
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            sceneShaderProgram.SetUniform("modelViewMatrix", modelViewMatrix);
            //Render the mesh for this game items.
            sceneShaderProgram.SetUniform("material", mesh.getMaterial());
            mesh.render();
        }

        sceneShaderProgram.UnBind();
    }


    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight)
    {
        // Update Light Uniforms
        sceneShaderProgram.SetUniform("ambientLight", sceneLight.getAmbientLight());
        sceneShaderProgram.SetUniform("specularPower", specularPower);

        // Process Point Lights
        List<PointLight> pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.size(): 0;
        for (int i = 0; i<numLights; i++)
        {
            PointLight currPointLight = new PointLight(pointLightList.get(i));
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            sceneShaderProgram.SetUniform("pointLights", currPointLight, i);
        }

        // Process Spot Lights
        List<SpotLight> spotLightList = sceneLight.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.size() : 0;
        for (int i = 0; i<numLights; i++)
        {
            SpotLight currSpotLight = new SpotLight(spotLightList.get(i));
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f spotLightPos = currSpotLight.getPointLight().getPosition();
            Vector4f auxSpot = new Vector4f(spotLightPos, 1);
            auxSpot.mul(viewMatrix);
            spotLightPos.x = auxSpot.x;
            spotLightPos.y = auxSpot.y;
            spotLightPos.z = auxSpot.z;

            sceneShaderProgram.SetUniform("spotLights", currSpotLight, i);
        }
        //Get a copy of the directional light object and transform its position to view coordinates.
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShaderProgram.SetUniform("directionalLight", currDirLight);
        sceneShaderProgram.SetUniform("texture_sampler", 0);
    }

    private void renderHud(Window window, IHud hud)
    {
        hudShaderProgram.Bind();
        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for(GameItem gameItem : hud.getGameItems()) {
            Mesh mesh = gameItem.getMesh();
            Matrix4f projModelMatrix = transformation.getOrtoProjModelMatrix(gameItem, ortho);
            hudShaderProgram.SetUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.SetUniform("colour", gameItem.getMesh().getMaterial().getAmbientColour());
            hudShaderProgram.SetUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);
            mesh.render();
        }
        hudShaderProgram.UnBind();
    }
    public void cleanup() {
        if(shaderProgram != null)
        {
            shaderProgram.Cleanup();
        }
    }
    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }


}