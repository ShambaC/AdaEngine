package editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class JImGui {

    public static void drawVec2Control(String label, Vector2f values) {
        drawVec2Control(label, values, 0.0f);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() / 2.5f);
        ImGui.text(label);
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(label);
            ImGui.endTooltip();
        }
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 1.2f;

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.dragFloat("##x", vecValuesX, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.nextColumn();

        values.x = vecValuesX[0];
        values.y = vecValuesY[0];

        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }

    public static float dragFloat(String label, float value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() / 2.5f);
        ImGui.text(label);
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(label);
            ImGui.endTooltip();
        }
        ImGui.nextColumn();

        float[] valArr = {value};
        ImGui.dragFloat("##dragFloat", valArr, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valArr[0];
    }

    public static float sliderFloat(String label, float value, float minVal, float maxVal) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() / 2.5f);
        ImGui.text(label);
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(label);
            ImGui.endTooltip();
        }
        ImGui.nextColumn();

        float[] valArr = {value};
        ImGui.sliderFloat("##sliderFloat", valArr, minVal, maxVal, "%.3f");

        ImGui.columns(1);
        ImGui.popID();

        return valArr[0];
    }

    public static int dragInt(String label, int value) {
        return dragInt(label, value, label);
    }

    public static int dragInt(String label, int value, String toolTip) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() / 2.5f);
        ImGui.text(label);
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(toolTip);
            ImGui.endTooltip();
        }
        ImGui.nextColumn();

        int[] valArr = {value};
        ImGui.dragInt("##dragint", valArr, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valArr[0];
    }

    public static boolean checkBox(String label, boolean value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() / 2.5f);
        ImGui.text(label);
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(label);
            ImGui.endTooltip();
        }
        ImGui.nextColumn();

        boolean val = value;
        if (ImGui.checkbox("##checkbox", val)) {
            ImGui.columns(1);
            ImGui.popID();
            return !val;
        }

        ImGui.columns(1);
        ImGui.popID();

        return value;
    }

    public static boolean colorPicker4(String label, Vector4f color) {
        boolean res = false;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() / 2.5f);
        ImGui.text(label);
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(label);
            ImGui.endTooltip();
        }
        ImGui.nextColumn();

        float[] imColor = {color.x, color.y, color.z, color.w};
        if (ImGui.colorEdit4("##colorPicker", imColor)) {
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            res = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return res;
    }

    public static boolean comboBox(String label, ImInt index, String[] enumValues) {
        boolean res = false;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() / 2.5f);
        ImGui.text(label);
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(label);
            ImGui.endTooltip();
        }
        ImGui.nextColumn();

        if (ImGui.combo("", index, enumValues, enumValues.length)) {
            res = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return res;
    }

    public static String inputText(String label, String text) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() / 2.5f);
        ImGui.text(label);
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(label);
            ImGui.endTooltip();
        }
        ImGui.nextColumn();

        ImString outString = new ImString(text, 256);
        if(ImGui.inputText("##" + label, outString)) {
            ImGui.columns(1);
            ImGui.popID();

            return outString.get();
        }

        ImGui.columns(1);
        ImGui.popID();

        return text;
    }
}
