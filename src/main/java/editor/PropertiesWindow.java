package editor;

import Ada.GameObject;
import Ada.MouseListener;
import imgui.ImGui;
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
                activeGameObject = currentScene.getGameObject(pickingTexture.readPixel(x, y));
            }

            this.debounce = 0.2f;
        }
    }

    public void imgui() {
        ImGui.begin("Inspector");
        if (activeGameObject != null) {
            activeGameObject.imgui();
        }
        else {
            ImGui.text("Select a gameobject to inspect");
        }
        ImGui.end();
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }
}
