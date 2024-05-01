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

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !MouseListener.isDragging()) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();

            if (MouseListener.get().isInViewPort()) {
                activeGameObject = currentScene.getGameObject(pickingTexture.readPixel(x, y));
            }
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
}
