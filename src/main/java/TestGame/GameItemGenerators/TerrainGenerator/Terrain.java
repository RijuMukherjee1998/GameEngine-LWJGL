package TestGame.GameItemGenerators.TerrainGenerator;

import Engine.GameObjects.GameItem;
import Engine.Mesh.Mesh;

import java.util.List;
import java.util.Vector;

public class Terrain {
    private Mesh mesh;
    private int terrainSize;
    private List<GameItem> cubes;

    public Terrain (Mesh mesh, int terrainSize)
    {
        this.mesh = mesh;
        this.terrainSize = terrainSize;
        cubes = new Vector<GameItem>();
    }

    public List<GameItem> generateTerrain()
    {
        GameItem gameItem;
        float cubeSideLength = 1.0f;
        float x = 0.0f,y = 0.0f,z = -2.0f;
        for(int i = 0; i < terrainSize; i++ )
        {
            for (int j = 0; j < terrainSize; j++)
            {
                gameItem = new GameItem(mesh);
                gameItem.setScale(0.5f);
                x = x + cubeSideLength;
                gameItem.setPosition(x, y, z);
                cubes.add(gameItem);
            }
            x = 0.0f;
            z = z - cubeSideLength;
        }
        return cubes;
    }

}
