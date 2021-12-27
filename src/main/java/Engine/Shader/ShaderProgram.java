package Engine.Shader;

import Engine.Light.DirectionalLight;
import Engine.Light.PointLight;
import Engine.Light.SpotLight;
import Engine.Materials.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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

     public void CreateDirectionalLightUniform(String uniformName) throws Exception
     {
         CreateUniform(uniformName + ".colour");
         CreateUniform(uniformName + ".direction");
         CreateUniform(uniformName + ".intensity");
     }
     public void CreatePointLightUniform(String uniformName) throws Exception
     {
        CreateUniform(uniformName + ".colour");
        CreateUniform(uniformName + ".position");
        CreateUniform(uniformName + ".intensity");
        CreateUniform(uniformName + ".att.constant");
        CreateUniform(uniformName + ".att.linear");
        CreateUniform(uniformName + ".att.exponent");
     }

     public void CreateSpotLightUniform(String uniformName) throws Exception
     {
         CreatePointLightUniform(uniformName + ".pl");
         CreateUniform(uniformName + ".conedir");
         CreateUniform(uniformName + ".cutoff");
     }

     public void CreateMaterialUniform(String uniformName) throws Exception
     {
         CreateUniform(uniformName + ".ambient");
         CreateUniform(uniformName + ".diffuse");
         CreateUniform(uniformName + ".specular");
         CreateUniform(uniformName + ".hasTexture");
         CreateUniform(uniformName + ".reflectance");
     }

     public void SetUniform(String uniformName, Matrix4f value) {
        //Dump the matrix into a float buffer
         try(MemoryStack stack = MemoryStack.stackPush()) {
             FloatBuffer fb = stack.mallocFloat(16);
             glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(fb));
         }
    }
     public void SetUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
     }
     public void SetUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

     public void SetUniform(String uniformName, Vector3f value)
     {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
     }
    public void SetUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

     public void SetUniform(String uniformName, PointLight pointLight)
     {
         SetUniform(uniformName + ".colour", pointLight.getColor());
         SetUniform(uniformName + ".position", pointLight.getPosition());
         SetUniform(uniformName + ".intensity", pointLight.getIntensity());
         PointLight.Attenuation att = pointLight.getAttenuation();
         SetUniform(uniformName + ".att.constant", att.getConstant());
         SetUniform(uniformName + ".att.linear", att.getLinear());
         SetUniform(uniformName + ".att.exponent", att.getExponent());
     }

     public void SetUniform(String uniformName, SpotLight spotLight)
     {
         SetUniform(uniformName + ".pl", spotLight.getPointLight());
         SetUniform(uniformName + ".conedir", spotLight.getConeDirection());
         SetUniform(uniformName + ".cutoff", spotLight.getCutOff());
     }
     public void SetUniform(String uniformName, Material material)
     {
         SetUniform(uniformName + ".ambient", material.getAmbientColour());
         SetUniform(uniformName + ".diffuse", material.getDiffuseColour());
         SetUniform(uniformName + ".specular", material.getSpecularColour());
         SetUniform(uniformName + ".hasTexture", material.isTextured() ? 1:0);
         SetUniform(uniformName + ".reflectance", material.getReflectance());
     }
     public void SetUniform(String uniformName, DirectionalLight dirLight) {
        SetUniform(uniformName + ".colour" ,dirLight.getColor());
        SetUniform(uniformName + ".direction", dirLight.getDirection());
        SetUniform(uniformName + ".intensity", dirLight.getIntensity());
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
