package components;

import Ada.GameObject;
import editor.JImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Component is an abstract class from which every other component derives.
 * <p></p>
 * <p>This class defines the essential methods required by every component to function properly.</p>
 */
public abstract class Component {

    /**
     * Maintains a counter to help assign uid
     */
    private static int ID_COUNTER = 0;
    /**
     * Stores a unique ID for every component. Essential for serialization.
     */
    private int uid = -1;

    /**
     * The gameobject this component is attached to
     */
    public transient GameObject gameObject = null;

    /**
     * This method is invoked at the start of a session to perform component specific functions
     */
    public void start() {

    }

    /**
     * This method is called every frame maintaining a fixed time gap between subsequent calls
     * @param dt deltaTime that signifies a synchronised frame time
     */
    public void update(float dt) {}

    /**
     * This method is called every frame maintaining a fixed time gap between subsequent calls. Unlike {@link #update(float) update} method, this method is called while not in play mode.
     * <p>This performs the refresh operation of the editor</p>
     * @param dt deltaTime that signifies a synchronised frame time
     */
    public void editorUpdate(float dt) {}

    /**
     * This method shows the fields of the specific component in the editor GUI.
     * <p></p>
     * <p>It uses reflection to get access to the fields and shows them as editable fields in the GUI and updates their values accordingly</p>
     */
    public void imgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if(isTransient) {
                    continue;
                }
                if(isPrivate) {
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int) value;
                    field.set(this, JImGui.dragInt(name, val));
                }
                else if (type == float.class) {
                    float val = (float) value;
                    field.set(this, JImGui.dragFloat(name, val));
                }
                else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    field.set(this, JImGui.checkBox(name, val));
                }
                else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    JImGui.drawVec2Control(name, val);
                }
                else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                }
                else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                }
                else if (type.isEnum()) {
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum) value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    JImGui.comboBox(field.getName(), index, enumValues);
                    field.set(this, type.getEnumConstants()[index.get()]);
                }

                if(isPrivate) {
                    field.setAccessible(false);
                }
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to generate UID for the component
     */
    public void generateId() {
        if (this.uid == -1) {
            this.uid = ID_COUNTER++;
        }
    }

    /**
     * Helper method to get list of constants from an enum to display in the editor GUI.
     * @param enumType The enum to deserialize
     * @return String list of all the enum values
     */
    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValue : enumType.getEnumConstants()) {
            enumValues[i] = enumIntegerValue.name();
            i++;
        }

        return enumValues;
    }

    /**
     * Helper method to find the index of a string in a string array
     * @param str String to search for
     * @param arr Array to search within
     * @return index of the string
     */
    private int indexOf(String str, String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (str.equals(arr[i])) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Method to destroy the component, ie. to remove it from the gameobject
     */
    public void destroy() {

    }

    /**
     * Method to get the UID of the component
     * @return the UID of this component
     */
    public int getUid() {
        return this.uid;
    }

    /**
     * Method to initialize a component
     * @param maxId
     */
    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }
}
