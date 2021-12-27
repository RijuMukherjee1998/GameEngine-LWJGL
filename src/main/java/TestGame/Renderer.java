package TestGame;
import Engine.Camera.Camera;
import Engine.GameObjects.GameItem;
import Engine.Light.DirectionalLight;
import Engine.Light.PointLight;
import Engine.Light.SpotLight;
import Engine.Mesh.Mesh;
import Engine.Shader.ShaderProgram;
import Engine.Transformations.Transformation;
import Engine.Utils.LoadResource;
import Engine.Window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private Transformation transformation;
    private ShaderProgram shaderProgram;
    private float specularPower;

    public Renderer() {

        transformation = new Transformation();
        specularPower = 10.0f;
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.CreateVertexShader(LoadResource.loadResource("C:\\Dev\\GameEngine\\src\\main\\resources\\Shaders\\vertex.vs.glsl"));
        shaderProgram.CreateFragmentShader(LoadResource.loadResource("C:\\Dev\\GameEngine\\src\\main\\resources\\Shaders\\fragment.fs.glsl"));
        shaderProgram.Link();

        shaderProgram.CreateUniform("projectionMatrix");
        shaderProgram.CreateUniform("modelViewMatrix");
        shaderProgram.CreateUniform("texture_sampler");

        //Create Uniform for Materials
        shaderProgram.CreateMaterialUniform("material");
        //Create lighting related uniforms
        shaderProgram.CreateUniform("specularPower");
        shaderProgram.CreateUniform("ambientLight");
        shaderProgram.CreatePointLightUniform("pointLight");
        shaderProgram.CreateSpotLightUniform("spotLight");
        shaderProgram.CreateDirectionalLightUniform("directionalLight");

    }

    public void render(Window window, Camera camera, GameItem[] gameItems, Vector3f ambientLight,
                       PointLight pointLight, SpotLight spotLight, DirectionalLight directionalLight)
    {
        clear();

        if(window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        shaderProgram.Bind();
        //Update the Projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(
                FOV,
                window.getWidth(),
                window.getHeight(),
                Z_NEAR, Z_FAR);
        shaderProgram.SetUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        // Update Light Uniforms
        shaderProgram.SetUniform("ambientLight", ambientLight);
        shaderProgram.SetUniform("specularPower", specularPower);

        // Get a copy of the light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.SetUniform("pointLight", currPointLight);

        // Get a copy of the light object and transform its position to view coordinates
        SpotLight currSpotLight = new SpotLight(spotLight);
        Vector4f dir = new Vector4f(currSpotLight.getConeDirection(),0);
        dir.mul(viewMatrix);
        currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

        Vector3f spotLightPos = currSpotLight.getPointLight().getPosition();
        Vector4f auxSpot = new Vector4f(spotLightPos, 1);
        auxSpot.mul(viewMatrix);
        spotLightPos.x = auxSpot.x;
        spotLightPos.y = auxSpot.y;
        spotLightPos.z = auxSpot.z;
        shaderProgram.SetUniform("spotLight", currSpotLight);

        //Get a copy of the directional light object and transform its position to view coordinates.
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.SetUniform("directionalLight", currDirLight);
        shaderProgram.SetUniform("texture_sampler", 0);

        for(GameItem gameItem : gameItems) {
            //Set world matrix for this item
            Mesh mesh = gameItem.getMesh();
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.SetUniform("modelViewMatrix", modelViewMatrix);
            shaderProgram.SetUniform("material", mesh.getMaterial());
            //Render the mesh for this game items.
            mesh.render();
        }

        shaderProgram.UnBind();
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