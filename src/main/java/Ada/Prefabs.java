package Ada;

import components.Sprite;
import components.SpriteRenderer;
import components.Transform;
import org.joml.Vector2f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Sprite Object Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer().setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}
