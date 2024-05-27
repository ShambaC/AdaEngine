package editor;

import Ada.GameObject;
import Ada.MouseListener;
import imgui.ImGui;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import renderer.PickingTexture;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private List<GameObject> activeGameObjects;

    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
    }

    public void imgui() {
        if (activeGameObjects.size() == 1 &&  activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
        }
        ImGui.begin("Inspector");
        if (activeGameObject != null) {
            activeGameObject.imgui();

            ImGui.separator();
            ImGui.setCursorPos(0, ImGui.getWindowHeight() - 30);

            if (ImGui.button("Add Component", ImGui.getWindowWidth(), 30)) {
                ImGui.openPopup("ComponentMenu");
            }

            ImGui.setNextWindowSize(ImGui.getWindowWidth(), ImGui.getWindowHeight() / 2.5f);

            if (ImGui.beginPopup("ComponentMenu")) {
                if (ImGui.collapsingHeader("Physics")) {
                    if (ImGui.menuItem("Rigidbody")) {
                        if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
                            activeGameObject.addComponent(new Rigidbody2D());
                        }
                    }

                    if (ImGui.menuItem("Box Collider")) {
                        if (activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null) {
                            activeGameObject.addComponent(new Box2DCollider());
                        }
                    }

                    if (ImGui.menuItem("Circle Collider")) {
                        if (activeGameObject.getComponent(CircleCollider.class) == null && activeGameObject.getComponent(Box2DCollider.class) == null) {
                            activeGameObject.addComponent(new CircleCollider());
                        }
                    }

                }

                ImGui.endPopup();
            }
        } else {
            ImGui.text("Select a gameobject to inspect");
        }
        ImGui.end();
    }

    public GameObject getActiveGameObject() {
        return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null;
    }

    public List<GameObject> getActiveGameObjects() {
        return activeGameObjects;
    }

    public void clearSelected() {
        this.activeGameObjects.clear();
        activeGameObject = null;
    }

    public void setActiveGameObject(GameObject go) {
        if (go != null) {
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }

    public void addActiveGameObject(GameObject go) {
        this.activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}
