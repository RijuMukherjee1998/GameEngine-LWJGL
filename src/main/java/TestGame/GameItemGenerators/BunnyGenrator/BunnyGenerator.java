package TestGame.GameItemGenerators.BunnyGenrator;

import Engine.GameObjects.GameItem;
import Engine.Materials.Material;
import Engine.Mesh.Mesh;
import org.joml.Vector4f;

import java.util.List;
import java.util.Random;
import java.util.Vector;

public class BunnyGenerator {
    private Mesh mesh;
    private int terrainSize;
    private List<GameItem> bunnies;
    Vector4f color;
    Material materialBunny;
    float reflectance = 1.0f;
    public BunnyGenerator (Mesh rawMesh, int terrainSize)
    {
        this.mesh = rawMesh;
        this.terrainSize = terrainSize;
        bunnies = new Vector<GameItem>();
    }

    public List<GameItem> generateBunnies() {
        GameItem gameItem;
        float x = 0.0f,y = 0.55f,z = -2.0f;
        for(int i = 0; i < terrainSize; i++ )
        {
            for (int j = 0; j < terrainSize; j++)
            {
                color = new Vector4f(Math.abs(z)* 0.1f, Math.abs(z )* 0.2f, Math.abs(z) * 0.2f, 1.0f);
                materialBunny = new Material(color,reflectance);
                mesh.setMaterial(materialBunny);
                gameItem = new GameItem(mesh);
                gameItem.setScale(0.1f);
                x = x + 1;
                gameItem.setPosition(x, y, z);
                bunnies.add(gameItem);
            }
            x = 0.0f;
            z = z - 1;
        }
        return bunnies;
    }
}
