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
                activeGameObject.transform.position.x -= MouseListener.getWorldX();
            }
            else if(yAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getWorldY();
            }
            else if(freeMoveActive) {
                activeGameObject.transform.position.x -= MouseListener.getWorldX();
                activeGameObject.transform.position.y -= MouseListener.getWorldY();
            }
        }

        super.editorUpdate(dt);
    }
}
