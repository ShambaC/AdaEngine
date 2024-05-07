package editor;

import Ada.GameObject;
import Ada.MouseListener;
import imgui.ImGui;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        debounce -= dt;

        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !MouseListener.isDragging() && debounce < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();

            if (MouseListener.get().isInViewPort()) {
                GameObject tempObj = currentScene.getGameObject(pickingTexture.readPixel(x, y));
                // Conditions to not set the gizmo as the active gameobject
                if (activeGameObject != null) {
                    activeGameObject.setIsPicked(false);
                }

                if (tempObj == null) {
                    activeGameObject = null;
                }
                else if (tempObj.isPickable()) {
                    activeGameObject = tempObj;
                    activeGameObject.setIsPicked(true);
                }

            }

            this.debounce = 0.2f;
        }
    }

    public void imgui() {
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
                        if (activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null) {
                            activeGameObject.addComponent(new Box2DCollider());
                        }
                    }

                    if (ImGui.menuItem("Circle Collider")) {
                        if (activeGameObject.getComponent(CircleCollider.class) == null &&
                                activeGameObject.getComponent(Box2DCollider.class) == null) {
                            activeGameObject.addComponent(new CircleCollider());
                        }
                    }

//                    ImGui.endMenu();
                }

                ImGui.endPopup();
            }
        }
        else {
            ImGui.text("Select a gameobject to inspect");
        }
        ImGui.end();
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject go) {
        this.activeGameObject = go;
    }
}
