package Ada;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import scenes.Scene;

public class ImGuiLayer {
    private boolean showText = false;

    public void imgui(Scene currentScene) {
        currentScene.sceneImgui();
        ImGui.begin("New Window");

        if (ImGui.button("I am a button")) {
            showText = true;
        }

        if(showText) {
            ImGui.text("You clicked a button");
            ImGui.sameLine();
            if(ImGui.button("Stop showing text")) {
                showText = false;
            }
        }

        ImGui.end();
    }

    public void init() {
        ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();     // natively allocated, must be destroyed

        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontFromFileTTF("assets/fonts/ARLRDBD.ttf", 16, fontConfig);

        fontConfig.destroy();
    }
}
