package scenes;

import Ada.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import components.*;
import editor.JImGui;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import renderer.Texture;
import util.AssetPool;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelEditorSceneInitializer extends SceneInitializer {
    private List<Spritesheet> spriteSheets = new ArrayList<>();
    private Map<String, int[]> spriteSheetCollection = new HashMap<>();

    private GameObject levelEditorStuff;

    private String filePath = "";
    private int sprWidth = 0;
    private int sprHeight = 0;
    private int numSpr = 0;
    private int sprSpace = 0;

    public LevelEditorSceneInitializer() {

    }

    @Override
    public void init(Scene scene) {
        for(String filePath : spriteSheetCollection.keySet()) {
            Spritesheet sprites = AssetPool.getSpritesheet(filePath);
            spriteSheets.add(sprites);
        }
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
        load();

        for(String filePath : spriteSheetCollection.keySet()) {
            int[] sheetData = spriteSheetCollection.get(filePath);
            AssetPool.addSpriteSheet(filePath,
                    new Spritesheet(AssetPool.getTexture(filePath),
                            sheetData[0], sheetData[1], sheetData[2], sheetData[3]));
        }

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

    private void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            FileWriter writer = new FileWriter("levelSprites.txt");
            writer.write(gson.toJson(this.spriteSheetCollection));
            writer.close();
        }
        catch (IOException err) {
            err.printStackTrace();
        }
    }

    private void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("levelSprites.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (!inFile.isBlank() && !inFile.equalsIgnoreCase("[]")) {
            Type type = new TypeToken<Map<String, int[]>>(){}.getType();
            this.spriteSheetCollection = gson.fromJson(inFile, type);
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
                for (int k = 0; k < spriteSheets.size(); k++) {
                    Spritesheet sprites = spriteSheets.get(k);
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
                    }
                }

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
                            filePath = selection.values().stream().findFirst().get();
                            if (!this.spriteSheetCollection.containsKey(filePath)) {
                                ImGui.openPopup("SheetModal");
                            }
                            else {
                                System.out.println("File exists");
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
                    tmpSheet.init(filePath);

                    int tmpSheetId = tmpSheet.getTexID();
                    float tmpSheetWidth = tmpSheet.getWidth();
                    float tmpSheetHeight = tmpSheet.getHeight();

                    ImGui.image(tmpSheetId, tmpSheetWidth * 2, tmpSheetHeight * 2, 0, 1, 1, 0);

                    sprWidth = JImGui.dragInt("Sprite Width", sprWidth);
                    sprHeight = JImGui.dragInt("Sprite Height", sprHeight);
                    numSpr = JImGui.dragInt("NumSprites", numSpr, "Number of sprites in the sheet");
                    sprSpace = JImGui.dragInt("Spacing", sprSpace, "Spacing between sprites");

                    int[] sheetData = {sprWidth, sprHeight, numSpr, sprSpace};

                    if (ImGui.button("OK")) {
                        this.spriteSheetCollection.put(filePath, sheetData);
                         AssetPool.addSpriteSheet(filePath,
                                 new Spritesheet(AssetPool.getTexture(filePath),
                                         sheetData[0], sheetData[1], sheetData[2], sheetData[3]));
                         spriteSheets.add(AssetPool.getSpritesheet(filePath));
                         save();

                         filePath = "";
                         sprWidth = 0;
                         sprHeight = 0;
                         numSpr = 0;
                         sprSpace = 0;

                         ImGui.closeCurrentPopup();
                         MouseListener.get().setInPopup(false);
                    }

                    if (ImGui.button("Cancel")) {
                        filePath = "";
                        sprWidth = 0;
                        sprHeight = 0;
                        numSpr = 0;
                        sprSpace = 0;

                        ImGui.closeCurrentPopup();
                        MouseListener.get().setInPopup(false);
                    }

                    ImGui.endPopup();
                }
                if (!isPopupOpen.get())   MouseListener.get().setInPopup(false);

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
