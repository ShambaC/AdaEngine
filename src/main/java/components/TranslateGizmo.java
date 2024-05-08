package components;

import Ada.GameObject;
import Ada.MouseListener;
import Ada.Prefabs;
import editor.PropertiesWindow;

public class TranslateGizmo extends Gizmo{

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }
    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow, Sprite freeObjectSprite) {
        super(arrowSprite, propertiesWindow, freeObjectSprite);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.position.x -= MouseListener.getWorldDx();
            }
            else if(yAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getWorldDy();
            }
            else if(freeMoveActive) {
                activeGameObject.transform.position.x -= MouseListener.getWorldDx();
                activeGameObject.transform.position.y -= MouseListener.getWorldDy();
            }
        }

        super.editorUpdate(dt);
    }
}
