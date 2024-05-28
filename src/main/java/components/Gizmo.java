package components;

import Ada.*;
import editor.PropertiesWindow;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component {
    private Vector4f xAxisColor = new Vector4f(0.75f, 0, 0, 1);
    private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
    private Vector4f yAxisColor = new Vector4f(0, 0.75f, 0, 1);
    private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);
    private Vector4f freeMoveColor = new Vector4f(0.17f, 0.87f, 0.25f, 1);
    private Vector4f freeMoveColorHover = new Vector4f(0.12f, 0.63f, 0.18f, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private GameObject freeMoveObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    private SpriteRenderer freeMoveSprite;

    protected GameObject activeGameObject = null;

    private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6f / 80f);
    private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);
    private Vector2f freeObjOffset = new Vector2f(0.2f, 0.1f);

    private float gizmoWidth = 16f / 80f;
    private float gizmoHeight = 48f / 80f;
    private float freeMoveSize = 24f / 100f;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;
    protected boolean freeMoveActive = false;

    private boolean isUsing = false;

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.freeMoveObject = null;
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.freeMoveSprite = null;
        this.propertiesWindow = propertiesWindow;

        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);
    }

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow, Sprite freeMoveSprite) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.freeMoveObject = Prefabs.generateSpriteObject(freeMoveSprite, freeMoveSize, freeMoveSize * 2);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.freeMoveSprite = this.freeMoveObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);
        Window.getScene().addGameObjectToScene(this.freeMoveObject);
    }

    @Override
    public void start() {
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.setNoSerialize();
        this.xAxisObject.setIsPickable(false);
        this.yAxisObject.setNoSerialize();
        this.yAxisObject.setIsPickable(false);

        if (freeMoveObject != null) {
            this.freeMoveObject.transform.zIndex = 100;
            this.freeMoveObject.setNoSerialize();
            this.freeMoveObject.setIsPickable(false);
        }
    }

    @Override
    public void update(float dt) {
        if (isUsing) {
            this.setInactive();
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (!isUsing) {
            this.setInactive();
            return;
        }

        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObject != null) {
            this.setActive();
        }
        else {
            this.setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();
        boolean freeHot = checkFreeHoverState();

        if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !yAxisActive && !freeMoveActive) {
            xAxisActive = true;
        }
        else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !xAxisActive && !freeMoveActive) {
            yAxisActive = true;
        }
        else if ((freeHot || freeMoveActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !xAxisActive && !yAxisActive) {
            freeMoveActive = true;
        }
        else {
            freeMoveActive = false;
            xAxisActive = false;
            yAxisActive = false;
        }

        MouseListener.get().setUsingGizmo(freeHot || xAxisHot || yAxisHot);

        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);

            if (freeMoveObject != null) {
                this.freeMoveObject.transform.position.set(this.activeGameObject.transform.position);
                this.freeMoveObject.transform.position.add(this.freeObjOffset);
            }
        }
    }

    private void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);

        if (freeMoveObject != null) {
            this.freeMoveSprite.setColor(freeMoveColor);
        }
    }

    private void setInactive() {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
        if (freeMoveObject != null) {
            this.freeMoveSprite.setColor(new Vector4f(0, 0, 0, 0));
        }
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoHeight / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoWidth / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    private boolean checkFreeHoverState() {
        if (freeMoveObject == null) {
            return false;
        }

        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= freeMoveObject.transform.position.x + (freeMoveSize / 2.0f) &&
                mousePos.x >= freeMoveObject.transform.position.x - (freeMoveSize / 2.0f) &&
                mousePos.y <= freeMoveObject.transform.position.y + (freeMoveSize * 2 / 2.0f) &&
                mousePos.y >= freeMoveObject.transform.position.y - (freeMoveSize / 4.0f)) {
            freeMoveSprite.setColor(freeMoveColorHover);
            return true;
        }

        freeMoveSprite.setColor(freeMoveColor);
        return false;
    }

    public void setUsing(boolean state) {
        this.isUsing = state;
    }
}
