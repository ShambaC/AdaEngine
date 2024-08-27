package physics2d.components;


import components.Component;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class CircleCollider extends Component {
    private float radius = 1f;
    private Vector2f offset = new Vector2f();

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    @Override
    public void editorUpdate(float dt) {
        if (gameObject != null && gameObject.isPicked()) {
            Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
            DebugDraw.addCircle(center, this.radius);
        }
    }
}
