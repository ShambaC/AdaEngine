package components;

import Ada.Component;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Rigidbody extends Component {
    private int colliderType = 0;
    private float friction = 0.8f;
    public Vector3f velocity = new Vector3f(0, 0, 0);
    public transient Vector4f tmp = new Vector4f(0, 0, 0, 0);

    @Override
    public String toString() {
        String res = "Collider type: " + colliderType;
        res += "\nFriction: " + friction;
        res += "\nVelocity: " + velocity.x + " " + velocity.y + " " + velocity.z;

        return res;
    }
}