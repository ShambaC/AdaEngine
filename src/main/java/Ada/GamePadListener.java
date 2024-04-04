package Ada;

import org.lwjgl.glfw.GLFWGamepadState;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Utility class for listening to GamePad events
 */
public class GamePadListener {
    private static GamePadListener instance;

    private static boolean isConnected;
    private static GLFWGamepadState state;
    private static int id;

    /**
     * Private constructor for GamePadListener class
     * <p>Initializes with a joystick if one is already connected</p>
     */
    private GamePadListener() {
        GamePadListener.isConnected = false;
        GamePadListener.state = GLFWGamepadState.malloc();

        if(glfwJoystickPresent(GLFW_JOYSTICK_1)) {
            System.out.println("GamePad already connected");
            if(glfwJoystickIsGamepad(GLFW_JOYSTICK_1)) {
                GamePadListener.id = GLFW_JOYSTICK_1;
                GamePadListener.isConnected = true;
                glfwGetGamepadState(GLFW_JOYSTICK_1, GamePadListener.state);
            }
        }
    }

    /**
     * Static get method for GamePadListener singleton
     * @return GamePadListener instance
     */
    public static GamePadListener get() {
        if(instance == null) {
            instance = new GamePadListener();
        }

        return  instance;
    }

    /**
     * GamePad Callback method to assign new connected controllers
     * @param id The controller id
     * @param event connection event
     */
    public static void GamePadCallback(int id, int event) {
        if(event == GLFW_CONNECTED) {
            if(glfwJoystickIsGamepad(id)) {
                System.err.println("GamePad Connected");
                get().isConnected = true;
                get().id = id;
            }
        }
        else if(event == GLFW_DISCONNECTED) {
            System.err.println("GamePad disconnected");
        }
    }

    // Methods to get button states of the XBOX 360 gamepad
    public static boolean getA() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_A) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getB() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_B) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getX() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_X) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getY() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_Y) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getLB() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getRB() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getBack() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_BACK) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getStart() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_START) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getHome() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_GUIDE) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getL3() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getR3() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB) == GLFW_PRESS;
        }

        return false;
    }
    public static boolean getDLeft() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getDUp() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getDRight() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW_PRESS;
        }
        return false;
    }
    public static boolean getDDown() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return state.buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN) == GLFW_PRESS;
        }
        return false;
    }

    // Get axis data
    public static float getLTAxis() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return  state.axes(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);
        }
        return 0.0f;
    }
    public static float getRTAxis() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return  state.axes(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
        }
        return 0.0f;
    }
    public static float getLeftStickX() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return  state.axes(GLFW_GAMEPAD_AXIS_LEFT_X);
        }
        return 0.0f;
    }
    public static float getLeftStickY() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return  state.axes(GLFW_GAMEPAD_AXIS_LEFT_Y);
        }
        return 0.0f;
    }
    public static float getRightStickX() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return  state.axes(GLFW_GAMEPAD_AXIS_RIGHT_X);
        }
        return 0.0f;
    }
    public static float getRightStickY() {
        if(isConnected) {
            glfwGetGamepadState(id, state);
            return  state.axes(GLFW_GAMEPAD_AXIS_RIGHT_Y);
        }
        return 0.0f;
    }
}
