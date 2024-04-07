package Ada;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[350];

    private KeyListener() {}

    public static KeyListener get() {
        if(instance == null) {
            instance = new KeyListener();
        }

        return instance;
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int mods) {
        if(key < 350 && key >= 0) {
            if (action == GLFW_PRESS) {
                get().keyPressed[key] = true;
            } else if (action == GLFW_RELEASE) {
                get().keyPressed[key] = false;
            }
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return keyCode < 350 && keyCode >= 0 && get().keyPressed[keyCode];
    }
}
