package components;

import Ada.Camera;
import Ada.KeyListener;
import Ada.MouseListener;
import org.joml.Vector2f;
import util.Settings;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {

    private int dragDebounce = 2;

    private float lerpTime = 0.0f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        // Scene camera movement for editor
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            this.clickOrigin = MouseListener.getWorld();
            dragDebounce --;
        }
        else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = MouseListener.getWorld();
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.getPosition().sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0 && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 2;
        }

        // Zooming in editor
        if (MouseListener.getScrollY() != 0.0f && MouseListener.get().isInViewPort()) {
            float addvalue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1 / levelEditorCamera.getZoom());
            addvalue *= -Math.signum(MouseListener.getScrollY());

            levelEditorCamera.addZoom(addvalue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_PERIOD)) {
            reset = true;
        }

        if (reset) {
            levelEditorCamera.getPosition().lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() + ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;

            if (Math.abs(levelEditorCamera.getPosition().x) <= Settings.GRID_WIDTH &&
                    Math.abs(levelEditorCamera.getPosition().y) <= Settings.GRID_HEIGHT) {
                this.lerpTime = 0.0f;
                levelEditorCamera.getPosition().set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
