package components;

import org.joml.Vector2f;
import renderer.Texture;

public class Sprite {

    private float width, height;

    private Texture texture = null;
    private Vector2f[] texCoords = {
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }

    public Sprite setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
        return this;
    }

    public Sprite setTexture(Texture texture) {
        this.texture = texture;
        return this;
    }

    public float getWidth() {
        return width;
    }

    public Sprite setWidth(float width) {
        this.width = width;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public Sprite setHeight(float height) {
        this.height = height;
        return this;
    }

    public int getTexId() {
        return texture == null ? -1 : texture.getTexID();
    }
}
