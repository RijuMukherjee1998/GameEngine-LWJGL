package Engine.Mesh;

import Engine.Materials.Material;
import Engine.Textures.Texture;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    //VAO-ID
    private final int vaoId;

    //VBO-ID's
    private final List<Integer> vboIdList;

    private final int vertexCount;

    private Material material;

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        FloatBuffer verticesBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<>();

            //VAO => [Array of VBO's]
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);
            
            //VBO position buffer
            int VboId = glGenBuffers();
            vboIdList.add(VboId);
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, VboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //VBO texture buffer
            VboId = glGenBuffers();
            vboIdList.add(VboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, VboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false,0, 0);

            //VBO normals buffer
            VboId = glGenBuffers();
            vboIdList.add(VboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, VboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 3, GL_FLOAT, false,0, 0);

            //VBO index buffer
            VboId = glGenBuffers();
            vboIdList.add(VboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (verticesBuffer != null)
                MemoryUtil.memFree(verticesBuffer);
            if (indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);
            if (textCoords != null)
                MemoryUtil.memFree(textCoordsBuffer);
            if (vecNormalsBuffer != null)
                MemoryUtil.memFree(vecNormalsBuffer);
        }
    }

    public void render() {
        Texture texture = material.getTexture();
        if(texture != null) {
            //Activate the texture bank
            glActiveTexture(GL_TEXTURE0);
            //Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        //Draw the mesh
        glBindVertexArray(getVaoId());
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        //Restore the state
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Material getMaterial() {
        return material;
    }
    public void setMaterial(Material material) {
        this.material = material;
    }
    public int getVaoId() {
        return vaoId;
    }
    public int getVertexCount() {
        return vertexCount;
    }
    public void cleanUp() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        //Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList)
        {
            glDeleteBuffers(vboId);
        }

        //Delete the texture
        Texture texture = material.getTexture();
        if(texture != null) {
            //Delete the texture
            texture.cleanup();
        }

        //Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        //Delete the VBO's
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for(int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        //Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

}
