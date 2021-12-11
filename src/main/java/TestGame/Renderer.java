package TestGame;
import Engine.GameObjects.GameItem;
import Engine.Mesh.Mesh;
import Engine.Shader.ShaderProgram;
import Engine.Transformations.Transformation;
import Engine.Utils.LoadResource;
import Engine.Window.Window;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    ShaderProgram shaderProgram;

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private Matrix4f projectionMatrix;
    private Transformation transformation;

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();

        shaderProgram.CreateVertexShader(LoadResource.loadResource("C:\\Dev\\GameEngine\\src\\main\\resources\\vertex.vs.glsl"));
        shaderProgram.CreateFragmentShader(LoadResource.loadResource("C:\\Dev\\GameEngine\\src\\main\\resources\\fragment.fs.glsl"));
        shaderProgram.Link();
        shaderProgram.CreateUniform("projectionMatrix");
        shaderProgram.CreateUniform("worldMatrix");
        window.setClearColor(0.0f, 0.0f, 0.0f,0.0f);
    }

    public void render(Window window, GameItem[] gameItems)
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

        for(GameItem gameItem : gameItems) {
            //Set world matrix for this item
            Matrix4f worldMatrix =
                    transformation.getWorldMatrix(
                            gameItem.getPosition(),
                            gameItem.getRotation(),
                            gameItem.getScale()
                    );
            shaderProgram.SetUniform("worldMatrix", worldMatrix);
            //Render the mesh for this game items.
            gameItem.getMesh().render();
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