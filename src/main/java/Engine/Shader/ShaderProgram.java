package Engine.Shader;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
public class ShaderProgram {

    private final int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception
    {
        programID = glCreateProgram();
        uniforms = new HashMap<>();
        if(programID == 0)
        {
            throw new Exception("Cannot create Shader Program");
        }
    }
     public void CreateVertexShader(String vertexShaderCode) throws Exception {
        vertexShaderID = CreateShader(vertexShaderCode, GL_VERTEX_SHADER);
     }

     public void CreateFragmentShader(String fragmentShaderCode) throws Exception {
        fragmentShaderID = CreateShader(fragmentShaderCode, GL_FRAGMENT_SHADER);
     }

     private int CreateShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if(shaderId == 0) {
            throw new Exception("Error creating shader of type: " +shaderType );
        }
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
        {
            throw new Exception("Error compiling shader of type " + glGetShaderInfoLog(shaderId, 1024));
        }
        glAttachShader(programID, shaderId);

        return shaderId;
     }

     public void CreateUniform(String uniformName) throws Exception {
         int uniformLocation = glGetUniformLocation(programID, uniformName);

         if(uniformLocation < 0)
         {
             throw new Exception("Could not find uniform:" + uniformName);
         }
         uniforms.put(uniformName, uniformLocation);
     }

     public void SetUniform(String uniformName, Matrix4f value) {
        //Dump the matrix into a float buffer
         try(MemoryStack stack = MemoryStack.stackPush()) {
             FloatBuffer fb = stack.mallocFloat(16);
             value.get(fb);
             glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
         }
     }
     public void Link() throws Exception{
        glLinkProgram(programID);

        if(glGetProgrami(programID, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking shader code :" + glGetProgramInfoLog(programID, 1024));
        }

        if(vertexShaderID != 0) {
            glDetachShader(programID, vertexShaderID);
        }
        if(fragmentShaderID != 0) {
            glDetachShader(programID, fragmentShaderID);
        }
        glValidateProgram(programID);
        if(glGetProgrami(programID, GL_VALIDATE_STATUS) == 0)
        {
            System.err.println("Warning validating shader code : " + glGetProgramInfoLog(programID, 1024));
        }
     }

     public void Bind() {
        glUseProgram(programID);
     }

     public void UnBind() {
        glUseProgram(0);
     }

     public void Cleanup() {
        UnBind();
        if(programID != 0)
        {
            glDeleteProgram(programID);
        }
     }
}
