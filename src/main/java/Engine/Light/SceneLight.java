package Engine.Light;

import org.joml.Vector3f;

import java.util.List;

public class SceneLight {
    private Vector3f ambientLight;

    private List<PointLight> pointLightList;

    private List<SpotLight> spotLightList;

    private DirectionalLight directionalLight;

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public List<PointLight> getPointLightList() {
        return pointLightList;
    }

    public void setPointLightList(List<PointLight> pointLightList) {
        this.pointLightList = pointLightList;
    }

    public List<SpotLight> getSpotLightList() {
        return spotLightList;
    }

    public void setSpotLightList(List<SpotLight> spotLightList) {
        this.spotLightList = spotLightList;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }
}
