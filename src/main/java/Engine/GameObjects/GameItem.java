package Engine.GameObjects;

import Engine.Mesh.Mesh;
import org.joml.Vector3f;

public class GameItem {
    private  Mesh mesh;
    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;

    public GameItem(Mesh mesh)
    {
        this.mesh = mesh;
        position = new Vector3f(0, 0 , 0);
        scale = 1;
        rotation = new Vector3f(0,0,0);
    }
    public GameItem() {
        position = new Vector3f();
        scale = 1;
        rotation = new Vector3f();
    }

    public void setPosition(float x, float y, float z)
    {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }

    public float getScale()
    {
        return scale;
    }

    public void setRotation(float x, float y, float z)
    {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Vector3f getRotation()
    {
        return rotation;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    public Mesh getMesh()
    {
        return mesh;
    }


}
