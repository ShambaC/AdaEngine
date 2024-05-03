package components;

import Ada.KeyListener;
import Ada.Window;

import static org.lwjgl.glfw.GLFW.*;

public class GizmoSystem extends Component {
    private Spritesheet gizmos;
    private int usingGizmo = 0;

    public GizmoSystem(Spritesheet gizmoSprites) {
        this.gizmos = gizmoSprites;
    }

    public void init() {
        gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1), Window.get().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.get().getPropertiesWindow()));
    }

    @Override
    public void update(float dt) {
        if (usingGizmo == 0) {
            gameObject.getComponent(TranslateGizmo.class).setUsing(true);
            gameObject.getComponent(ScaleGizmo.class).setUsing(false);
        }
        else if (usingGizmo == 1) {
            gameObject.getComponent(TranslateGizmo.class).setUsing(false);
            gameObject.getComponent(ScaleGizmo.class).setUsing(true);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            usingGizmo = 0;
        }
        else if(KeyListener.isKeyPressed(GLFW_KEY_R)) {
            usingGizmo = 1;
        }
    }
}
