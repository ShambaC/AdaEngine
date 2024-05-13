package scenes;

import Ada.*;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import renderer.Texture;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelEditorSceneInitializer extends SceneInitializer {
    private GameObject obj1;
    private Spritesheet sprites;
    private List<String> spriteSheetsPaths = new ArrayList<>();

    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer() {

    }

    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);

        levelEditorStuff.start();
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 84, 0));
        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
        AssetPool.addSpriteSheet("assets/images/items.png",
                new Spritesheet(AssetPool.getTexture("assets/images/items.png"),
                        16, 16, 43, 0));
        AssetPool.addSpriteSheet("assets/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));
        AssetPool.addSpriteSheet("assets/images/spritesheets/plus.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/plus.png"),
                        32, 32, 1, 0));

        for (GameObject g : scene.getGameObjects()) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }

            if (g.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void imgui() {
        ImGui.begin("Engine Settings");
        levelEditorStuff.imgui();
        ImGui.end();

        ImGui.begin("Asset Explorer");

        if (ImGui.beginTabBar("WindowTabBar")) {

            if (ImGui.beginTabItem("Blocks")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 2;
                    float spriteHeight = sprite.getHeight() * 2;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        // Attach this to the mouse cursor
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }

                    if (i == sprites.size() - 1) {
                        Spritesheet plusSignSheet = AssetPool.getSpritesheet("assets/images/spritesheets/plus.png");
                        Sprite spr = plusSignSheet.getSprite(0);
                        int plusId = spr.getTexId();
                        Vector2f[] texCoordsPlus = spr.getTexCoords();

                        if (ImGui.imageButton(plusId, 32, 32,  texCoordsPlus[2].x, texCoordsPlus[0].y, texCoordsPlus[0].x, texCoordsPlus[2].y)) {
                            MouseListener.get().setInPopup(true);
                            ImGuiFileDialog.openModal("sheet_browse", "Choose Spritesheet", ".png", ".", 1, 42, ImGuiFileDialogFlags.None);
                        }
                        if (ImGui.isItemHovered()) {
                            ImGui.beginTooltip();
                            ImGui.text("Add Spritesheets");
                            ImGui.endTooltip();
                        }

                        if (ImGuiFileDialog.display("sheet_browse", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {

                            if (ImGuiFileDialog.isOk()) {
                                Map<String, String> selection = ImGuiFileDialog.getSelection();
                                if (selection != null && !selection.isEmpty()) {
                                    String filePath = selection.values().stream().findFirst().get();
                                    if (!this.spriteSheetsPaths.contains(filePath)) {
                                        this.spriteSheetsPaths.add(filePath);
                                        ImGui.openPopup("SheetModal");
                                    }
                                }
                            }
                            ImGuiFileDialog.close();
                            MouseListener.get().setInPopup(false);
                        }

                        ImBoolean isPopupOpen = new ImBoolean(true);
                        if (ImGui.beginPopupModal("SheetModal", isPopupOpen, ImGuiWindowFlags.AlwaysAutoResize)) {
                            MouseListener.get().setInPopup(true);
                            ImGui.text("New SpriteSheet Wizard");

                            Texture tmpSheet = new Texture();
                            tmpSheet.init(spriteSheetsPaths.get(spriteSheetsPaths.size() - 1));

                            int tmpSheetId = tmpSheet.getTexID();
                            float tmpSheetWidth = tmpSheet.getWidth();
                            float tmpSheetHeight = tmpSheet.getHeight();

                            ImGui.image(tmpSheetId, tmpSheetWidth, tmpSheetHeight);

                            

                            if(ImGui.button("Cancel")) {
                                ImGui.closeCurrentPopup();
                                MouseListener.get().setInPopup(false);
                            }

                            ImGui.endPopup();
                        }
                        if (!isPopupOpen.get())   MouseListener.get().setInPopup(false);
                    }
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Prefabs")) {
                Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 2;
                float spriteHeight = sprite.getHeight() * 2;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generatePlayer();
                    // Attach this to the mouse cursor
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();

                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }

        ImGui.end();
    }
}
