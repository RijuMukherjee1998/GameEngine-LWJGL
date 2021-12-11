package Engine.Mesh;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    //VAO-ID
    private final int vaoId;

    //VBO-ID's
    private final int posVboId;
    private final int idxVboId;
    private final int colorVboId;

    private final int vertexCount;

    public Mesh(float[] positions,  float[] colors, int[] indices) {
        FloatBuffer verticesBuffer = null;
        FloatBuffer colorsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();

            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();

            colorsBuffer = MemoryUtil.memAllocFloat(colors.length);
            colorsBuffer.put(colors).flip();

            //VAO => [Array of VBO's]
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //VBO position buffer
            posVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //VBO color buffer
            colorVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
            glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, false,0, 0);

            //VBO index buffer
            idxVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (verticesBuffer != null)
                MemoryUtil.memFree(verticesBuffer);
            if (indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);
            if (colorsBuffer != null)
                MemoryUtil.memFree(colorsBuffer);
        }
    }

    public void render() {
        //Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        //Restore the state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
    public int getVaoId() {
        return vaoId;
    }
    public int getVertexCount() {
        return vertexCount;
    }
    public void cleanUp() {
        glDisableVertexAttribArray(0);

        //Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(posVboId);
        glDeleteBuffers(idxVboId);
        glDeleteBuffers(colorVboId);

        //Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

    }

}
