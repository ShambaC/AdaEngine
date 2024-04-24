package Ada;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.DebugDraw;
import renderer.Framebuffer;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import util.Time;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private Framebuffer framebuffer;

    public float r, g, b, a;

    private static Window window = null;

    private static Scene currentScene;

    private final ImGuiImplGlfw ImGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 ImGuiGl3 = new ImGuiImplGl3();

    private String glslVersion = null;

    private Window() {
        this.width = 640;
        this.height = 480;
        this.title = "AdaEngine";
        this.imguiLayer = new ImGuiLayer();

        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get() {
        if(Window.window == null) {
            Window.window = new Window();
        }

        return  Window.window;
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public void run() {
        System.out.println("LWGJL: " + Version.getVersion());

        init();
        loop();

        ImGuiGl3.dispose();
        ImGuiGlfw.dispose();
        ImGui.destroyContext();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate the GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if(!glfwInit()) {
            throw  new IllegalStateException(("Unable to initialize GLFW"));
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if(glfwWindow == NULL) {
            throw new IllegalStateException(("Failed to create GLFW Window"));
        }

        // Set up mouse callback event
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        // Set up keylisteners
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Set up gamepad listener
        glfwSetJoystickCallback(GamePadListener::GamePadCallback);

        // Set up window size callback
        glfwSetWindowSizeCallback(glfwWindow, WindowListener::windowCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Update variables after initial maximise
        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowSize(glfwWindow, w, h);
        this.width = w[0];
        this.height = h[0];

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // ImGui init
        imguiLayer.init();
        ImGuiGlfw.init(glfwWindow, true);
        ImGuiGl3.init(glslVersion);

        // Enable alpha blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Framebuffer
        this.framebuffer = new Framebuffer(1920, 1080);

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            DebugDraw.beginFrame();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            this.framebuffer.bind();
            if(dt >= 0) {
                DebugDraw.draw();
                currentScene.update(dt);
            }
            this.framebuffer.unbind();

            ImGuiGlfw.newFrame();
            ImGui.newFrame();
            imguiLayer.imgui(currentScene);
            ImGui.render();
            ImGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupWindowPtr);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

        currentScene.saveExit();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
