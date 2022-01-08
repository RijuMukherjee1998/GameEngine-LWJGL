package TestGame;

import Engine.GameObjects.GameItem;
import Engine.GameObjects.OBJLoader;
import Engine.HUD.FontTexture;
import Engine.HUD.IHud;
import Engine.HUD.TextItem;
import Engine.Materials.Material;
import Engine.Mesh.Mesh;
import Engine.Window.Window;
import org.joml.Vector4f;

import java.awt.*;

public class Hud  implements IHud {
    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private final GameItem[] gameItems;

    private final TextItem statusTextItem;

    private final GameItem compassItem;

    public Hud(Window window, String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));

        // Create compass
        Mesh mesh = OBJLoader.loadMesh("C:\\Dev\\GameEngine\\src\\main\\resources\\Models\\compass.obj");
        Material material = new Material();
        material.setAmbientColour(new Vector4f(1f, 1f, 1f, 1));
        mesh.setMaterial(material);
        compassItem = new GameItem(mesh);
        compassItem.setScale(80.0f);
        compassItem.setPosition(window.getWidth() - 30f, 80f, 0);
        // Rotate to transform it to screen coordinates
        compassItem.setRotation(0f, 0f, 180f);

        // Create list that holds the items that compose the HUD
        gameItems = new GameItem[]{statusTextItem,compassItem};
    }
    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    @Override
    public GameItem[] getGameItems() {
        return gameItems;
    }

    public void updateSize(Window window)
    {
        this.statusTextItem.setPosition(10f, window.getHeight() - 100f, 0);
    }
}
