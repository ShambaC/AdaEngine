package Ada;

import imgui.*;
import imgui.flag.*;
import imgui.type.ImBoolean;
import scenes.Scene;

public class ImGuiLayer {
    private boolean showText = false;

    public void imgui(Scene currentScene) {
        currentScene.imgui();
    }

    public void init() {
        ImGui.createContext();
        setStyle();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();     // natively allocated, must be destroyed

        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontFromFileTTF("assets/fonts/ARLRDBD.ttf", 16, fontConfig);

        fontConfig.destroy();
    }

    public void setupDockspace() {
        int windowFlags =
                ImGuiWindowFlags.MenuBar |
                ImGuiWindowFlags.NoDocking |
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus |
                ImGuiWindowFlags.NoNavFocus;

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.get().getWidth(), Window.get().getHeight());

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);

        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));
    }

    private void setStyle() {
        ImGuiStyle style = ImGui.getStyle();

        style.setColor(ImGuiCol.Text, 0.96f, 0.96f, 0.96f, 1.00f);
        style.setColor(ImGuiCol.TextDisabled, 0.50f, 0.50f, 0.50f, 1.00f);
        style.setColor(ImGuiCol.WindowBg, 0.06f, 0.06f, 0.06f, 0.94f);
        style.setColor(ImGuiCol.ChildBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.PopupBg, 0.08f, 0.08f, 0.08f, 0.94f);
        style.setColor(ImGuiCol.Border, 0.66f, 0.66f, 0.66f, 0.51f);
        style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.FrameBg, 0.14f, 0.14f, 0.13f, 1.00f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.23f, 0.24f, 0.21f, 1.00f);
        style.setColor(ImGuiCol.FrameBgActive, 0.40f, 0.44f, 0.46f, 1.00f);
        style.setColor(ImGuiCol.TitleBg, 0.04f, 0.04f, 0.04f, 1.00f);
        style.setColor(ImGuiCol.TitleBgActive, 0.26f, 0.26f, 0.26f, 1.00f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.51f);
        style.setColor(ImGuiCol.MenuBarBg, 0.14f, 0.14f, 0.14f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.53f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.31f, 0.31f, 0.31f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.41f, 0.41f, 0.41f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.51f, 0.51f, 0.51f, 1.00f);
        style.setColor(ImGuiCol.CheckMark, 0.81f, 0.81f, 0.81f, 1.00f);
        style.setColor(ImGuiCol.SliderGrab, 0.53f, 0.53f, 0.53f, 1.00f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.08f, 0.08f, 0.08f, 1.00f);
        style.setColor(ImGuiCol.Button, 0.52f, 0.56f, 0.56f, 0.40f);
        style.setColor(ImGuiCol.ButtonHovered, 0.48f, 0.48f, 0.48f, 1.00f);
        style.setColor(ImGuiCol.ButtonActive, 0.35f, 0.35f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.Header, 0.47f, 0.53f, 0.61f, 0.31f);
        style.setColor(ImGuiCol.HeaderHovered, 0.66f, 0.68f, 0.71f, 0.80f);
        style.setColor(ImGuiCol.HeaderActive, 0.45f, 0.46f, 0.46f, 1.00f);
        style.setColor(ImGuiCol.Separator, 0.93f, 0.93f, 0.96f, 0.50f);
        style.setColor(ImGuiCol.SeparatorHovered, 0.10f, 0.40f, 0.75f, 0.78f);
        style.setColor(ImGuiCol.SeparatorActive, 0.10f, 0.40f, 0.75f, 1.00f);
        style.setColor(ImGuiCol.ResizeGrip, 0.26f, 0.59f, 0.98f, 0.20f);
        style.setColor(ImGuiCol.ResizeGripHovered, 0.26f, 0.59f, 0.98f, 0.67f);
        style.setColor(ImGuiCol.ResizeGripActive, 0.26f, 0.59f, 0.98f, 0.95f);
        style.setColor(ImGuiCol.Tab, 0.28f, 0.28f, 0.27f, 1.00f);
        style.setColor(ImGuiCol.TabHovered, 0.23f, 0.23f, 0.24f, 0.80f);
        style.setColor(ImGuiCol.TabActive, 0.40f, 0.45f, 0.51f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocused, 0.07f, 0.10f, 0.15f, 0.97f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.13f, 0.13f, 0.13f, 1.00f);
        style.setColor(ImGuiCol.PlotLines, 0.61f, 0.61f, 0.61f, 1.00f);
        style.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.TableHeaderBg, 0.19f, 0.19f, 0.20f, 1.00f);
        style.setColor(ImGuiCol.TableBorderStrong, 0.31f, 0.31f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.TableBorderLight, 0.23f, 0.23f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.TableRowBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TableRowBgAlt, 1.00f, 1.00f, 1.00f, 0.06f);
        style.setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f);
        style.setColor(ImGuiCol.DragDropTarget, 1.00f, 1.00f, 0.00f, 0.90f);
        style.setColor(ImGuiCol.NavHighlight, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.NavWindowingHighlight, 1.00f, 1.00f, 1.00f, 0.70f);
        style.setColor(ImGuiCol.NavWindowingDimBg, 0.80f, 0.80f, 0.80f, 0.20f);
        style.setColor(ImGuiCol.ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.35f);
    }
}
