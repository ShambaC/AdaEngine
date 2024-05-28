package components;

import Ada.GameObject;
import Ada.KeyListener;
import Ada.MouseListener;
import Ada.Window;
import editor.PropertiesWindow;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import renderer.DebugDraw;
import renderer.PickingTexture;
import scenes.Scene;
import util.Settings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObject = null;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    private boolean singlePlace = false;

    public void pickupObject(GameObject go) {
        if (this.holdingObject != null) {
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        holdingObject.setIsPickable(false);
        Window.getScene().addGameObjectToScene(go);
    }

    public void place() {
        GameObject newObj = this.holdingObject.copy();
        if (newObj.getComponent(StateMachine.class) != null) {
            newObj.getComponent(StateMachine.class).refreshTextures();
        }
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        newObj.setIsPickable(true);
        Window.getScene().addGameObjectToScene(newObj);
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PropertiesWindow propertiesWindow = Window.get().getPropertiesWindow();
        PickingTexture pickingTexture = propertiesWindow.getPickingTexture();
        Scene currentScene = Window.getScene();

        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();

        if (holdingObject != null && MouseListener.get().isInViewPort()) {
            holdingObject.transform.position.x = MouseListener.getWorldX();
            holdingObject.transform.position.y = MouseListener.getWorldY();

            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH + (Settings.GRID_WIDTH / 2);
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT + (Settings.GRID_HEIGHT / 2);

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                if (MouseListener.isDragging() && !blockInCell(holdingObject.transform.position.x - halfWidth, holdingObject.transform.position.y - halfHeight)) {
                    place();
                }
                else if (!MouseListener.isDragging() && !singlePlace) {
                    place();
                    singlePlace = true;
                    debounce = debounceTime;
                }
            }

            if (MouseListener.mouseButtonUp(GLFW_MOUSE_BUTTON_LEFT)) {
                singlePlace = false;
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        }
        else if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !MouseListener.isDragging() && debounce < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();

            if (MouseListener.get().isInViewPort()) {
                GameObject tempObj = currentScene.getGameObject(pickingTexture.readPixel(x, y));
                // Conditions to not set the gizmo as the active gameobject
                if (!activeGameObjects.isEmpty() && activeGameObjects.get(0) != null) {
                    for (GameObject gObj: activeGameObjects) {
                        gObj.setIsPicked(false);
                    }
                }

                if (tempObj == null) {
                    propertiesWindow.clearSelected();
                }
                else if (tempObj.isPickable()) {
                    propertiesWindow.setActiveGameObject(tempObj);
                    tempObj.setIsPicked(true);
                }

            }

            this.debounce = 0.2f;
        }
        else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && MouseListener.get().isInViewPort() && !MouseListener.get().isUsingGizmo()) {
            if (!boxSelectSet) {
                Window.get().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D((new Vector2f(boxSelectStartWorld)).add(halfSize), new Vector2f(halfSize).mul(2.0f), 0.0f);
        }
        else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int) boxSelectStart.x;
            int screenStartY = (int) boxSelectStart.y;
            int screenEndX = (int) boxSelectEnd.x;
            int screenEndY = (int) boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }

            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(new Vector2i(screenStartX, screenStartY), new Vector2i(screenEndX, screenEndY));
            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float objId : gameObjectIds) {
                uniqueGameObjectIds.add((int) objId);
            }

            for (Integer gameObjectId : uniqueGameObjectIds) {
                GameObject pickedObject = Window.getScene().getGameObject(gameObjectId);
                if (pickedObject != null && pickedObject.isPickable()) {
                    Window.get().getPropertiesWindow().addActiveGameObject(pickedObject);
                }
            }
        }
    }

    private boolean blockInCell(float x, float y) {
        PropertiesWindow propertiesWindow = Window.get().getPropertiesWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT / 1.2f));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf = MouseListener.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int) startScreenf.x + 2, (int) startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int) endScreenf.x + 2, (int) endScreenf.y + 2);

        float[] gameObjectIds = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < gameObjectIds.length; i++) {
            if (gameObjectIds[i] >= 0) {
                GameObject pickedObject = Window.getScene().getGameObject((int) gameObjectIds[i]);
                if (pickedObject.isPickable()) {
                    return true;
                }
            }
        }

        return false;
    }
}
